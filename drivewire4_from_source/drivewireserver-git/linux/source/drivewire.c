#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#if !defined(__sun)
#include <stdint.h>
#endif
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include <time.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <time.h>
#include <termios.h>
#include <sys/ioctl.h>
#include <curses.h>
#include <pthread.h>


 	
#define	REV_MAJOR	3
#define	REV_MINOR	0

#if defined(__APPLE__) || defined(__sun)
#define	FD		_file
#else
#define __LIL_ENDIAN__
#define	FD		_fileno
#endif

/* Operation Codes */
#define		OP_NOP		0
#define		OP_GETSTAT	'G'
#define		OP_SETSTAT	'S'
#define		OP_READ		'R'
#define		OP_READEX	'R'+128
#define		OP_WRITE	'W'
#define		OP_REREAD	'r'
#define		OP_REREADEX	'r'+128
#define		OP_REWRITE	'w'
#define		OP_INIT		'I'
#define		OP_TERM		'T'
#define		OP_TIME		'#'
#define		OP_RESET2	0xFE
#define		OP_RESET1	0xFF
#define		OP_PRINT	'P'
#define		OP_PRINTFLUSH	'F'
#define     OP_VPORT_READ    'C'


struct dwTransferData
{
	int		dw_protocol_vrsn;
	FILE		*devpath;
	FILE		*dskpath[4];
	int		cocoType;
	int		baudRate;
	unsigned char	lastDrive;
	uint32_t	readRetries;
	uint32_t	writeRetries;
	uint32_t	sectorsRead;
	uint32_t	sectorsWritten;
	unsigned char	lastOpcode;
	unsigned char	lastLSN[3];
	unsigned char	lastSector[256];
	unsigned char	lastGetStat;
	unsigned char	lastSetStat;
	uint16_t	lastChecksum;
	unsigned char	lastError;
	FILE	*prtfp;
	unsigned char	lastChar;
	char	prtcmd[80];
};


int readSector(struct dwTransferData *dp);
int writeSector(struct dwTransferData *dp);
int seekSector(struct dwTransferData *dp, int sector);
void DoOP_INIT(struct dwTransferData *dp);
void DoOP_TERM(struct dwTransferData *dp);
void DoOP_RESET(struct dwTransferData *dp);
void DoOP_READ(struct dwTransferData *dp, char *logStr);
void DoOP_REREAD(struct dwTransferData *dp, char *logStr);
void DoOP_READEX(struct dwTransferData *dp, char *logStr);
void DoOP_REREADEX(struct dwTransferData *dp, char *logStr);
void DoOP_WRITE(struct dwTransferData *dp, char *logStr);
void DoOP_REWRITE(struct dwTransferData *dp, char *logStr);
void DoOP_GETSTAT(struct dwTransferData *dp);
void DoOP_SETSTAT(struct dwTransferData *dp);
void DoOP_TERM(struct dwTransferData *dp);
void DoOP_TIME(struct dwTransferData *dp);
void DoOP_PRINT(struct dwTransferData *dp);
void DoOP_PRINTFLUSH(struct dwTransferData *dp);
void DoOP_VPORT_READ(struct dwTransferData *dp);
char *getStatCode(int statcode);
void WinInit(void);
void WinSetup(WINDOW *window);
void WinUpdate(WINDOW *window, struct dwTransferData *dp);
void WinTerm(void);
uint16_t computeChecksum(u_char *data, int numbytes);
uint16_t computeCRC(u_char *data, int numbytes);
int comOpen(struct dwTransferData *dp, const char *device);
void comRaw(struct dwTransferData *dp);
int comRead(struct dwTransferData *dp, void *data, int numbytes);
int comWrite(struct dwTransferData *dp, void *data, int numbytes);
int comClose(struct dwTransferData *dp);
unsigned int int4(u_char *a);
unsigned int int3(u_char *a);
unsigned int int2(u_char *a);
unsigned int int1(u_char *a);
void _int2(uint16_t a, u_char *b);
int loadPreferences(struct dwTransferData *datapack);
int savePreferences(struct dwTransferData *datapack);
void openDSK(struct dwTransferData *dp, int which);
void closeDSK(struct dwTransferData *dp, int which);
void *CoCoProcessor(void *dp);
void prtOpen(struct dwTransferData *dp);
void prtClose(struct dwTransferData *dp);
void logOpen(void);
void logClose(void);
void logHeader(void);
void setCoCo(struct dwTransferData* datapack, int cocoType);

char device[256];
char dskfile[4][256];
int maxy, maxx;
int updating = 0;
int thread_dead = 0;
FILE *logfp = NULL;

WINDOW *window0, *window1, *window2, *window3;


void sighandler(int signum)
{
	switch (signum)
	{
		case SIGUSR1:
			thread_dead = 1;
			pthread_exit(NULL);
	}
}


