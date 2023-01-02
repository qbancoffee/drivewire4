//
//  SerialPort.m
//  DriveWire
//
//  Created by Boisy Pitre on Mon Jul 14 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

#import "SerialPort.h"


@implementation SerialPort


#pragma mark Init Methods

/*!
	@method init
	@abstract Initializes the class
	@result The pointer to the object.
 */
- (id)init
{
    // Initialize our super.
    
    if (self = [super init])
    {
        fd = -1;
    }


    // Return ourself.
    
    return(self);
}


#pragma mark Port Control Methods

/*!
	@method acquirePort
	@abstract Acquires a path to the specified serial port.
	@param NSString * A pointer to the string containing the port name.
	@result If the acquisition was successful, YES is returned, otherwise NO.
 */
- (BOOL)acquirePort:(NSString *)deviceName
{
    int status = -1;

	
    // Acquire the path to the device.  If error, return an error.
    
    status = open([deviceName cString], O_RDWR | O_NOCTTY | O_NDELAY);

    if (status != -1) 
    {
		fd = status;
		
        // Set blocking I/O, no signal on data ready...

        if ((status = fcntl(fd, F_SETFL, 0)) != -1) 
        {
            // Get the current options and save them for later reset.

            if ((status = tcgetattr(fd, &sOriginalTTYAttrs)) != -1) 
            {
                // Set raw input, one second timeout.
                // These options are documented in the man page for termios.
                
                sTTYAttrs = sOriginalTTYAttrs;
                cfmakeraw(&sTTYAttrs);
                sTTYAttrs.c_cflag |= CS8 | CLOCAL | CREAD;
                sTTYAttrs.c_lflag = IGNBRK | IGNPAR;
                sTTYAttrs.c_cc[VMIN] = 1;		// 1 chars to wait for
                sTTYAttrs.c_cc[VTIME] = 0;

        
                // Set the options.
                
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
            }
        }
    }


	if (status == -1)
	{
		return NO;
	}
	else
	{
		return YES;
	}
}



/*!
	@method releasePort
	@abstract Terminates the path to the serial port.
 */
- (void)releasePort
{
    if (fd != -1)
    {
        close(fd);
        
        fd = -1;
    }
    
    
    return;
}



/*!
	@method readData
	@abstract Reads a number of bytes from the serial port.
	@param data A pointer to a buffer which contains the read data.
	@param maximumLength The maximum number of bytes to read.
	@result Returns YES if the read was successful, NO if the read was not.
 */
- (BOOL)readData :(u_char *)data :(int)maximumLength
{
    int status = -1;
    
    
    if (fd != -1)
    {
		status = read(fd, data, maximumLength);
    }
    
    
	if (status == -1)
	{
		return NO;
	}
	else
	{
		return YES;
	}
}



/*!
	@method writeData
	@abstract Writes a number of bytes to the serial port.
	@param data A pointer to a buffer which contains the data to write.
	@param length The number of bytes to write.
	@result Returns YES if the read was successful, NO if the read was not.
 */
- (BOOL)writeData :(u_char *)data :(int)length
{
    int status = -1;
    
    
    if (fd != -1)
    {
        status = write(fd, data, length);
    }
    
    
	if (status == -1)
	{
		return NO;
	}
	else
	{
		return YES;
	}
}



/*!
	@method bytesReady
	@abstract Returns the number of bytes ready to be read.
	@result The number of bytes ready to be read from the serial port.
 */
- (int)bytesReady
{
    int	ready = -1;
    
    
    if (fd != -1)
    {
        ioctl(fd, FIONREAD, &ready);
    }
    
    
    return ready;
}



/*!
	@method isAcquired
	@abstract Returns the status of the serial port.
	@result YES if the port is acquired, otherwise NO.
 */
- (BOOL)isAcquired
{
    if (fd == -1)
    {
        return NO;
    }
    
    
    return YES;
}



/*!
	@method name
	@abstract Returns the name of the serial port.
	@result Returns an NSString of the name of the serial port.
 */
- (NSString *)name
{
    return name;
}



/*!
	@method setBaudRate
	@abstract Sets the baud rate of the serial port.
	@param baudRate The baud rate to set the serial port to.
	@result Returns YES if the set was successful, NO if it was not.
 */
- (BOOL)setBaudRate:(int)baudRate
{
    int status = -1;
    
    
    if (fd != -1)
    {
        if ((status = cfsetspeed(&sTTYAttrs, baudRate)) != -1)
        {
            status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
		}
    }
    

	if (status == -1)
	{
		return NO;
	}
	else
	{
		return YES;
	}
}



/*!
	@method baudRate
	@abstract Returns the serial port's current baud rate.
	@result The baud rate is returned.
 */
- (int)baudRate
{
    int	status = -1;
    
    
    if (fd != -1)
    {
        status = cfgetispeed(&sTTYAttrs);
    }
    

    return status;
}



/*!
	@method setWordSize
	@abstract Sets the word size of the serial port.
	@param int The word size.
	@result Returns YES if the set was successful, NO if it was not.
 */
