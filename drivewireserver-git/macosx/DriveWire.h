//
//  DriveWire.h
//  DriveWire
//
//  This class processes the DriveWire protocol as defined by spec.
//
//  It interfaces to the outside through the object:
//		portDelegate (called to read/write data from/to the "client")
//
//  Data can be provided asynchronously to it via the "dataReady" method.
//
//
//  Created by Boisy Pitre on 12/8/04.
//  Copyright 2008 BGP. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TBSerial/TBSerialPort.h>
#import <TBVirtualDrive/TBVirtualDriveController.h>


#define DW_DEFAULT_VERSION	2

#define	DRIVE_COUNT			4

#define	_OP_NOP				'\0'
#define	_OP_RESYNC			'\1'
#define	_OP_TIME			'#'
#define	_OP_INIT			'I'
#define	_OP_TERM			'T'
#define	_OP_READ			'R'
#define	_OP_READEX			'R'+128
#define	_OP_WRITE			'W'
#define	_OP_REREAD			'r'
#define	_OP_REREADEX		'r'+128
#define	_OP_REWRITE			'w'
#define	_OP_GETSTAT			'G'
#define	_OP_SETSTAT			'S'
#define	_OP_RESET1			254
#define	_OP_RESET2			255
#define	_OP_WIREBUG_MODE    'B'

// WireBug definitions
#define	_OP_WIREBUG_READREGS	'R'
#define	_OP_WIREBUG_WRITEREGS	'r'
#define	_OP_WIREBUG_READMEM	'M'
#define	_OP_WIREBUG_WRITEMEM	'm'
#define	_OP_WIREBUG_GO		'G'


#define E_ILLNUM			16
#define E_CRC				243

/*!
	@class DriveWireServer
	This class encapsulates the entire DriveWire protocol.
*/
@interface DriveWireServer : NSObject
{
	NSMutableArray			*driveArray;
	NSFileHandle			*portDelegate;
	
	// Protocol management variables
    const u_char			*dataBytes;
    int						dataLength;
    NSString				*currentState;
	Boolean					validateWithCRC;
	NSTimer					*watchDog;

	// Tracks if we are in debug mode
	Boolean					debugMode;

	// Statistics Dictionary
	NSMutableDictionary		*statistics;

	// Registers Dictionary
	NSMutableDictionary		*registers;

	// Server version
	int						version;

	// Flag used for OP_READEX
	int						postRead;

	// Flags used for OP_WIREBUG_MODE
	unsigned char     wirebugOpcode;
	unsigned char		wirebugCoCoType;
	unsigned char		wirebugCPUType;
	
	// Registers use for WireBug
	u_int8_t	_cc, _dp, _a, _b, _e, _f, _md;
	u_int16_t	_v, _x, _y, _u, _s, _pc;
	
}



/*!
	@method init
	@abstract Initializes the object.
 */
- (id)init;



/*!
	@method initWithVersion
	@abstract Initializes the object with a specific DriveWire version.
 */
- (id)initWithVersion:(int)versionNumber;



/*!
	@method driveArray
	@abstract Returns a pointer to the virtual drive array.
	@result A pointer to the virtual drive array is returned.
 */
- (NSMutableArray *)driveArray;



/*!
	@method portDelegate
	@abstract Returns the serial port.
	@result A pointer to the serial port object.
 */
- (id)portDelegate;



/*!
	@method setPortDelegate
	@abstract Sets up the port handler that will be used.
	@param new_handler A pointer to the new handler.
 */
- (void)setPortDelegate:(id)new_handler;


// Private methods
- (void)initDesignated;
- (uint16_t)compute16BitChecksum:(const u_char *)data :(int)length;
- (uint8_t)compute8BitChecksum :(const u_char *)data :(int)length;
- (uint16_t)computeCRC:(const u_char *)data :(int)length;

- (void)OP_OPCODE;
- (void)OP_NOP;
- (void)OP_RESET;
- (void)OP_INIT;
- (void)OP_TERM;
- (void)OP_TIME;
- (void)OP_RESYNC;
- (void)OP_READ;
- (void)OP_READEX;
- (void)OP_REREAD;
- (void)OP_REREADEX;
- (void)OP_WRITE;
- (void)OP_REWRITE;
- (void)OP_GETSTAT;
- (void)OP_SETSTAT;
- (void)statCommon:(NSString *)whichStat;
- (void)OP_WIREBUG_GO_RESPONSE;
- (void)OP_WIREBUG_READREGS_RESPONSE;
- (void)OP_WIREBUG_WRITEREGS_RESPONSE;
- (void)OP_WIREBUG_READMEM_RESPONSE;
- (void)OP_WIREBUG_WRITEMEM_RESPONSE;
- (void)sendRunTarget;

- (void)readRegistersFromCoCo;
- (void)goCoCo;

- (NSString *)statCodeToString:(int)code;

@end