int comOpen(struct dwTransferData *dp, const char *device)
{
	char devname[128];


	sprintf(devname, "/dev/%s", device);

	/* Open serial port. */

//	dp->devpath = open(devname, S_IREAD | S_IWRITE);
	dp->devpath = fopen(devname, "a+");

	if (dp->devpath != NULL)
	{
		return 0;
	}

	return -1;
}


int comClose(struct dwTransferData *dp)
{
	close(dp->devpath->FD);

	return 0;
}

void setCoCo(struct dwTransferData *dp, int cocoType)
{
	dp->cocoType = cocoType;
	switch (cocoType)
	{
		case 3:
			switch (dp->dw_protocol_vrsn)
			{
				case 3:
					dp->baudRate = B115200;
					break;
				default:
					dp->baudRate = B57600;
					break;
			}
			break;

		default:
			switch (dp->dw_protocol_vrsn)
			{
				case 3:
					dp->baudRate = B57600;
					break;
				case 2:
					dp->baudRate = B38400;
					break;
			}
			break;

	}

}

int main(void)
{
        struct dwTransferData datapack;
	int i;
	pthread_t thread_id;
	int quitter = 0;


	signal(SIGUSR1, sighandler);

	datapack.dw_protocol_vrsn = 2;

	if (loadPreferences(&datapack) != 0)
	{
#if defined(__APPLE__)
		strcpy(device, "ttysc");
#elif defined(__sun)
		strcpy(device, "ttya");
#else
		strcpy(device, "ttyS0");
#endif
		strcpy(dskfile[0], "disk0");
		strcpy(dskfile[1], "disk1");
		strcpy(dskfile[2], "disk2");
		strcpy(dskfile[3], "disk3");
		setCoCo(&datapack, 3); // assume CoCo 3
		// change EOLs and send to printer
		strcpy(datapack.prtcmd, "| tr \"\\r\" \"\\n\" | lpr");
		// change EOLs and move to file
		// strcpy(datapack.prtcmd, "| tr \"\\r\" \"\\n\" > printeroutput.txt");
	}

	
	if (comOpen(&datapack, device) < 0)
	{
		fprintf(stderr, "Couldn't open %s (error %d)\n", device, errno);
		
		exit(0);
	}
	
	openDSK(&datapack, 0);
	openDSK(&datapack, 1);
	openDSK(&datapack, 2);
	openDSK(&datapack, 3);
	prtOpen(&datapack);
	logOpen();
	
	DoOP_RESET(&datapack);

	comRaw(&datapack);

	WinInit();

	loadPreferences(&datapack);

	pthread_create(&thread_id, NULL, CoCoProcessor, (void *)&datapack);

	while (quitter == 0)
	{
		char c;

		noecho();

		c = tolower(wgetch(window0));

		echo();

		switch(c)
		{
			case 'q':
				quitter = 1;
				break;

			case 'r':
				DoOP_RESET(&datapack);
				WinUpdate(window0, &datapack);
				break;

			case 'm':
				if (datapack.dw_protocol_vrsn == 1)
				{
					datapack.dw_protocol_vrsn = 2;
				}
				else if (datapack.dw_protocol_vrsn == 2)
				{
					datapack.dw_protocol_vrsn = 3;
				}
				else
				{
					datapack.dw_protocol_vrsn = 1;
				}
				WinUpdate(window0, &datapack);
				break;

			case 'c':
				if (datapack.cocoType == 3)
				{
					setCoCo(&datapack, 2);
				}
				else
				{
					setCoCo(&datapack, 3);
				}
				comRaw(&datapack);
				WinUpdate(window0, &datapack);
				break;

			case '0':
			case '1':
			case '2':
			case '3':
				{
					int which = c-'0';
					char tmpfile[128];

					updating = 1;
					wmove(window0, 18+which, 20);
					wclrtoeol(window0);
					wgetstr(window0, tmpfile);

					if (tmpfile[0] != '\0')
					{
						strcpy(dskfile[which], tmpfile);
						closeDSK(&datapack, which);
						openDSK(&datapack, which);
					}
					updating = 0;
					WinUpdate(window0, &datapack);
					break;
				}

			case 'p':
				{
					char newSerial[128];
					struct dwTransferData temp;

					updating = 1;

					wmove(window0, 14, 20);
					wclrtoeol(window0);
					wgetstr(window0, newSerial);
					if (comOpen(&temp, newSerial)== 0)
					{
						comClose(&datapack);
						datapack.devpath = temp.devpath;
						strcpy(device, newSerial);
						comRaw(&datapack);
					}

					updating = 0;
					WinUpdate(window0, &datapack);
					break;
				}

			case 'l':
				{
					char newCmd[80];
					updating = 1;

					wmove(window0, 16, 20);
					wclrtoeol(window0);
					wgetstr(window0, newCmd);
					if (newCmd[0] != '\0')
						strcpy(datapack.prtcmd, newCmd);

					updating = 0;
					WinUpdate(window0, &datapack);
					break;
				}
		}
	}

	pthread_kill(thread_id, SIGUSR1);

	/* Wait for thread to die. */
	while (thread_dead == 0);

	closeDSK(&datapack, 0);
	closeDSK(&datapack, 1);
	closeDSK(&datapack, 2);
	closeDSK(&datapack, 3);
	prtClose(&datapack);
	logClose();

	savePreferences(&datapack);

	comClose(&datapack);

	WinTerm();
}


