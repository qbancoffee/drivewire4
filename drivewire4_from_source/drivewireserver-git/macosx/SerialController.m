//
//  SerialController.m
//  DriveWire
//
//  Created by Boisy Pitre on Fri Feb 14 2003.
//  Copyright (c) 2003 AES. All rights reserved.
//

#import "SerialController.h"
#import <IOKit/serial/IOSerialKeys.h>

#include <sys/param.h>
#include <sysexits.h>
#include <pthread.h>


@implementation SerialController


- (id)init
{
    int	status = -1;
    int i;
    
    
    if (self = [super init])
    {
        // Harvest all serial devices on the system.
        
        serialDeviceArray = [SerialController harvestSerialDevices];
        serialNameArray = [SerialController harvestSerialNames];
		objectMappingArray = [[NSMutableArray alloc] init];
	}
        
    
    
    // Return self.
    
    return self;
}


#pragma mark Query methods

- (BOOL)doesPortExist:(NSString *)name
{
	int i, count = [serialNameArray count];
	
	
	for (i = 0; i < count; i++)
	{
		NSString *namePtr = [serialNameArray objectAtIndex:i];
		
		if ([name isEqualToString:namePtr] == YES)
		{
			return YES;
		}
	}
	
	
    return NO;
}



- (BOOL)isPortAvailable:(NSString *)name
{
	// First, check to see if the port exists.
	
	int i, count = [serialNameArray count];
	
	
	for (i = 0; i < count; i++)
	{
		NSString *namePtr = [serialNameArray objectAtIndex:i];
		
		if ([name isEqualToString:namePtr] == YES)
		{
			id owner = [objectMappingArray objectAtIndex:i];
			
			if (owner == nil)
			{
				return YES;
			}
			else
			{
				return NO;
			}
		}
	}
	
	
	return NO;
}



#pragma mark Action methods

- (BOOL)reservePort:(NSString *)name:(id)object
{
	int i, count = [serialNameArray count];
	
	
	for (i = 0; i < count; i++)
	{
		NSString *namePtr = [serialNameArray objectAtIndex:i];
		
		if ([name isEqualToString:namePtr] == YES)
		{
			id owner = [objectMappingArray objectAtIndex:i];
			
			if (owner == nil)
			{
				owner = object;
				
				return YES;
			}
			else
			{
				return NO;
			}
		}
	}
	
	
    return NO;
}



- (BOOL)releasePort:(NSString *)name
{
	int i, count = [serialNameArray count];
	
	
	for (i = 0; i < count; i++)
	{
		NSString *namePtr = [serialNameArray objectAtIndex:i];
		
		if ([name isEqualToString:namePtr] == YES)
		{
			id owner = [objectMappingArray objectAtIndex:i];
			
			if (owner != nil)
			{
				owner = nil;
				
				return YES;
			}
		}
	}

	
	return NO;
}



#if 0
/*!
	@method initWithPort
	@abstract Initializes the class with the port name and baud rate
	@discussion This is the designated initializer for this class.
	@result The pointer to the object.
 */
- (id)initWithPort:(NSString *)portName:(int)baudRate:(NSString *)serialProtocol
{
    int	status = -1;
    int i;
    
    
    if (self = [super init])
    {
        // Allocate and initialize our instance of SerialPort.
                
        port = [[SerialPort alloc] init];

        if (port == nil)
        {
            return nil;
        }
        
        
        // Harvest all serial devices on the system.
        
        availableSerialDevices = [SerialController harvestSerialDevices];
        availableSerialNames = [SerialController harvestSerialNames];
        
        
        // Determine if the passed name matches any of our port names.
        
        int count = [availableSerialNames count];
        
        for (i = 0; i < count; i++)
        {
            NSString *namePtr = [availableSerialNames objectAtIndex:i];
            
            if ([portName isEqualToString: namePtr] == YES)
            {
                break;
            }
        }

    
        // Verify that we found a match.
    
        if (i < count)
        {
            // We found a match, so get the device name.

            NSString *deviceNamePtr = [availableSerialDevices objectAtIndex:i];

            if (deviceNamePtr != nil)
            {
                status = [port acquirePort: deviceNamePtr];
                
                if (status != -1)
                {
                    // Set up serial port.

                    [port setBaudRate:baudRate];

                    if ([serialProtocol isEqualToString:@"8N1"] == YES)
                    {
                        [port setWordSize:8];
                        [port setParity:parityNone];
                        [port setStopBits:1];
                    }
                    else if ([serialProtocol isEqualToString:@"7E1"] == YES)
                    {
                        [port setWordSize:7];
                        [port setParity:parityEven];
                        [port setStopBits:1];
                    }

                    [port setMinimumReadBytes:0];
                    [port setReadTimeout:1];


                    // Spin off listener thread.
                    
                    allowedToRun = TRUE;
                    
                    serialLock = [[NSLock alloc] init];
                    
                    [NSThread detachNewThreadSelector:@selector(listener:) toTarget:self withObject:nil];
                }
            }
        }
    }
    
    
    // Return self.
    
    return self;
}



/*!
	@method listener
	@abstract Internal thread method that gathers incoming serial data.
	@discussion This method should run on its own thread.  It listens to any data coming in from the port, then packages that data and sends it via a notification object.
	@param id Pointer to the object to which the data will be sent via notification.
 */
