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

/*!
 @header TBSerialPort.h
 @abstract Header file for Serial Port access
 @discussion Include TBSerialPort.h to access the class for serial port access.
 @copyright Tee-Boy
 @updated 2007-06-25
 */

#include <unistd.h>
#include <fcntl.h>
#include <sys/filio.h>
#include <sys/ioctl.h>
#include <termios.h>

@class TBSerialPort;

@protocol TBSerialPortDelegate

- (void)serialPort:(TBSerialPort *)port didReceiveData:(NSData *)data;

@end

/*!
 @interface TBSerialPort
 @abstract Serial Port access class
 @discussion This class manages provides access to all available serial port
 devices.  It has been designed to provide a clear and concise interface
 for applications that need serial communication services.
 */
@interface TBSerialPort : NSObject
{
	int				_fd;				// file descriptor to path of serial device
	id				_owner;				// owner of the port
    NSString		*_deviceName;		// the OS-friendly name of the device
    NSString		*_serviceName;		// the view-friendly name of the device
    struct termios 	_originalTTYAttrs;	// original TTY attributes
    struct termios 	_ttyAttrs;			// maeleable TTY attributes
	BOOL			_allowedToRun;
	BOOL			_logIncomingBytes;
	BOOL			_logOutgoingBytes;
    BOOL            _threadIsRunning;
	id<TBSerialPortDelegate>				_delegate;
}

/*!
 @enum serialParity
 @abstract Constants for serial parity modes.
 @discussion These constants are used to set the parity mode of a serial
 port.
 */
typedef enum
	{
		parityOdd,
		parityEven,
		parityNone
	} serialParity;


// Port access methods

/*!
 @method initWithDeviceName:serviceName:
 @abstract Acquires a path to the specified serial port.
 @param deviceName A pointer to the string containing the port name.
 @param serviceName A pointer to the string containing the service name.
 @discussion This method opens a port and conditions it for immediate
 use.  The default settings are 9600 baud, 8 bit word size, no parity,
 and 1 stop bit.
 @result YES if the acquisition was successful; otherwise NO.
 */
- (id)initWithDeviceName:(NSString *)deviceName serviceName:(NSString *)serviceName;

/*!
 @method openPort:
 @abstract Acquires a path to the specified serial port.
 @param owner A pointer to the desired owner object of the port.
 @param error The address of a pointer to an error object.
 @discussion This method opens a port and conditions it for immediate
 use.  The default settings are 9600 baud, 8 bit word size, no parity,
 and 1 stop bit.
 @result YES if the port acquisition was successful; otherwise NO.  The error object is updated in the unsuccessful case.
 */
- (BOOL)openPort:(id)owner error:(NSError **)error;

/*!
 @method closePort
 @abstract Releases a previously acquired port.
 @result YES if port was closed successfully; otherwise NO.
 */
- (BOOL)closePort;

/*!
 @method delegate
 @abstract Returns a pointer to the delegate.
 @result The pointer to the delegate that was previously set.
 */
- (id)delegate;

/*!
 @method setDelegate:
 @param data A pointer to an object to be the delegate.
 @abstract Sets the delegate for the class.
 */
- (void)setDelegate:(id)_value;

/*!
 @method readData
 @abstract Obtains available data from the serial port.
 @result A pointer to the NSData object containing the data; nil of no data available.
 */
- (NSData *)readData;

/*!
 @method writeData:
 @param data A pointer to an NSData object of the data to be written.
 @abstract Obtains available data from the serial port.
 @result YES if data was written; otherwise NO.
 */
- (BOOL)writeData:(NSData *)data;

/*!
 @method writeString:
 @param data A pointer to an NSString object of the data to be written.
 @abstract Obtains available data from the serial port.
 @result YES if data was written; otherwise NO.
 */
- (BOOL)writeString:(NSString *)data;


// Query methods

/*!
 @method inputLogging
 @abstract Returns the state of input logging
 @result TRUE if logging is on; FALSE logging is off
 */
- (Boolean)inputLogging;

/*!
 @method setInputLogging:
 @abstract Turns on input logging and shows all bytes coming into the serial port
 @param value TRUE to turn on logging; FALSE to turn off logging.
 */
- (void)setInputLogging:(Boolean)value;

/*!
 @method outputLogging
 @abstract Returns the state of output logging
 @result TRUE if logging is on; FALSE logging is off
 */
