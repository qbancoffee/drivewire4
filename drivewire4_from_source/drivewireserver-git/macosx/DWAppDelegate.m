#import "DWAppDelegate.h"

@implementation DWAppDelegate

- (IBAction)createBlankImageAction:(id)sender
{
	NSSavePanel *panel = [NSSavePanel savePanel];

	[panel setNameFieldLabel:@"Name:"];
	[panel setTitle:[sender title]];

	if ([panel runModal] == NSFileHandlingPanelOKButton)
	{
		// We just use fopen/fclose to create the file
		NSString *filename = [[panel URL] relativePath];
		NSString *extension = [filename pathExtension];
		
		if ([extension isEqualToString:@"os9"] == NO &&
			[extension isEqualToString:@"dsk"] == NO)
		{
			filename = [filename stringByAppendingPathExtension:@"dsk"];
		}

		FILE *fp = fopen([filename cStringUsingEncoding:NSUTF8StringEncoding], "w+");

		if (fp != NULL) fclose(fp);
	}
}

@end
