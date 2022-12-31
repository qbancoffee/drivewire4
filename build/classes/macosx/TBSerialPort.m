//--------------------------------------------------------------------------------------------------
//
//  Copyright (c) 2010-2013 Tee-Boy
//
//  This source code and specific concepts contained herein are Confidential
//  Information and Property of Tee-Boy.
//  Distribution is prohibited without written permission of Tee-Boy.
//
//--------------------------------------------------------------------------------------------------
//
//  Tee-Boy                                http://www.tee-boy.com/
//  441 Saint Paul Avenue
//  Opelousas, LA  70570                   info@tee-boy.com
//
//--------------------------------------------------------------------------------------------------

#define MODULE_HASHTAG  "TBSerialPort"
#import "TBSerialPort.h"

//#define DEBUG

@implementation TBSerialPort

#pragma mark -
#pragma mark Init/Dealloc Methods

enum {PORT_CLOSED, PORT_OPENED, PORT_CLOSING};

- (id)initWithDeviceName:(NSString *)deviceName serviceName:(NSString *)serviceName;
{
    // Initialize our super.
    if ((self = [super init]))
    {
        _fd = -1;		
		_owner = nil;
		_deviceName = [deviceName retain];
		_serviceName = [serviceName retain];
    }
	
    // Return ourself.    
    return(self);
}

- (void)dealloc;
{
    _delegate = nil;
	
	[self closePort];
	
	// Release retained variables
	[_deviceName release];
	[_serviceName release];
	
	// Call our super's dealloc
    [super dealloc];
	
    return;
}

#pragma mark -
#pragma mark Accessor Methods

- (id)delegate;
{
	return _delegate;
}

- (void)setDelegate:(id)_value;
{
	_delegate = _value;
}

#pragma mark -
#pragma mark Port Acquisition Methods

- (BOOL)openPort:(id)owner error:(NSError **)error;
{
    int status = -1;
	
	// if the port is already opened, return NO
	if (_fd != -1)
	{
		if (error != nil)
		{
			*error = [NSError errorWithDomain:@"com.tee-boy.DriveWireMacServer" code:-10 userInfo:nil];
		}
		
		return NO;
	}
	
    // acquire the path to the device.  If error, return an error.
    status = open([_deviceName cStringUsingEncoding:NSASCIIStringEncoding], O_RDWR | O_NOCTTY | O_NDELAY);
	_fd = status;
	
    if (status != -1) 
    {
		_owner = owner;
		
        // set blocking I/O, no signal on data ready...
        if ((status = fcntl(_fd, F_SETFL, 0)) != -1) 
        {
            // Get the current options and save them for later reset.
            if ((status = tcgetattr(_fd, &_originalTTYAttrs)) != -1) 
            {
                // Set raw input, one second timeout.
                // These options are documented in the man page for termios.
                _ttyAttrs = _originalTTYAttrs;
                cfmakeraw(&_ttyAttrs);
                _ttyAttrs.c_cflag |= CS8 | CLOCAL | CREAD;
                _ttyAttrs.c_lflag = IGNBRK | IGNPAR;
                _ttyAttrs.c_cc[VMIN] = 1;		// 1 chars to wait for
                _ttyAttrs.c_cc[VTIME] = 0;
				[self setBaudRate:9600];
				
                // set the options.                
                status = tcsetattr(_fd, TCSANOW, &_ttyAttrs);
				
				[self setWordSize:8];
				[self setParity:parityNone];
				[self setStopBits:1];
				
				[self setMinimumReadBytes:0];
				[self setReadTimeout:1];
				
				// spin off listener thread.				
				_allowedToRun = TRUE;				
                _threadIsRunning = TRUE;
				[NSThread detachNewThreadSelector:@selector(listener2:) toTarget:self withObject:nil];
            }
        }
    }
	
	if (status == -1)
	{
		if (error != nil)
		{
			*error = [NSError errorWithDomain:@"com.tee-boy.DriveWireMacServer" code:-10 userInfo:nil];
		}
		
		return NO;
	}
	else
	{
		return YES;
	}
}

- (BOOL)closePort;
{
	if (_allowedToRun == TRUE)
	{
		// Flag listener thread to quit
		_allowedToRun = FALSE;
		
		if (_fd != -1)
		{
			close(_fd);        
			_fd = -1;
			_owner = nil;
		}
		
		return YES;
	}
    
    return NO;
}


#pragma mark -
#pragma mark Data Acquisition Methods