void openDSK(struct dwTransferData *dp, int which)
{
	dp->dskpath[which] = fopen(dskfile[which], "r+");

	if (dp->dskpath[which] == NULL)
	{
		strcpy(dskfile[which], "");
	}
}


void closeDSK(struct dwTransferData *dp, int which)
{
	if (dp->dskpath[which] != NULL)
	{
		fclose(dp->dskpath[which]);
		dp->dskpath[which] = NULL;
	}
}


void *CoCoProcessor(void *data)
{
	struct dwTransferData *dp = (struct dwTransferData *)data;

	WinUpdate(window0, dp);

	while (comRead(dp, &(dp->lastOpcode), 1) > 0)
	{
		fd_set	rfds;
		struct timeval	tv;
		char *timeString = NULL;

		{

			switch (dp->lastOpcode)
			{
				case OP_RESET1:
				case OP_RESET2:
					DoOP_RESET(dp);
					break;

				case OP_INIT:
					DoOP_INIT(dp);
					break;

				case OP_TERM:
					DoOP_TERM(dp);
					break;

				case OP_REREAD:
					DoOP_REREAD(dp, "OP_REREAD");
					break;

				case OP_READ:
					DoOP_READ(dp, "OP_READ");
					break;

				case OP_REREADEX:
					DoOP_REREADEX(dp, "OP_REREADEX");
					break;

				case OP_READEX:
					DoOP_READEX(dp, "OP_READEX");
					break;

				case OP_WRITE:
					DoOP_WRITE(dp, "OP_WRITE");
					break;

				case OP_REWRITE:
					DoOP_REWRITE(dp, "OP_REWRITE");
					break;

				case OP_GETSTAT:
					DoOP_GETSTAT(dp);
					break;

				case OP_SETSTAT:
					DoOP_SETSTAT(dp);
					break;

				case OP_TIME:
					DoOP_TIME(dp);
					break;

				case OP_PRINT:
					DoOP_PRINT(dp);
					break;

				case OP_PRINTFLUSH:
					DoOP_PRINTFLUSH(dp);
					break;

				case OP_VPORT_READ:
					DoOP_VPORT_READ(dp);
					break;
                    
				default:
					break;
			}

			fflush(dp->dskpath[dp->lastDrive]);
		}

		WinUpdate(window0, dp);
	}
}


void DoOP_INIT(struct dwTransferData *dp)
{

	logHeader();
	fprintf(logfp, "OP_INIT\n");

	return;
}


void DoOP_TERM(struct dwTransferData *dp)
{
	logHeader();
	fprintf(logfp, "OP_TERM\n");

	return;
}


void DoOP_REWRITE(struct dwTransferData *dp, char *logStr)
{
	/* 1. Increment retry counter */
	dp->writeRetries++;

	/* 2. Call on WRITE handler */
	DoOP_WRITE(dp, logStr);

	return;
}


void DoOP_WRITE(struct dwTransferData *dp, char *logStr)
{
	u_char cocoChecksum[2];
	u_char response = 0;

	/* 1. Read in drive # and 3 byte LSN */
	comRead(dp, &(dp->lastDrive), 1);
	comRead(dp, dp->lastLSN, 3);

	/* 2. Read in 256 byte lastSector from CoCo */
	comRead(dp, dp->lastSector, 256);

	/* 3. Compute Checksum on sector received */
	if (dp->dw_protocol_vrsn == 1)
	{
		dp->lastChecksum = computeCRC(dp->lastSector, 256);
	}
	else
	{
		dp->lastChecksum = computeChecksum(dp->lastSector, 256);
	}

	/* 4. Read in 2 byte Checksum from CoCo */
	comRead(dp, cocoChecksum, 2);

	/* 5. Compare */
	if (dp->lastChecksum != int2(cocoChecksum))
	{
		response = 0xF3;

		/* 4.1.1 Bad Checksum, ask CoCo to send again, and return */

		comWrite(dp, &response, 1);

		if (logfp != NULL)
		{
			logHeader();
			fprintf(logfp, "%s[%d] LSN[%d] CoCoSum[%d], ServerSum[%d]\n", logStr, dp->lastDrive, int3(dp->lastLSN), int2(cocoChecksum), dp->lastChecksum);
		}
//		printf("WARNING! myChecksum = %X, cocoChecksum = %X\n", dp->lastChecksum, int2(cocoChecksum));

		
		return;
	}

	/* 5. Good Checksum, send positive ACK */
	comWrite(dp, &response, 1);

	/* 6. Seek to LSN in DSK image */
	if (seekSector(dp, int3(dp->lastLSN)) == 0)
	{
		/* 7. Write lastSector to DSK image */
		writeSector(dp);

		/* 8. Everything is ok, send an ok ack */
		comWrite(dp, &response, 1);

		/* 9. Increment sectorsWritten count */
		dp->sectorsWritten++;
	}

	return;
}


