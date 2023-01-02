//
//  VSerialChannel.h
//  DriveWire MacServer
//
//  Created by Boisy Pitre on 4/15/15.
//
//

#import <Foundation/Foundation.h>

@interface VSerialChannel : NSObject

@property (assign) NSUInteger number;
@property (strong) NSMutableData *incomingBuffer; // incoming TO CoCo
@property (strong) NSMutableData *outgoingBuffer; // outgoing FROM CoCo
@property (assign) NSUInteger waitCounter;

- (id)initWithNumber:(NSUInteger)number;

- (BOOL)hasData;
- (u_char)getByte;
- (void)putByte:(u_char)byte;
- (void)putBytes:(u_char *)bytes length:(NSUInteger)length;

- (void)open;
- (void)close;

@end