// This method should run on its own thread.  It listens to any data coming in from the port,
// then packages that data and sends it out.
- (void)listener2:(id)anObject;
{
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
	
	struct timeval timeout;
	int result = 0;
	fd_set localReadFDSet;
	char buf[1024];
	
	// Do processing so long as we are allowed to run.
    while (_allowedToRun == TRUE && _fd != -1)
    {
		FD_ZERO(&localReadFDSet);
		FD_SET(_fd, &localReadFDSet);
		
		timeout.tv_sec = 0;
		timeout.tv_usec = 100000; // check to see if port closed every 100ms
		
		result = select(_fd + 1, &localReadFDSet, NULL, NULL, &timeout);
		if (TRUE == _allowedToRun && result > 0 && FD_ISSET(_fd, &localReadFDSet)) 
		{
			// Data is available
			long lengthRead = read(_fd, buf, sizeof(buf));
			if (lengthRead > 0)
			{
				NSData *readData = [NSData dataWithBytes:buf length:lengthRead];
				if (readData != nil && [(id)_delegate respondsToSelector:@selector(serialPort:didReceiveData:)])
				{
					[_delegate serialPort:self didReceiveData:readData];
				}
			}
		}
		
		[pool drain];    	
		pool = [[NSAutoreleasePool alloc] init];
    }
	
    _threadIsRunning = FALSE;
	
	[pool drain];
}

- (NSData *)readData;
{
	NSData *incoming = nil;
	
    // only if we have a valid port open
    if (_fd != -1)
    {
        int ready = [self bytesReady];
        
        // only if we have data to ready
        if (ready > 0)
        {
            void *buffer = (void *)malloc(ready);
			
            int count = read(_fd, buffer, ready);
			
            // only if we read some bytes
            if (count > 0)
            {
                incoming = [NSData dataWithBytes:buffer length:count];
            }
			
            free(buffer);
        }
    }
	
    return incoming;
}

- (BOOL)writeData:(NSData *)data;
{
    BOOL result = FALSE;
    
    if (_fd != -1)
    {
		int status = write(_fd, [data bytes], [data length]);
        
        if (status != -1)
        {
            // write succeeded
            result = YES;
            
            if (_logOutgoingBytes == TRUE)
            {
            }            
        }
    }
    
    return result;
}

- (BOOL)writeString:(NSString *)data;
{
    NSData *packaged = [data dataUsingEncoding:NSUTF8StringEncoding];
	
    return [self writeData:packaged];
}


#pragma mark -
#pragma mark Data Query Methods

- (Boolean)inputLogging;
{
	return _logIncomingBytes;
}

- (void)setInputLogging:(Boolean)value;
{
    _logIncomingBytes = value;
}

- (Boolean)outputLogging;
{
	return _logOutgoingBytes;
}

- (void)setOutputLogging:(Boolean)value;
{
    _logOutgoingBytes = value;
}

- (int)bytesReady;
{
    int	ready = -1;
    
    if (_fd != -1)
    {
        ioctl(_fd, FIONREAD, &ready);
    }
    
    return ready;
}

- (BOOL)isAcquired;
{
    if (_fd == -1)
    {
        return NO;
    }
    
    return YES;
}

- (NSString *)deviceName;
{
    return _deviceName;
}

- (NSString *)serviceName;
{
    return _serviceName;
}

- (id)owner;
{
    return _owner;
}


#pragma mark -
#pragma mark Port Control Methods

- (BOOL)dtrState;
{
	BOOL result = FALSE;
	
    if (_fd != -1)
    {		
		int portstatus;
		
        int status = ioctl(_fd, TIOCMGET, &portstatus);   // get current port status
		if (status == 0)
		{
			result = (portstatus & TIOCM_DTR);
		}
    }
	
	return result;
}

- (void)setDTRState:(BOOL)onOrOff;
{
    if (_fd != -1)
    {		
		int portstatus;
		
        ioctl(_fd, TIOCMGET, &portstatus);   // get current port status
        switch (onOrOff)
        {
            case TRUE:
				portstatus |= TIOCM_DTR;
                break;
				
            case FALSE:
				portstatus &= ~TIOCM_DTR;
                break;
        }
		
        ioctl(_fd, TIOCMSET, &portstatus);   // set current port status
    }
}

- (BOOL)hardwareHandshaking;
{
	BOOL result = FALSE;
	
    if (_fd != -1)
    {
		tcgetattr(_fd, &_ttyAttrs);
		
		result = _ttyAttrs.c_cflag & CRTSCTS;
    }
	
	return result;
}

- (void)setHardwareHandshaking:(BOOL)onOrOff;
{
    if (_fd != -1)
    {
        switch (onOrOff)
        {
            case TRUE:
                _ttyAttrs.c_cflag |= CRTSCTS;
                break;
				
            case FALSE:
                _ttyAttrs.c_cflag &= ~CRTSCTS;
                break;
        }
		
		tcsetattr(_fd, TCSANOW, &_ttyAttrs);
    }
}