- (void)listener:(id)anObject
{
    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
    NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
    const int readBufferSize = 300;
    char readBuffer[readBufferSize];
    NSData *serialData;
    NSDictionary *dictionary;

     
	NSLog(@"Entering Thread...\n");


    // Lock access to this thread.
    
    [serialLock lock];


    // Do processing so long as we are allowed to run.

    while (allowedToRun == TRUE)
    {
        int maxsize = 0;

        
        // Read up to 'maxsize' bytes from the port.
            
        maxsize = [port readData :readBuffer :readBufferSize];

        if (maxsize > 0)
        {
            // Package serial data into an NSData object

            serialData = [NSData dataWithBytesNoCopy:readBuffer length:maxsize freeWhenDone:NO];


            // Put that data into a new dictionary.
            
            dictionary = [NSDictionary dictionaryWithObject:serialData forKey:@"Data"];


            // Post the dictionary to the notification object.
            
            [nc postNotificationName:@"SerialDataReady" object:self userInfo:dictionary];
        }
    }
        
    [port releasePort];
    
    [pool release];
    

    // Unlock the lock.
    
    [serialLock unlock];

	NSLog(@"Exiting Thread...\n");
}



/*!
	@method dealloc
	@abstract Releases the thread resource before the instantiated class exits.
 */
- (void)dealloc
{
    allowedToRun = FALSE;
    
    // Wait for the lock.
    
    [serialLock lock];

    [super dealloc];

	
    return;
}



// Allows the caller to write data to the serial port.

/*!
	@method sendToPort
	@abstract Sends data to the serial port.
	@param UserBuffer The pointer to the buffer containing the data to send.
 */
- (int)sendToPort:(char *)userBuffer: (int)numBytes
{
    [port writeData :userBuffer :numBytes];
    
    return 0;
}
#endif



// The rest of this file contains methods and functions that should be internal to this class.

static kern_return_t MyFindSerialPorts(io_iterator_t *matchingServices, mach_port_t *masterPort);


/*!
	@method harvestSerialDevices
	@abstract Determines what serial devices are on the system.
	@result An array of serial port names available on the system.
 */
+ (NSMutableArray *)harvestSerialDevices
{
    int		count = 0;
    mach_port_t masterPort = NULL;
    kern_return_t kernResult;
    NSMutableArray *devices;


    // Init the arrays.

    devices = [[NSMutableArray alloc] init];

    
    io_iterator_t serialPortIterator = NULL;

    kernResult = MyFindSerialPorts(&serialPortIterator, &masterPort);

    if  (kernResult == kIOReturnSuccess)
    {
        io_object_t		rs232Service;


        while (rs232Service = IOIteratorNext(serialPortIterator))
        {
            CFTypeRef	bsdPathAsCFString;
    
            count++;
            
            bsdPathAsCFString = IORegistryEntryCreateCFProperty
            (
                rs232Service,
                CFSTR(kIOCalloutDeviceKey),
                kCFAllocatorDefault,
                0
            );

            [devices addObject: (NSString *)bsdPathAsCFString]; 
            CFRelease(bsdPathAsCFString);

            IOObjectRelease(rs232Service);
        }
    }

    
    IOObjectRelease(serialPortIterator);

    
    return devices;
}



/*!
	@method harvestSerialNames
	@abstract Acquires names of available serial ports on the system.
	@result An array of serial port names.
 */
+ (NSMutableArray *)harvestSerialNames
{
    int		count = 0;
    mach_port_t masterPort = NULL;
    kern_return_t kernResult;
    NSMutableArray *names;


    // Init the arrays.

    names = [[NSMutableArray alloc] init];

    
    io_iterator_t serialPortIterator = NULL;

    kernResult = MyFindSerialPorts(&serialPortIterator, &masterPort);

    if  (kernResult == kIOReturnSuccess)
    {
        io_object_t		rs232Service;


        while (rs232Service = IOIteratorNext(serialPortIterator))
        {
            CFTypeRef	bsdPathAsCFString;
    
            count++;
            
            bsdPathAsCFString = IORegistryEntryCreateCFProperty
            (
                rs232Service,
                CFSTR(kIOTTYBaseNameKey),
                kCFAllocatorDefault,
                0
            );
        
            [names addObject: (NSString *)bsdPathAsCFString]; 
        
            CFRelease(bsdPathAsCFString);
            IOObjectRelease(rs232Service);
        }
    }

    
    IOObjectRelease(serialPortIterator);

    
    return names;
}



static kern_return_t MyFindSerialPorts(io_iterator_t *matchingServices, mach_port_t *masterPort)
{
    kern_return_t kernResult; 
    CFMutableDictionaryRef classesToMatch = NULL;


    *matchingServices = NULL;

    kernResult = IOMasterPort(NULL, masterPort);

    if (kernResult != KERN_SUCCESS)
    {
        printf("IOMasterPort returned %d\n", kernResult);

        return kernResult;
    }
    
    
    // The provider class for serial devices is IOSerialBSDClient.

    classesToMatch = IOServiceMatching(kIOSerialBSDServiceValue);

    if (classesToMatch == NULL)
    {
        printf("IOServiceMatching returned a NULL dictionary.\n");

        return EX_UNAVAILABLE;
    }
    else
    { 
        CFDictionarySetValue
        (
            classesToMatch,
            CFSTR(kIOSerialBSDTypeKey),
            CFSTR(kIOSerialBSDAllTypes)
//            CFSTR(kIOSerialBSDRS232Type)
        );
    }
    
    
    kernResult = IOServiceGetMatchingServices
    (
        *masterPort,
        classesToMatch,
        matchingServices
    );

    if ( (kernResult != KERN_SUCCESS) || (*matchingServices == NULL) )
    {
        if (kernResult == KERN_SUCCESS)
        {
            kernResult = EX_UNAVAILABLE; // Make sure error if no serial ports.
        }
    }
    
    
    return kernResult;
}

@end