void DoOP_RESET(struct dwTransferData *dp)
{
	int a;

	/* 1. Reset counters and flags */
	dp->lastDrive = 0;
	dp->readRetries = 0;
	dp->writeRetries = 0;
	dp->sectorsRead = 0;
	dp->sectorsWritten = 0;
	dp->lastOpcode = OP_RESET1;

	for (a = 0; a < 3; a++)
	{
		dp->lastLSN[a] = 0;
	}

	for (a = 0; a < 256; a++)
	{
		dp->lastSector[a] = 0;
	}

	dp->lastGetStat = 255;
	dp->lastSetStat = 255;
	dp->lastChecksum = 0;
	dp->lastError = 0;

	logHeader();
	fprintf(logfp, "OP_RESET\n");

	/* Finally, sync disks. */
#if 0
	closeDSK(dp, 0);
	closeDSK(dp, 1);
	closeDSK(dp, 2);
	closeDSK(dp, 3);

	openDSK(dp, 0);
	openDSK(dp, 1);
	openDSK(dp, 2);
	openDSK(dp, 3);
#endif
	
	return;
}


void DoOP_REREAD(struct dwTransferData *dp, char *logStr)
{
	/* 1. Increment retry counter */
	dp->readRetries++;

	/* 2. Call on READ handler */
	DoOP_READ(dp, logStr);

	return;
}


void DoOP_READ(struct dwTransferData *dp, char *logStr)
{
	/* 1. Read in drive # and 3 byte LSN */
	comRead(dp, &(dp->lastDrive), 1);
	comRead(dp, dp->lastLSN, 3);

	/* 2. Seek to position in disk image based on LSN received */
	if (seekSector(dp, int3(dp->lastLSN)) == 0)
	{
		/* 3. Read the lastSector at LSN */
		readSector(dp);

		/* 4. Get error value, if any */
		dp->lastError = errno;

		/* 5. Send error code to CoCo */
		comWrite(dp, &(dp->lastError), 1);

		if (dp->lastError == 0)
		{
			u_char cocosum[2];

			/* 5.1.1. No error - send lastSector to CoCo */
			comWrite(dp, dp->lastSector, 256);
	
			/* 5.1.2. Compute Checksum and send to CoCo */
			if (dp->dw_protocol_vrsn == 1)
			{
				dp->lastChecksum = computeCRC(dp->lastSector, 256);
			}
			else
			{
				dp->lastChecksum = computeChecksum(dp->lastSector, 256);
			}
			cocosum[0] = dp->lastChecksum >> 8;
			cocosum[1] = dp->lastChecksum & 0xFF;

			comWrite(dp, (void *)cocosum, 2);

			/* 5.1.3. Increment sectorsRead count */
			dp->sectorsRead++;

			logHeader();
			fprintf(logfp, "%s[%d] LSN[%d] CoCoSum[%d]\n", logStr, dp->lastDrive, int3(dp->lastLSN), int2(cocosum));
		}
	}

	return;
}


void DoOP_REREADEX(struct dwTransferData *dp, char *logStr)
{
	/* 1. Increment retry counter */
	dp->readRetries++;

	/* 2. Call on READ handler */
	DoOP_READEX(dp, logStr);

	return;
}


void DoOP_READEX(struct dwTransferData *dp, char *logStr)
{
	/* 1. Read in drive # and 3 byte LSN */
	comRead(dp, &(dp->lastDrive), 1);
	comRead(dp, dp->lastLSN, 3);

	/* 2. Seek to position in disk image based on LSN received */
	if (seekSector(dp, int3(dp->lastLSN)) == 0)
	{
		/* 3. Read the lastSector at LSN */
		readSector(dp);

		/* 4. Get error value, if any */
		dp->lastError = errno;

		/* 5. Send the sector data to the CoCo */
		comWrite(dp, dp->lastSector, 256);

		/* 6. Read the checksum from the coco */
		u_char cocosum[2];

		comRead(dp, cocosum, 2);

		if (dp->lastError == 0)
		{
			u_char mysum[2];

			dp->lastChecksum = computeChecksum(dp->lastSector, 256);

			mysum[0] = (dp->lastChecksum >> 8) & 0xFF;
			mysum[1] = (dp->lastChecksum << 0) & 0xFF;

			if (cocosum[0] == mysum[0] && cocosum[1] == mysum[1])
			{
				/* Increment sectorsRead count */
				dp->sectorsRead++;

				logHeader();
				fprintf(logfp, "%s[%d] LSN[%d] CoCoSum[%d]\n", logStr, dp->lastDrive, int3(dp->lastLSN), int2(cocosum));
			}

			comWrite(dp, &(dp->lastError), 1);

		}
	}

	return;
}



