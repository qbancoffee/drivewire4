/*--------------------------------------------------------------------------------------------------
//
//   File Name   :   TBSerialManager.h
//
//   Description :   Serial manager.
//
//--------------------------------------------------------------------------------------------------
//
//  Copyright (c) 2007 Tee-Boy
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
//  $Id$
//------------------------------------------------------------------------------------------------*/

/*!
	@header TBSerialManager.h
	@copyright BP
	@abstract
	@discussion
	@updated 2007-06-25
 */

#import <Foundation/Foundation.h>

#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <paths.h>
#include <termios.h>
#include <sysexits.h>
#include <sys/param.h>
#include "TBSerialPort.h"

	
/*!
	@class TBSerialManager
	@discussion The TBSerialManager class manages access to serial
	port devices.
 */
@interface TBSerialManager : NSObject
{
	NSMutableDictionary		*portList;
	Boolean					allowedToRun;
	NSLock					*serialLock;
}


/*!
	@method init
	@abstract Initializes the class.
	@result The pointer to the initialized object.
 */
- (id)init;


#pragma mark Query methods

/*!
	@method doesPortExist
	@abstract Determines if a serial port exists.
	@param name Pointer to the port name to verify.
	@discussion This method determines if a particular port exists on the
	system.  The passed name must be a service name, and not a device name.
	@result YES if the port exists, otherwise NO.
*/
- (BOOL)doesPortExist:(NSString *)name;


/*!
	@method isPortAvailable
	@abstract Determines if the passed port name is available for use.
	@discussion Ports are available only if they are not already opened by
	another thread.  This method will alert the caller to the
	availability of a named port.
	@result YES if the port is available; otherwise NO.
*/
- (BOOL)isPortAvailable:(NSString *)name;


/*!
	@method reservePort
	@abstract Marks a port as reserved.
	@discussion Reserving a serial port marks that port as being in use. It
	cannot be reserved again until it is released with <i>releasePort</i>.
	If the passed port name is already reserved, then <i>nil</i> is returned.
	@param name Pointer to the port name to reserve.
	@param object Pointer to the object which will "own" the port.
	@result id of the port if it was properly reserved; nil if not.
 */
- (TBSerialPort *)reservePort:(NSString *)name forOwner:(id)object;


/*!
	@method releasePort
	@abstract Releases a previously reserved port.
	@discussion This method will release a previously reserved port so that
	it can be claimed by another thread.  If the passed port name is already
	released, then this method does nothing.
	@param name Pointer to the port name to release.
	@result YES if the port was properly released; NO if not.
 */
- (BOOL)releasePort:(NSString *)name;


/*!
	@method availablePorts
	@abstract Returns a dictionary of available ports.
	@discussion Applications that use serial ports are typically interested
	in allowing the user to select from a list of available ports on the
	system. This method returns an NSDictionary that contains key/value
	pairs of the port name/device name of each serial port on the system,
	whether they are reserved or not.
	@result A dictionary containing the port name as key and device name as
	value.
*/
+ (NSMutableDictionary *)availablePorts;

@end
