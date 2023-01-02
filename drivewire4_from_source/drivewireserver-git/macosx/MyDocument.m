//
//  MyDocument.m
//  DriveWire
//
//  Created by Boisy Pitre on 12/23/04.
//  Copyright __MyCompanyName__ 2004 . All rights reserved.
//

#import "MyDocument.h"

@implementation MyDocument

// Initializion routine
- (id)init;
{
   self = [super init];

   if (self)
	{
		// All of our initialization is done in windowControllerDidLoadNib
	}

   return self;
}


- (void)dealloc;
{
	NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
	NSMutableArray *driveArray = [dwModel driveArray];
	int i;
	
	// Remove observer of printer messages
	[nc removeObserver:printerWindowController name:@"DWPrint" object:dwModel];
   
	// Remove observer of statistics messages
	[nc removeObserver:statsView name:@"DWStats" object:dwModel];
		
	// Remove observer of log messages
	[nc removeObserver:logView name:@"DWStats" object:dwModel];
	
	for (i = 0; i < DRIVE_COUNT; i++)
	{
		// Remove ourself as an observer of cartridge insert/eject messages for each drive
		[nc removeObserver:self name:@"cartridgeWasInserted" object:[driveArray objectAtIndex:i]];
		[nc removeObserver:self name:@"cartridgeWasEjected" object:[driveArray objectAtIndex:i]];
	}	
	
   [dwModel release];
   
   [myWindowController release];
   
	[super dealloc];
}


- (NSString *)windowNibName;
{
    // Override returning the nib file name of the document
    // If you need to use a subclass of NSWindowController or if your document supports multiple NSWindowControllers, you should remove this method and override -makeWindowControllers instead.
    return @"MyDocument";
}


- (void)windowControllerDidLoadNib:(NSWindowController *)aController;
{
	int i;
	NSString *currentPort, *portTitle = nil;
	
   myWindowController = [aController retain];
   
   if (dwModel == nil)
   {
      dwModel = [[DriveWireServerModel alloc] init];
   }
   
   [dwModel setDelegate:self];

	// Call super class
   [super windowControllerDidLoadNib:aController];
	
    // Request the array of ports from the Document Model	
	NSMutableDictionary *portNames = [dwModel availablePorts];

	// Remove all items from the port button
	[serialPortButton removeAllItems];
	
	// Get the selected serial port, if any
	currentPort = [dwModel serialPort];
   
	// Iterate through the list of names and add each to the list
	{
		NSEnumerator *e = [portNames keyEnumerator];
		NSString *n;
		
		[serialPortButton addItemWithTitle:@"No Device"];

		while ((n = [e nextObject]))
		{
#ifdef DEBUG
			NSLog(@"%@ is available\n", n);
#endif
			[serialPortButton addItemWithTitle:n];	
			if (currentPort != nil && [currentPort compare:n] == NSOrderedSame)
			{
				portTitle = n;
			}
		}
	}

   [portNames release];
   
	// Select the model's port, if not nil, else select the 0th indexed item ("No Device")
	if (portTitle == nil)
	{
		[serialPortButton selectItemAtIndex:0];
		lastPortSelected = 0;
	}
	else
	{
		[serialPortButton selectItemWithTitle:portTitle];
		lastPortSelected = [serialPortButton indexOfSelectedItem];
	}

	// Get the array of drives from the Document Model
	NSMutableArray *driveArray = [dwModel driveArray];
	
	NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
	
	for (i = 0; i < DRIVE_COUNT; i++)
	{
		[driveView addSubview:[[driveArray objectAtIndex:i] view]];

		// Add ourself as an observer of cartridge insert/eject messages for each drive
		[nc addObserver:self selector:@selector(driveNotification:) name:@"cartridgeWasInserted" object:[driveArray objectAtIndex:i]];
		[nc addObserver:self selector:@selector(driveNotification:) name:@"cartridgeWasEjected" object:[driveArray objectAtIndex:i]];
#ifdef DEBUG
		NSLog(@"Observing for object: 0x%X", [driveArray objectAtIndex:i]);
#endif
	}	
	
	// Add the statsView as an observer of statistics messages
	[nc addObserver:statsView selector:@selector(updateStats:) name:@"DWStats" object:dwModel];

	// Add the logView as an observer of log messages
	[nc addObserver:logView selector:@selector(updateLog:) name:@"DWStats" object:dwModel];

   // Add the printerWindowController as an observer of print messages
   [nc addObserver:printerWindowController selector:@selector(updatePrintBuffer:) name:@"DWPrint" object:dwModel];
	
//   [debugDrawer open];
   
   // Hide logging if its not turned on
   NSWindow *documentWindow = [myWindowController window];
   NSRect frame = [documentWindow frame];
   if ([dwModel logState] == FALSE)
   {
      frame.size.height -= [[logView superview] frame].size.height + 30;
      frame.origin.y += [[logView superview] frame].size.height + 30;
   }
   
   // Hide statistics if its not turned on
   if ([dwModel statState] == FALSE)
   {
      frame.size.width -= [[statsView superview] frame].size.width + 20;
   }

   [documentWindow setFrame:frame display:TRUE animate:FALSE];
   
   [self updateUIComponents];
}