void DoOP_GETSTAT(struct dwTransferData *dp)
{
	/* 1. Read in drive # and stat code */
	comRead(dp, &(dp->lastDrive), 1);
	comRead(dp, &(dp->lastGetStat), 1);

	logHeader();
	fprintf(logfp, "OP_GETSTAT[%0d] Code[%s]\n", dp->lastDrive, getStatCode(dp->lastGetStat));

	return;
}


void DoOP_SETSTAT(struct dwTransferData *dp)
{
	/* 1. Read in drive # and stat code */
	comRead(dp, &(dp->lastDrive), 1);
	comRead(dp, &(dp->lastSetStat), 1);

	logHeader();
	fprintf(logfp, "OP_SETSTAT[%0d] Code[%s]\n", dp->lastDrive, getStatCode(dp->lastSetStat));

	return;
}


void DoOP_TIME(struct dwTransferData *dp)
{
	time_t	currentTime;
	struct tm *timepak;
	char p[1];


	currentTime = time(NULL);

	timepak = localtime(&currentTime);

	p[0] = timepak->tm_year;
	comWrite(dp, (void *)p, 1);
	p[0] = timepak->tm_mon + 1;
	comWrite(dp, (void *)p, 1);
	p[0] = timepak->tm_mday;
	comWrite(dp, (void *)p, 1);
	p[0] = timepak->tm_hour;
	comWrite(dp, (void *)p, 1);
	p[0] = timepak->tm_min;
	comWrite(dp, (void *)p, 1);
	p[0] = timepak->tm_sec;
	comWrite(dp, (void *)p, 1);
	p[0] = timepak->tm_wday;
	comWrite(dp, (void *)p, 1);

	logHeader();
	fprintf(logfp, "OP_TIME\n");

	return;
}


void DoOP_PRINT(struct dwTransferData *dp)
{
	comRead(dp, &dp->lastChar, 1);
	fwrite(&dp->lastChar, 1, 1, dp->prtfp);

	logHeader();
	fprintf(logfp, "OP_PRINT\n");

	return;
}


void DoOP_PRINTFLUSH(struct dwTransferData *dp)
{
	char buff[128];

	fclose(dp->prtfp);
	sprintf(buff, "cat drivewire.prt %s\n", dp->prtcmd);
	system(buff);
	dp->prtfp = fopen("drivewire.prt", "w+"); // empty it

	logHeader();
	fprintf(logfp, "OP_PRINTFLUSH\n");

	return;
}


void DoOP_VPORT_READ(struct dwTransferData *dp)
{
	logHeader();
	fprintf(logfp, "OP_VPORT_READ\n");
	comWrite(dp, (void *)"\x00\x00", 2);
    
	return;
}


uint16_t computeChecksum(u_char *data, int numbytes)
{
	uint16_t lastChecksum = 0x0000;

	/* Check to see if numbytes is odd or even */
	while (numbytes--)
	{
		lastChecksum += *(data++);
	}

	return(lastChecksum);
}


uint16_t computeCRC(u_char *data, int numbytes)
{
	uint16_t i, crc = 0;
	uint16_t *ptr = (uint16_t *)data;
	
	while(--numbytes >= 0)
	{
		crc = crc ^ *ptr++ << 8;
		
		for (i = 0; i < 8; i++)
		{
			if (crc & 0x8000)
			{
				crc = crc << 1 ^ 0x1021;
			}
			else
			{
				crc = crc << 1;
			}
		}
	}
	
	return (crc & 0xFFFF);
}


int comWrite(struct dwTransferData *dp, void *data, int numbytes)
{
/* Slight delay */
//usleep(10);

	fwrite(data, numbytes, 1, dp->devpath);
//	write(dp->devpath, data, numbytes);

	return(errno);
}


int readSector(struct dwTransferData *dp)
{
	fread(dp->lastSector, 1, 256, dp->dskpath[dp->lastDrive]);

	return(errno);
}


int writeSector(struct dwTransferData *dp)
{
	fwrite(dp->lastSector, 1, 256, dp->dskpath[dp->lastDrive]);

	return(errno);
}


int seekSector(struct dwTransferData *dp, int sector)
{
	if (dp->dskpath[dp->lastDrive] == NULL)
	{
		return -1;	
	}

	fseek(dp->dskpath[dp->lastDrive], sector * 256, SEEK_SET);

	return(errno);
}


int comRead(struct dwTransferData *dp, void *data, int numbytes)
{
	return fread(data, numbytes, 1, dp->devpath);
//	return read(dp->devpath, data, numbytes);
}