- (BOOL)setWordSize:(int)wordSize;
{
    int		status = -1;

	
    if (fd != -1)
    {
        switch (wordSize)
        {
            case 8:
                sTTYAttrs.c_cflag &= ~CSIZE;
                sTTYAttrs.c_cflag |= CS8;
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
                break;

            case 7:
                sTTYAttrs.c_cflag &= ~CSIZE;
                sTTYAttrs.c_cflag |= CS7;
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
                break;
                
            case 6:
                sTTYAttrs.c_cflag &= ~CSIZE;
                sTTYAttrs.c_cflag |= CS6;
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
                break;

            case 5:
                sTTYAttrs.c_cflag &= ~CSIZE;
                sTTYAttrs.c_cflag |= CS5;
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
                break;
        }
    }
    
	if (status == -1)
	{
		return NO;
	}
	else
	{
		return YES;
	}
}



/*!
	@method wordSize
	@abstract Returns the serial port's word size setting.
	@result The word size is returned.
 */
- (int)wordSize;
{
    int	status = -1;
    
    
    if (fd != -1)
    {
        switch (sTTYAttrs.c_cflag & CSIZE)
        {
            case CS8:
                status = 8;
                break;

            case CS7:
                status = 7;
                break;
                
            case CS6:
                status = 6;
                break;

            case CS5:
                status = 5;
                break;
        }
    }
    

    return status;
}



/*!
	@method setParity
	@abstract Sets the serial port's parity.
	@param parity The desired parity.
	@result Returns YES if the set was successful, NO if it was not.
 */
- (BOOL)setParity:(serialParity)parity
{
    int	status = -1;


    if (fd != -1)
    {
        switch (parity)
        {
            case parityNone:
                sTTYAttrs.c_cflag &= ~(PARENB | PARODD);
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
                break;

            case parityOdd:
                sTTYAttrs.c_cflag |= (PARENB | PARODD);
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
                break;

            case parityEven:
                sTTYAttrs.c_cflag |= PARENB;
                sTTYAttrs.c_cflag &= ~PARODD;
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
                break;
        }
    }
    
    
	if (status == -1)
	{
		return NO;
	}
	else
	{
		return YES;
	}
}



/*!
	@method parity
	@abstract Returns the serial port's current parity setting.
	@result The serial port's current parity setting.
 */
- (serialParity)parity
{
    serialParity	parity = parityNone;
    
    
    if (fd != -1)
    {
        if (sTTYAttrs.c_cflag & PARENB != 0)
        {
            if (sTTYAttrs.c_cflag & PARODD != 0)
            {
                parity = parityOdd;
            }
            else
            {
                parity = parityEven;
            }
        }
    }
        

    return parity;
}



/*!
	@method setStopBits
	@abstract Sets the serial port's stop bits.
	@param stopBits The number of stop bits to set.
	@result Returns YES if the set was successful, NO if it was not.
 */
- (BOOL)setStopBits:(int)stopBits;
{
    int	status = -1;
    
    
    if (fd != -1)
    {
        switch (stopBits)
        {
            case 1:
                sTTYAttrs.c_cflag &= ~CSTOPB;
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
                break;

            case 2:
                sTTYAttrs.c_cflag |= CSTOPB;
                status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
                break;
        }
    }
    

	if (status == -1)
	{
		return NO;
	}
	else
	{
		return YES;
	}
}



/*!
	@method stopBits
	@abstract Returns the serial port's stop bits setting.
	@result The number of stop bits.
 */
- (int)stopBits;
{
    int	status = -1;
    
    
    if (fd != -1)
    {
        status = 1;	// assume 1 stop bit.
        
        if (sTTYAttrs.c_cflag & CSTOPB != 0)
        {
            status = 2;
        }
    }
    

    return status;
}



// Sets the read timeout in milliseconds.
// Returns 0 (success) OR -1 (failure).

/*!
	@method setReadTimeout
	@abstract Sets read timeout in milliseconds.
	@param timeout The number of milliseconds to timeout if no data is present.
	@result Returns YES if the set was successful, NO if it was not.
 */
- (BOOL)setReadTimeout:(int)timeout
{
    int	status = -1;


    if (fd != -1)
    {
        sTTYAttrs.c_cc[VTIME] = timeout;

        status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
    }

	if (status == -1)
	{
		return NO;
	}
	else
	{
		return YES;
	}
}


/*!
	@method readTimeout
	@abstract Returns the serial port's read timeout.
	@result The read timeout in milliseconds.
 */
- (int)readTimeout
{
    int	status = -1;
    
    
    if (fd != -1)
    {
        status = sTTYAttrs.c_cc[VTIME];
    }
    

    return status;
}



/*!
	@method setMinimumReadBytes
	@abstract Sets the serial port's minimum read byte size.
	@param number The minimum number of read bytes to set.
	@result Returns YES if the set was successful, NO if it was not.
 */
- (BOOL)setMinimumReadBytes:(int)number
{
    int	status = -1;


    if (fd != -1)
    {
        sTTYAttrs.c_cc[VMIN] = number;

        status = tcsetattr(fd, TCSANOW, &sTTYAttrs);
    }

	if (status == -1)
	{
		return NO;
	}
	else
	{
		return YES;
	}
}



/*!
	@method minimumReadBytes
	@abstract Returns the serial port's minimum read byte count.
	@result The minimum number of ready bytes is returned.
 */
- (int)minimumReadBytes
{
    int	status = -1;
    
    
    if (fd != -1)
    {
        status = sTTYAttrs.c_cc[VMIN];
    }
    

    return status;
}


@end
