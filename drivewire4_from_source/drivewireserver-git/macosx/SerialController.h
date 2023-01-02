//
//  SerialController.h
//  DriveWire
//
//  Created by Boisy Pitre on Fri Feb 14 2003.
//  Copyright (c) 2003 AES. All rights reserved.
//

#import <Foundation/Foundation.h>

#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <paths.h>
#include <termios.h>
#include <sysexits.h>
#include <sys/param.h>
#include <SerialPort.h>


@interface SerialController : NSObject
{
    NSMutableArray	*serialDeviceArray, *serialNameArray, *objectMappingArray;
}



/*!
	@method init
	@abstract Initializes the class
	@result The pointer to the object.
 */
- (id)init;


/*!
	@method doesPortExist
	@abstract This method determines if the passed port name exists on the system.
	@param name Pointer to the port name to verify.
	@result YES if the port exists; NO if not.
 */
- (BOOL)doesPortExist:(NSString *)name;


/*!
	@method isPortAvailable
	@abstract This method determines if a port is available or reserved.
	@param name Pointer to the port name to check.
	@result YES if the port is available; NO if not.
 */
- (BOOL)isPortAvailable:(NSString *)name;


/*!
	@method reservePort
	@abstract Marks a port as reserved.
	@discussion If the passed port name is already reserved, then NO is returned.
	@param name Pointer to the port name to reserve.
	@param object Pointer to the object which will "own" the port.
	@result YES if the port was properly reserved; NO if not.
 */
- (BOOL)reservePort:(NSString *)name:(id)object;


/*!
	@method releasePort
	@abstract Releases a previously reserved port.
	@discussion If the passed port name is already released, then this method does nothing.
	@param name Pointer to the port name to release.
	@result YES if the port was properly released; NO if not.
 */
- (BOOL)releasePort:(NSString *)name;

// Internal class methods
+ (NSMutableArray *)harvestSerialDevices;
+ (NSMutableArray *)harvestSerialNames;

@end
