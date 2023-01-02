//
//  DriveWireServerModel.h
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
#import <TeeBoy/TBSerialPort.h>
#import <TeeBoy/TBSerialManager.h>
#import "TBVirtualDriveController.h"
#import "VSerialChannel.h"

#define DW_DEFAULT_VERSION       3

#define	_OP_NOP                 '\0'
//#define	_OP_RESYNC              '\1'
#define	_OP_TIME                '#'
#define	_OP_INIT                'I'
#define	_OP_TERM                'T'
#define	_OP_READ                'R'
#define	_OP_READEX              'R'+128     /* DW3 */
#define	_OP_WRITE               'W'
#define	_OP_REREAD              'r'
#define	_OP_REREADEX            'r'+128     /* DW3 */
#define	_OP_REWRITE             'w'
#define	_OP_GETSTAT             'G'
#define	_OP_SETSTAT             'S'
#define	_OP_RESET3              248         /* DW3 */
#define	_OP_RESET1              254
#define	_OP_RESET2              255
#define	_OP_WIREBUG_MODE        'B'

// Printer extension definitions
#define	_OP_PRINTFLUSH          'F'
#define	_OP_PRINT               'P'

// Virtual Port extension definitions
#define	_OP_SERINIT				0x45
#define	_OP_SERTERM				0xC5
#define	_OP_SERGETSTAT			0x44
#define	_OP_SERSETSTAT			0xC4
#define	_OP_SERTERM				0xC5
#define	_OP_SERREAD				0x43
#define	_OP_SERWRITE			0xC3
#define	_OP_SERWRITEM			0x64

// WireBug definitions
#define	_OP_WIREBUG_READREGS    'R'
#define	_OP_WIREBUG_WRITEREGS	'r'
#define	_OP_WIREBUG_READMEM     'M'
#define	_OP_WIREBUG_WRITEMEM    'm'
#define	_OP_WIREBUG_GO          'G'

#define _OP_DWINIT				 0x5A
#define _OP_NAMEOBJ_MOUNT        0x01 /* Named Object Mount */
#define _OP_NAMEOBJ_CREATE       0x02 /* Named Object Create */


#define E_ILLNUM                 16
#define E_CRC                    243

@protocol DriveWireDelegate

- (void)updateInfoView:(NSDictionary *)info;
- (void)updateMemoryView:(NSDictionary *)info;
- (void)updateRegisterView:(NSDictionary *)info;
- (void)updatePrinterView:(NSDictionary *)info;

@end

/*!
	@class DriveWireServerModel
	This class encapsulates the entire DriveWire protocol.
*/
@interface DriveWireServerModel : NSObject
{
	TBSerialPort			*fPort;
	NSMutableDictionary	*fSerialPortNames;
	NSString             *fCurrentPort;
	
	Boolean					statState;
	Boolean					logState;
	Boolean					wirebugState;
	int						machineType;

	NSMutableArray			*driveArray;
	NSFileHandle			*portDelegate;
	
	// Protocol management variables
    const u_char			*dataBytes;
    int						dataLength;
    SEL						currentState;
	Boolean					validateWithCRC;
	NSTimer					*watchDog;

	// Statistics Dictionary
	NSMutableDictionary	*statistics;

	// Registers Dictionary
	NSMutableDictionary	*registers;

	// Server version
	int						version;

	// Response used for OP_READEX
   uint16_t             readexChecksum;
	unsigned char        readexResponse;

	// Flags used for OP_WIREBUG_MODE
	unsigned char        wirebugOpcode;
	unsigned char        wirebugCoCoType;
	unsigned char        wirebugCPUType;
	
	// Registers used for WireBug
	u_int8_t	_cc, _dp, _a, _b, _e, _f, _md;
	u_int16_t	_v, _x, _y, _u, _s, _pc;
	// Address of memory to view
	u_int16_t	memAddress;

	int nameobj_size;
	int driveCount;
	
   NSMutableData *printBuffer;
   
}

@property (assign) id<DriveWireDelegate> delegate;
@property (strong) NSArray *serialChannels;

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


/*!
	@method setMemAddress
	@abstract Sets the memory address used to inspect memory.
	@param new_address The new address.
 */
- (void)setMemAddress:(u_int16_t)new_address;


- (void)setMachineType:(int)whichCoCo;
- (Boolean)setCommPort:(NSString *)thePort;
- (void)setStatState:(Boolean)state;
- (void)setLogState:(Boolean)state;
- (void)setWirebugState:(Boolean)state;
- (NSString *)serialPort;
- (int)machineType;
- (Boolean)statState;
- (Boolean)logState;
- (Boolean)wirebugState;

- (void)goCoCo;

@end