- (int)baudRate;
{
    int	status = -1;
	
    if (_fd != -1)
    {
        status = cfgetispeed(&_ttyAttrs);
    }
    
    return status;
}

- (BOOL)setBaudRate:(int)baudRate;
{
    BOOL result = FALSE;
    
    if (_fd != -1)
    {
        int status;
        
        if ((status = cfsetspeed(&_ttyAttrs, baudRate)) != -1)
        {
            status = tcsetattr(_fd, TCSANOW, &_ttyAttrs);
		}
        
        if (status != -1)
        {
            result = YES;
        }
    }
    
    return result;
}

- (int)wordSize;
{
    int	status = -1;
	
    if (_fd != -1)
    {
        switch (_ttyAttrs.c_cflag & CSIZE)
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

- (BOOL)setWordSize:(int)wordSize;
{
    BOOL result = FALSE;
    
    if (_fd != -1)
    {
        switch (wordSize)
        {
            case 8:
                _ttyAttrs.c_cflag &= ~CSIZE;
                _ttyAttrs.c_cflag |= CS8;
                break;
                
            case 7:
                _ttyAttrs.c_cflag &= ~CSIZE;
                _ttyAttrs.c_cflag |= CS7;
                break;
                
            case 6:
                _ttyAttrs.c_cflag &= ~CSIZE;
                _ttyAttrs.c_cflag |= CS6;
                break;
                
            case 5:
                _ttyAttrs.c_cflag &= ~CSIZE;
                _ttyAttrs.c_cflag |= CS5;
                break;
        }
        
        int status = tcsetattr(_fd, TCSANOW, &_ttyAttrs);
        
        if (status != -1)
        {
            result = TRUE;
        }
	}
    
    return result;
}

- (serialParity)parity;
{
    serialParity	parity = parityNone;
	
    if (_fd != -1)
    {
        if ((_ttyAttrs.c_cflag & PARENB) != 0)
        {
            if ((_ttyAttrs.c_cflag & PARODD) != 0)
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

- (BOOL)setParity:(serialParity)parity;
{
    BOOL result = FALSE;
    
    if (_fd != -1)
    {
        switch (parity)
        {
            case parityNone:
                _ttyAttrs.c_cflag &= ~(PARENB | PARODD);
                break;
                
            case parityOdd:
                _ttyAttrs.c_cflag |= (PARENB | PARODD);
                break;
                
            case parityEven:
                _ttyAttrs.c_cflag |= PARENB;
                _ttyAttrs.c_cflag &= ~PARODD;
                break;
        }
        
        int status = tcsetattr(_fd, TCSANOW, &_ttyAttrs);
        
        if (status != -1)
        {
            result = TRUE;
        }
	}
    
    return result;
}

- (int)stopBits;
{
    int	status = -1;    
    
    if (_fd != -1)
    {
        status = 1;	// assume 1 stop bit.
        
        if ((_ttyAttrs.c_cflag & CSTOPB) != 0)
        {
            status = 2;
        }
    }
    
    return status;
}

- (BOOL)setStopBits:(int)stopBits;
{
    BOOL result = FALSE;
    
    if (_fd != -1)
    {
        switch (stopBits)
        {
            case 1:
                _ttyAttrs.c_cflag &= ~CSTOPB;
                break;
				
            case 2:
                _ttyAttrs.c_cflag |= CSTOPB;
                break;
        }
		
        int status = tcsetattr(_fd, TCSANOW, &_ttyAttrs);
        if (status != -1)
        {
            result = TRUE;
        }
	}
    
    return result;
}

- (int)readTimeout;
{
    int	status = -1;    
    
    if (_fd != -1)
    {
        status = _ttyAttrs.c_cc[VTIME];
    }    
	
    return status;
}

- (BOOL)setReadTimeout:(int)timeout;
{
    BOOL result = FALSE;
    
    if (_fd != -1)
    {
        _ttyAttrs.c_cc[VTIME] = timeout;
        
        int status = tcsetattr(_fd, TCSANOW, &_ttyAttrs);
        
        if (status != -1)
        {
            result = TRUE;
        }
    }
    
    return result;
}

- (int)minimumReadBytes;
{
    int	status = -1;
    
    if (_fd != -1)
    {
        status = _ttyAttrs.c_cc[VMIN];
    }
    
    return status;
}

- (BOOL)setMinimumReadBytes:(int)number;
{
    BOOL result = FALSE;
    
    if (_fd != -1)
    {
        _ttyAttrs.c_cc[VMIN] = number;
		
        int status = tcsetattr(_fd, TCSANOW, &_ttyAttrs);
		
        if (status != -1)
        {
            result = TRUE;
        }
	}
    
    return result;
}

@end