void comRaw(struct dwTransferData *dp)
{
	struct termios io_mod;
	int pathid = dp->devpath->FD;

	tcgetattr(pathid, &io_mod);
#if defined(__sun)
	io_mod.c_iflag &= 
		~(IGNBRK|BRKINT|PARMRK|ISTRIP|INLCR|IGNCR|ICRNL|IXON);
	io_mod.c_oflag &= ~OPOST;
	io_mod.c_lflag &= ~(ECHO|ECHONL|ICANON|ISIG|IEXTEN);
	io_mod.c_cflag &= ~(CSIZE|PARENB);
	io_mod.c_cflag |= CS8;

	cfsetospeed(&io_mod, dp->baudRate);
#else
	cfmakeraw(&io_mod);
	cfsetspeed(&io_mod, dp->baudRate);
#endif

	if (tcsetattr(pathid, TCSANOW, &io_mod) < 0)
	{
		perror("ioctl error");
	}
}



unsigned int int4(u_char *a)
{
	return (unsigned int)( (((u_char)*a)<<24) + ((u_char)*(a+1)<<16) + ((u_char)*(a+2)<<8) + (u_char)*(a+3) );
}


unsigned int int3(u_char *a)
{
	return(unsigned int)( (((u_char)*a)<<16) + ((u_char)*(a+1)<<8) + (u_char)*(a+2) );
}


unsigned int int2(u_char *a)
{
//#ifndef __BIG_ENDIAN__
//	return(unsigned int)( (((u_char)*(a+1))<<8) + (u_char)*a );
//#else
	return(unsigned int)( (((u_char)*a)<<8) + (u_char)*(a+1) );
//#endif
}


unsigned int int1(u_char *a)
{
	return(unsigned int)( ((u_char)*a) );
}


void _int4(unsigned int a, u_char *b)
{
	b[0] = ((a >> 24) & 0xFF); b[1] = ((a >> 16) & 0xFF); b[2] = ((a >> 8) & 0xFF); b[3] = (a & 0xFF);
}


void _int3(unsigned int a, u_char *b)
{
	b[0] = ((a >> 16) & 0xFF); b[1] = ((a >> 8)  & 0xFF); b[2] = (a & 0xFF);
}


void _int2(uint16_t a, u_char *b)
{
	b[0] = ((a >> 8)  & 0xFF); b[1] = (a & 0xFF);
}


void _int1(unsigned int a, u_char *b)
{
	b[0] = (a & 0xFF);
}



void WinInit(void)
{
	int y = 1;

	initscr();
	clear();
	window0 = newwin(24, 80, 0, 0);

	if (window0 == NULL)
	{
		printf("window must be at least 80x24!\n");
		exit(0);
	}

	wattron(window0, A_STANDOUT);
	wprintw(window0, "DriveWire Server v%d.%d (C) 2009 Boisy G. Pitre", REV_MAJOR, REV_MINOR);
	wattroff(window0, A_STANDOUT);


	WinSetup(window0);
}


void WinSetup(WINDOW *window)
{
	int y = 2;

	wmove(window, y++, 1);
	wprintw(window, "Last OpCode     :");
	wmove(window, y++, 1);
	wprintw(window, "Sectors Read    :");
	wmove(window, y++, 1);
	wprintw(window, "Sectors Written :");
	wmove(window, y++, 1);
	wprintw(window, "Last LSN        :");
	wmove(window, y++, 1);
	wprintw(window, "Read Retries    :");
	wmove(window, y++, 1);
	wprintw(window, "Write Retries   :");
	wmove(window, y++, 1);
	wprintw(window, "%% Good Reads    :");
	wmove(window, y++, 1);
	wprintw(window, "%% Good Writes   :");
	wmove(window, y++, 1);
	wprintw(window, "Last GetStat    :");
	wmove(window, y++, 1);
	wprintw(window, "Last SetStat    :");
        y++;
	wmove(window, y++, 1);
	wprintw(window, "CoCo Type       :");
	wmove(window, y++, 1);
	wprintw(window, "Serial Port     :");
	wmove(window, y++, 1);
	wprintw(window, "DriveWire Mode  :");
	wmove(window, y++, 1);
	wprintw(window, "Print Command   :");
        y++;
	wmove(window, y++, 1);
	wprintw(window, "Disk 0          :");
	wmove(window, y++, 1);
	wprintw(window, "Disk 1          :");
	wmove(window, y++, 1);
	wprintw(window, "Disk 2          :");
	wmove(window, y++, 1);
	wprintw(window, "Disk 3          :");

        y++;
	wmove(window, y++, 1);

	wattron(window, A_STANDOUT);
	wprintw(window, "[0-3] Disk   [C]oCo   [P]ort   [R]eset   [M]ode   [L]Print   [Q]uit");
	wattroff(window, A_STANDOUT);

	/* 2. Refresh */
	wrefresh(window);

	return;
}