- (Boolean)outputLogging;

/*!
 @method setOutputLogging:
 @abstract Turns on output logging and shows all bytes going out of serial port
 @param value TRUE to turn on logging; FALSE to turn off logging,
 */
- (void)setOutputLogging:(Boolean)value;

/*!
 @method bytesReady
 @abstract Returns the number of bytes available to read.
 @result The number of bytes that are ready for reading.
 */
- (int)bytesReady;

/*!
 @method isAcquired
 @abstract Returns the state of the port.
 @result YES if the port has been acquired; otherwise, NO.
 */
- (BOOL)isAcquired;

/*!
 @method owner
 @abstract Returns the owner of the port.
 @result owner ID, or nil if there is none.
 */
- (id)owner;

/*!
 @method dtrState:
 @abstract Returns the DTR state of the serial port.
 @result YES if DTR is high; otherwise NO.
 */
- (BOOL)dtrState;

/*!
 @method setDTRState:
 @abstract Sets the DTR state of the serial port.
 @param onOrOff TRUE to turn on DTR; FALSE to turn off.
 */
- (void)setDTRState:(BOOL)onOrOff;

/*!
 @method hardwareHandshaking:
 @abstract Returns the hardware handshaking state of the serial port.
 @result YES if the port was successfully set; otherwise, NO.
 */
- (BOOL)hardwareHandshaking;

/*!
 @method setHardwareHandshaking:
 @abstract Sets the hardware handshaking state of the serial port.
 @param onOrOff TRUE to turn on hardware handshaking; FALSE to turn off.
 */
- (void)setHardwareHandshaking:(BOOL)onOrOff;

/*!
 @method deviceName
 @abstract Returns the OS-friendly name of the serial port.
 @result The name of the serial port.
 */
- (NSString *)deviceName;

/*!
 @method serviceName
 @abstract Returns the user-friendly name of the serial port.
 @result The name of the serial port.
 */
- (NSString *)serviceName;

/*!
 @method baudRate
 @abstract Returns the baud rate of the serial port.
 @result The baud rate of the serial port.
 */
- (int)baudRate;

/*!
 @method setBaudRate:
 @abstract Sets the baud rate of the serial port.
 @param baudRate The baud rate to set the serial port to.
 @result YES if the port was successfully set; otherwise, NO.
 */
- (BOOL)setBaudRate:(int)baudRate;

/*!
 @method wordSize
 @abstract Returns the word size of the serial port.
 @result The word size of the serial port.
 */
- (int)wordSize;

/*!
 @method setWordSize
 @abstract Sets the word size of the serial port.
 @param wordSize The word size.
 @result YES if the port was successfully set; otherwise, NO.
 */
- (BOOL)setWordSize:(int)wordSize;

/*!
 @method parity
 @abstract Returns the parity of the serial port.
 @result The parity of the serial port.
 */
- (serialParity)parity;

/*!
 @method setParity:
 @abstract Sets the parity of the serial port.
 @param parity The desired parity.
 @result YES if the port was successfully set; otherwise, NO.
 */
- (BOOL)setParity:(serialParity)parity;

/*!
 @method stopBits
 @abstract Returns the serial port's stop bits setting.
 @result The number of stop bits.
 */
- (int)stopBits;

/*!
 @method setStopBits:
 @abstract Sets the number of stop bits of the serial port.
 @param stopBits The number of stop bits to set.
 @result YES if the port was successfully set; otherwise, NO.
 */
- (BOOL)setStopBits:(int)stopBits;

/*!
 @method readTimeout
 @abstract Returns the read timeout of the serial port.
 @result The read timeout in milliseconds.
 */
- (int)readTimeout;

/*!
 @method setReadTimeout:
 @param timeout The number of milliseconds to timeout if no data is present.
 @abstract Sets read timeout in milliseconds.
 @result YES if the port was successfully set; otherwise, NO.
 */
- (BOOL)setReadTimeout:(int)timeout;

/*!
 @method minimumReadBytes
 @abstract Returns the serial port's minimum read byte count.
 @result The minimum number of ready bytes is returned.
 */
- (int)minimumReadBytes;

/*!
 @method setMinimumReadBytes
 @abstract Sets the baud rate of the serial port.
 @param number The minimum number of read bytes to set.
 @result YES if the port was successfully set; otherwise, NO.
 */
- (BOOL)setMinimumReadBytes:(int)number;

@end
