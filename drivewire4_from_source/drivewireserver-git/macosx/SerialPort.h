//
//  SerialPort.h
//  DriveWire
//
//  Created by Boisy Pitre on Mon Jul 14 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

#include <unistd.h>
#include <fcntl.h>
#include <sys/filio.h>
#include <sys/ioctl.h>
#include <termios.h>



@interface SerialPort : NSObject
{
    int				fd;				// file descriptor to path of serial device
    NSString		*name;			// the view-friendly name of the device
    struct termios 	sOriginalTTYAttrs;	// original TTY attributes
    struct termios 	sTTYAttrs;		// maeleable TTY attributes
}



// Parity (Odd, Even, None)

typedef enum
{
    parityOdd,
    parityEven,
    parityNone
} serialParity;



// Port access methods

- (BOOL)acquirePort:(NSString *)name;
- (void)releasePort;
- (BOOL)readData:(u_char *)data:(int)maximumLength;
- (BOOL)writeData:(u_char *)data:(int)length;
- (int)bytesReady;



// Query methods

- (BOOL)isAcquired;



// Set methods

- (BOOL)setBaudRate:(int)baudRate;
- (BOOL)setWordSize:(int)wordSize;
- (BOOL)setParity:(serialParity)parity;
- (BOOL)setStopBits:(int)stopBits;
- (BOOL)setMinimumReadBytes:(int)number;
- (BOOL)setReadTimeout:(int)timeout;



// Get methods

- (NSString *)name;
- (int)baudRate;
- (int)wordSize;
- (serialParity)parity;
- (int)stopBits;
- (int)minimumReadBytes;
- (int)readTimeout;

@end