void WinUpdate(WINDOW *window, struct dwTransferData *dp)
{
	int x = 20;
	int y = 2;
	int i;

	if (updating == 1)
	{
		return;
	}

	wmove(window, y++, x);
	wclrtoeol(window);
	switch (dp->lastOpcode)
	{
		case OP_NOP:
			wprintw(window, "OP_NOP");
			break;

		case OP_INIT:
			wprintw(window, "OP_INIT");
			break;

		case OP_READ:
			wprintw(window, "OP_READ");
			break;

		case OP_READEX:
			wprintw(window, "OP_READEX");
			break;

		case OP_WRITE:
			wprintw(window, "OP_WRITE");
			break;

		case OP_REREAD:
			wprintw(window, "OP_REREAD");
			break;

		case OP_REREADEX:
			wprintw(window, "OP_REREADEX");
			break;

		case OP_REWRITE:
			wprintw(window, "OP_REWRITE");
			break;

		case OP_TERM:
			wprintw(window, "OP_TERM");
			break;

		case OP_RESET1:
		case OP_RESET2:
			wprintw(window, "OP_RESET");
			break;

		case OP_GETSTAT:
			wprintw(window, "OP_GETSTAT");
			break;

		case OP_SETSTAT:
			wprintw(window, "OP_SETSTAT");
			break;

		case OP_TIME:
			wprintw(window, "OP_TIME");
			break;

		case OP_PRINT:
			wprintw(window, "OP_PRINT");
			break;

		case OP_PRINTFLUSH:
			wprintw(window, "OP_PRINTFLUSH");
			break;

		default:
			wprintw(window, "UNKNOWN (%d)", dp->lastOpcode);
			break;
	}

	wmove(window, y++, x); wclrtoeol(window);
	wprintw(window, "%d", dp->sectorsRead);
	wmove(window, y++, x); wclrtoeol(window);
	wprintw(window, "%d", dp->sectorsWritten);
	wmove(window, y++, x); wclrtoeol(window);
	wprintw(window, "%d", int3(dp->lastLSN));
	wmove(window, y++, x); wclrtoeol(window);
	wprintw(window, "%d", dp->readRetries);
	wmove(window, y++, x); wclrtoeol(window);
	wprintw(window, "%d", dp->writeRetries);
	wmove(window, y++, x); wclrtoeol(window);
	if (dp->sectorsRead + dp->readRetries == 0)
	{
		wprintw(window, "0%%");
	}
	else
	{
		float percent = ((float)dp->sectorsRead / ((float)dp->sectorsRead + (float)dp->readRetries)) * 100;
		wprintw(window, "%3.3f%%", percent);
	}
	wmove(window, y++, x); wclrtoeol(window);
	if (dp->sectorsWritten + dp->writeRetries == 0)
	{
		wprintw(window, "0%%");
	}
	else
	{
		float percent = ((float)dp->sectorsWritten / ((float)dp->sectorsWritten + (float)dp->writeRetries)) * 100;
		wprintw(window, "%3.3f%%", percent);
	}
	wmove(window, y++, x); wclrtoeol(window);
	wprintw(window, "$%02X (%s)", dp->lastGetStat, getStatCode(dp->lastGetStat));
	wmove(window, y++, x); wclrtoeol(window);
	wprintw(window, "$%02X (%s)", dp->lastSetStat, getStatCode(dp->lastSetStat));
	wmove(window, y++, x); wclrtoeol(window);
	wmove(window, y++, x); wclrtoeol(window);
	switch (dp->cocoType)
	{
		case 3:
			if (dp->dw_protocol_vrsn == 3)
			{
				wprintw(window, "%s", "CoCo 3 (115200 baud)");
			}
			else
			{
				wprintw(window, "%s", "CoCo 3 (57600 baud)");
			}
			break;

		case 2:
			if (dp->dw_protocol_vrsn == 3)
			{
				wprintw(window, "%s", "CoCo 2 (57600 baud)");
			}
			else
			{
				wprintw(window, "%s", "CoCo 1/2 (38400 baud)");
			}
			break;
	}
	wmove(window, y++, x); wclrtoeol(window);
	wprintw(window, "%s", device);
	wmove(window, y++, x); wclrtoeol(window);
	switch (dp->dw_protocol_vrsn)
	{
		case 3:
			wprintw(window, "3.0");
			break;
		case 2:
			wprintw(window, "2.0");
			break;
		case 1:
			wprintw(window, "1.0");
			break;
	}

	wclrtoeol(window);

	wmove(window, y++, x); wclrtoeol(window);
	wprintw(window, dp->prtcmd);

	wmove(window, y++, x); wclrtoeol(window);

	for (i = 0; i < 4; i++)
	{
		wmove(window, y++, x); wclrtoeol(window);
		wprintw(window, "%s", dskfile[i]);
	}

	/* 2. Refresh */
	wrefresh(window);

	return;
}


