/* MyDocument */

#import <Cocoa/Cocoa.h>
#import "VirtualDriveJukeBoxView.h"
#import "DriveWireServerModel.h"
#import "PrinterWindowController.h"
#import "DebuggerWindowController.h"
#import "LogView.h"
#import "StatsView.h"


@interface MyDocument : NSDocument <DriveWireProtocol>
{
   IBOutlet VirtualDriveJukeBoxView *driveView;
	IBOutlet NSPopUpButton *serialPortButton;
	IBOutlet NSButton	*speedSwitch;
	IBOutlet LogView	*logView;
	IBOutlet StatsView	*statsView;
   IBOutlet NSDrawer *debugDrawer;
   IBOutlet NSButton *loggingSwitch;
   IBOutlet NSButton *statSwitch;
	int lastPortSelected;
   NSWindowController *myWindowController;
   IBOutlet DebuggerWindowController *debuggerWindowController;
   IBOutlet PrinterWindowController *printerWindowController;
   
	DriveWireServerModel	*dwModel;
}

- (void)updateInfoView:(NSDictionary *)info;
- (void)updateMemoryView:(NSDictionary *)info;
- (void)updateRegisterView:(NSDictionary *)info;
- (void)updatePrinterView:(NSDictionary *)info;

- (void)updateUIComponents;
- (IBAction)setCoCoType:(id)sender;
- (IBAction)setLogSwitch:(id)sender;
- (IBAction)setStatsSwitch:(id)sender;
- (IBAction)setSerialPort:(id)sender;
- (void)driveNotification:(NSNotification *)note;
- (IBAction)goCoCo:(id)sender;

@end