#pragma mark DriveWire Protocol Delegate Methods

- (void)updateInfoView:(NSDictionary *)info;
{
   [logView update:info];
   [statsView update:info];
}

- (void)updateMemoryView:(NSDictionary *)info;
{
   [debuggerWindowController updateMemory:info];
}

- (void)updateRegisterView:(NSDictionary *)info;
{
   [debuggerWindowController updateRegisters:info];
}

- (void)updatePrinterView:(NSDictionary *)info;
{
   [printerWindowController updatePrintBuffer:[info objectForKey:@"PrintData"]];
}

- (void)updateUIComponents;
{
   [loggingSwitch setState:[dwModel logState]];
   [statSwitch setState:[dwModel statState]];

   switch ([dwModel machineType])
   {
      case 1:
         [speedSwitch setTitle:@"CoCo 1 @ 38400 bps"];
         [speedSwitch setImage:[NSImage imageNamed:@"CoCo1"]];
         break;
      case 2:
         [speedSwitch setTitle:@"CoCo 2 @ 57600 bps"];
         [speedSwitch setImage:[NSImage imageNamed:@"CoCo2"]];
         break;
      case 3:
      default:
         [speedSwitch setTitle:@"CoCo 3 @ 115200 bps"];
         [speedSwitch setImage:[NSImage imageNamed:@"CoCo3"]];
         break;
   }
}

- (NSData *)dataRepresentationOfType:(NSString *)aType;
{
	return [NSArchiver archivedDataWithRootObject:dwModel];
}

- (BOOL)loadDataRepresentation:(NSData *)data ofType:(NSString *)aType;
{
	dwModel = [[NSUnarchiver unarchiveObjectWithData:data] retain];
	
	return YES;
}

- (IBAction)setSerialPort:(id)sender;
{
	NSString *thePort = [serialPortButton titleOfSelectedItem];
	
	// Ask the Document Model to open thePort
	if ([dwModel setCommPort:thePort] == NO)
	{
		NSAlert *errorAlert = [NSAlert alertWithMessageText:@"Communications Error"
											   defaultButton:@"OK"
											alternateButton:nil
												otherButton:nil
								  informativeTextWithFormat:@"The port \"%@\" failed to open.", thePort];
		
		[errorAlert runModal];
		
		[serialPortButton selectItemAtIndex:lastPortSelected];
		
		return;
	}
	
	lastPortSelected = [sender indexOfSelectedItem];

	[self updateChangeCount:NSChangeDone];
}

- (void)driveNotification:(NSNotification *)note;
{
	[self updateChangeCount:NSChangeDone];
}

- (IBAction)setCoCoType:(id)sender;
{
   int machineType = [dwModel machineType] + 1;
   if (machineType > 3) machineType = 1;
   [dwModel setMachineType:machineType];
   
   [self updateUIComponents];
}

- (IBAction)setLogSwitch:(id)sender;
{
   NSButton *b = (NSButton *)sender;
   [dwModel setLogState:[b state]];

   NSWindow *documentWindow = [myWindowController window];
   NSRect frame = [documentWindow frame];
   if ([b state] == TRUE)
   {
      frame.size.height += [[logView superview] frame].size.height + 30;
      frame.origin.y -= [[logView superview] frame].size.height + 30;
   }
   else
   {
      frame.size.height -= [[logView superview] frame].size.height + 30;
      frame.origin.y += [[logView superview] frame].size.height + 30;
   }
   [documentWindow setFrame:frame display:TRUE animate:TRUE];

   [self updateUIComponents];
}

- (IBAction)setStatsSwitch:(id)sender;
{
   NSButton *b = (NSButton *)sender;
   [dwModel setStatState:[b state]];

   NSWindow *documentWindow = [myWindowController window];
   NSRect frame = [documentWindow frame];
   if ([b state] == TRUE)
   {
      frame.size.width += [[statsView superview] frame].size.width + 20;
   }
   else
   {
      frame.size.width -= [[statsView superview]  frame].size.width + 20;
   }
   [documentWindow setFrame:frame display:TRUE animate:TRUE];

   [self updateUIComponents];
}

- (IBAction)goCoCo:(id)sender;
{
   [debugDrawer close];
   [dwModel goCoCo];
}

@end
