/*--------------------------------------------------------------------------------------------------
//
//   File Name   :   TBSerialManager.m
//
//   Description :   Serial port manager.
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
	@updated 2005-05-29
	@meta http-equiv=”refresh” content=”0;http://www.apple.com”
 */


#import "TBSerialManager.h"
#import <IOKit/serial/IOSerialKeys.h>

#include <sys/param.h>
#include <sysexits.h>
#include <pthread.h>

@implementation TBSerialManager

- (id)init;
{
    if ((self = [super init]))
    {
		NSMutableDictionary	*portsISee;
		NSEnumerator *oEnumerator, *kEnumerator;
		NSString *oValue, *kValue;
        
		// allocate space for port dictionary
		portList = [[NSMutableDictionary alloc] init];
		
		// get an dictionary of all ports and their device names.
        portsISee = [[TBSerialManager availablePorts] retain];

		oEnumerator = [portsISee objectEnumerator];
		kEnumerator = [portsISee keyEnumerator];

		// sift through each port and create a TBSerialPort
		while ((kValue = [kEnumerator nextObject]))
		{
			TBSerialPort *port;
			
			oValue = [oEnumerator nextObject];

#ifdef DEBUG
			NSLog(@"Device name '%@' for port '%@'", oValue, kValue);
#endif
			port = [[TBSerialPort alloc] initWithDeviceName:oValue serviceName:kValue];
			[portList setValue:port forKey:kValue];
			[port release];
		}
		
		[portsISee release];
	}
        
    return self;
}

- (void)dealloc;
{
	NSEnumerator *e = [portList objectEnumerator];
	TBSerialPort *p;
	
	while ((p = [e nextObject]))
	{
		[p closePort];
	}
	
	[portList release];
	[super dealloc];
}

#pragma mark -
#pragma mark Query methods

- (BOOL)doesPortExist:(NSString *)name;
{
	if ([portList objectForKey:name] != nil)
	{
		return YES;
	}
	
    return NO;
}

- (BOOL)isPortAvailable:(NSString *)name;
{
	TBSerialPort *port = [portList objectForKey:name];
	
	if ([port owner] == nil)
	{
		return YES;
	}
	
	return NO;
}

#pragma mark -
#pragma mark Action methods

- (TBSerialPort *)reservePort:(NSString *)name forOwner:(id)object;
{
	NSError *error = nil;
	TBSerialPort *port = [portList objectForKey:name];
	
	if ([port owner] == nil)
	{
		if ([port openPort:object error:&error] == NO)
		{
			return nil;
		}
	}
	else
	{
		port = nil;
	}
		
	return port;
}

- (BOOL)releasePort:(NSString *)name;
{
	TBSerialPort *port = [portList objectForKey:name];
	
	return [port closePort];
}

// The rest of this file contains methods and functions that should be internal to this class.

static kern_return_t MyFindSerialPorts(io_iterator_t *matchingServices, mach_port_t *masterPort);

+ (NSMutableDictionary *)availablePorts;
{
    int		count = 0;
    mach_port_t masterPort = (mach_port_t)0x0;
    kern_return_t kernResult;
    NSMutableDictionary *names;

    // init the arrays.
    names = [[[NSMutableDictionary alloc] init] autorelease];
    io_iterator_t serialPortIterator = 0;
    kernResult = MyFindSerialPorts(&serialPortIterator, &masterPort);
    if  (kernResult == kIOReturnSuccess)
    {
        io_object_t		rs232Service;

        while ((rs232Service = IOIteratorNext(serialPortIterator)))
        {
            CFTypeRef	bsdPathAsCFString, serviceNameAsCFString;
    
            count++;            
            serviceNameAsCFString = IORegistryEntryCreateCFProperty
            (
                rs232Service,
                CFSTR(kIOTTYBaseNameKey),
                kCFAllocatorDefault,
                0
            );
        
            bsdPathAsCFString = IORegistryEntryCreateCFProperty
			(
				 rs232Service,
				 CFSTR(kIOCalloutDeviceKey),
				 kCFAllocatorDefault,
				 0
			);
			
            [names setValue: (NSString *)bsdPathAsCFString
				forKey:(NSString *)serviceNameAsCFString]; 
        
            CFRelease(bsdPathAsCFString);
            CFRelease(serviceNameAsCFString);
			
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

    *matchingServices = 0;
    kernResult = IOMasterPort(0, masterPort);
    if (kernResult != KERN_SUCCESS)
    {
        NSLog(@"IOMasterPort returned %d", kernResult);

        return kernResult;
    }
    
    // the provider class for serial devices is IOSerialBSDClient.
    classesToMatch = IOServiceMatching(kIOSerialBSDServiceValue);

    if (classesToMatch == NULL)
    {
        NSLog(@"IOServiceMatching returned a NULL dictionary.");

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

    if ((kernResult != KERN_SUCCESS) || (*matchingServices == 0))
    {
        if (kernResult == KERN_SUCCESS)
        {
            kernResult = EX_UNAVAILABLE; // Make sure error if no serial ports.
        }
    }
    
    return kernResult;
}

@end