void WinTerm(void)
{
	endwin();

	return;
}


char *getStatCode(int statcode)
{
	static char codeName[64];

	switch (statcode)
	{
		case 0x00:
			strcpy(codeName, "SS.Opt");
			break;

		case 0x02:
			strcpy(codeName, "SS.Size");
			break;

		case 0x03:
			strcpy(codeName, "SS.Reset");
			break;

		case 0x04:
			strcpy(codeName, "SS.WTrk");
			break;

		case 0x05:
			strcpy(codeName, "SS.Pos");
			break;

		case 0x06:
			strcpy(codeName, "SS.EOF");
			break;

		case 0x0A:
			strcpy(codeName, "SS.Frz");
			break;

		case 0x0B:
			strcpy(codeName, "SS.SPT");
			break;

		case 0x0C:
			strcpy(codeName, "SS.SQD");
			break;

		case 0x0D:
			strcpy(codeName, "SS.DCmd");
			break;

		case 0x0E:
			strcpy(codeName, "SS.DevNm");
			break;

		case 0x0F:
			strcpy(codeName, "SS.FD");
			break;

		case 0x10:
			strcpy(codeName, "SS.Ticks");
			break;

		case 0x11:
			strcpy(codeName, "SS.Lock");
			break;

		case 0x12:
			strcpy(codeName, "SS.VarSect");
			break;

		case 0x14:
			strcpy(codeName, "SS.BlkRd");
			break;

		case 0x15:
			strcpy(codeName, "SS.BlkWr");
			break;

		case 0x16:
			strcpy(codeName, "SS.Reten");
			break;

		case 0x17:
			strcpy(codeName, "SS.WFM");
			break;

		case 0x18:
			strcpy(codeName, "SS.RFM");
			break;

		case 0x1B:
			strcpy(codeName, "SS.Relea");
			break;

		case 0x1C:
			strcpy(codeName, "SS.Attr");
			break;

		case 0x1E:
			strcpy(codeName, "SS.RsBit");
			break;

		case 0x20:
			strcpy(codeName, "SS.FDInf");
			break;

		case 0x26:
			strcpy(codeName, "SS.DSize");
			break;

		case 255:
			strcpy(codeName, "None");
			break;

		default:
			strcpy(codeName, "???");
			break;
	}

	return(codeName);
}


int loadPreferences(struct dwTransferData *datapack)
{
	FILE	*pf;
	char	buffer[81];
	int	i;
	char	*p;


	pf = fopen(".drivewirerc", "r");

	if (pf == NULL)
	{
		return errno;
	}

	for (i = 0; i < 4; i++)
	{
		fgets((char *)dskfile[i], 128, pf);
		p = strchr(dskfile[i], '\n');
		if (p != NULL) { *p = '\0'; }
	}

	fgets((char *)device, 128, pf);
	p = strchr(device, '\n');
	if (p != NULL) { *p = '\0'; }
	fgets(buffer, 128, pf);
	datapack->cocoType = atoi(buffer);
	fgets(buffer, 128, pf);
	datapack->dw_protocol_vrsn = atoi(buffer);
	setCoCo(datapack, datapack->cocoType);
	fgets(datapack->prtcmd, 128, pf);

	return 0;
}


int savePreferences(struct dwTransferData *datapack)
{
	FILE	*pf;
	char	buffer[81];
	int	i;


	pf = fopen(".drivewirerc", "w");

	if (pf == NULL)
	{
		return errno;
	}

	for (i = 0; i < 4; i++)
	{
		fprintf(pf, "%s\n", dskfile[i]);
	}

	fprintf(pf, "%s\n", device);
	fprintf(pf, "%d\n", datapack->cocoType);
	fprintf(pf, "%d\n", datapack->dw_protocol_vrsn);
	fprintf(pf, "%s\n", datapack->prtcmd);

	return 0;
}


void prtOpen(struct dwTransferData *datapack)
{
	datapack->prtfp = fopen("drivewire.prt", "a+");
}


void prtClose(struct dwTransferData *datapack)
{
	fclose(datapack->prtfp);

	datapack->prtfp = NULL;
}


void logOpen(void)
{
	logfp = fopen("drivewire.log", "a+");
}


void logClose(void)
{
	fclose(logfp);

	logfp = NULL;
}


void logHeader(void)
{
	time_t	currentTime;
	struct tm *timepak;

	currentTime = time(NULL);
	timepak = localtime(&currentTime);

	fprintf(logfp, "%04d-%02d-%02d %02d:%02d:%02d ", 
		1900 + timepak->tm_year,
		timepak->tm_mon + 1,
		timepak->tm_mday,
		timepak->tm_hour,
		timepak->tm_min,
		timepak->tm_sec,
		timepak->tm_wday);

	return;
}

