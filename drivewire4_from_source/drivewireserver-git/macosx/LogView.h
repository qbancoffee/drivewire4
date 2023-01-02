/* LogView */

#import <Cocoa/Cocoa.h>

@interface LogView : NSView
{
    IBOutlet NSTextView *logTextView;
}

- (IBAction)clearLog:(id)sender;
- (IBAction)copyLog:(id)sender;

- (void)update:(NSDictionary *)info;

@end
