#import "LogView.h"

@implementation LogView

- (void)updateLog:(NSString *)logString;
{
    [logTextView setEditable:TRUE];
    NSRange range = {[[logTextView string] length], 0};
    [logTextView setSelectedRange:range];
    [logTextView insertText:logString];
    [logTextView setEditable:FALSE];
}

- (id)initWithFrame:(NSRect)frameRect;
{
	if ((self = [super initWithFrame:frameRect]) != nil)
	{
	}

	return self;
}

- (void)awakeFromNib;
{
   [logTextView setEditable:FALSE];
   [logTextView setContinuousSpellCheckingEnabled:FALSE];
}

- (void)dealloc
{
}

- (void)drawRect:(NSRect)rect;
{
}

- (IBAction)clearLog:(id)sender;
{
   [logTextView setEditable:TRUE];
	[logTextView selectAll:sender];
	[logTextView cut:sender];
   [logTextView setEditable:FALSE];
}

- (IBAction)copyLog:(id)sender;
{
	NSRange range = {0, 0};
   [logTextView setEditable:TRUE];
	[logTextView selectAll:sender];
	[logTextView copy:sender];
	[logTextView setSelectedRange:range];
   [logTextView setEditable:FALSE];
}

- (void)update:(NSDictionary *)info;
{
	NSString *logString = [info objectForKey:@"OpCode"];
	NSString *rightNow = [[NSDate date] descriptionWithCalendarFormat:@"%Y-%m-%d %H:%M:%S" timeZone:nil locale:nil];
	
	if ([logString isEqualToString:@"OP_GETSTAT"] == YES)
	{
		logString = [NSString stringWithFormat:@"%@ %@[%@] Code[%@]\n", rightNow, [info objectForKey:@"OpCode"], [info objectForKey:@"DriveNumber"],
			[info objectForKey:@"GetStat"]];
	}
	else if ([logString isEqualToString:@"OP_SETSTAT"] == YES)
	{
		logString = [NSString stringWithFormat:@"%@ %@[%@] Code[%@]\n", rightNow, [info objectForKey:@"OpCode"], [info objectForKey:@"DriveNumber"],
			[info objectForKey:@"SetStat"]];
	}
	else if ([logString isEqualToString:@"OP_READ"] == YES
		|| [logString isEqualToString:@"OP_READEX"] == YES
		|| [logString isEqualToString:@"OP_REREAD"] == YES
		|| [logString isEqualToString:@"OP_REREADEX"] == YES
		|| [logString isEqualToString:@"OP_WRITE"] == YES
		|| [logString isEqualToString:@"OP_REWRITE"] == YES)
	{
		logString = [NSString stringWithFormat:@"%@ %@[%@] LSN[%d] CSum[%d] Error[%d]\n", rightNow, [info objectForKey:@"OpCode"],
			[info objectForKey:@"DriveNumber"], [[info objectForKey:@"LSN"] intValue], [[info objectForKey:@"Checksum"] intValue],
			[[info objectForKey:@"Error"] intValue]];
	}
	else if ([logString isEqualToString:@"OP_PRINT"] == YES)
	{
      // Don't bother logging a PQ_ADD unless the printer has been flushed.  We don't want to flood the log window with these messages because it slows down the CoCo.
      return;
   }
	else if ([logString isEqualToString:@"OP_VPORT_READ"] == YES)
	{
		logString = [NSString stringWithFormat:@"%@ %@[%@] ReadCount[%@]\n", rightNow, [info objectForKey:@"OpCode"], [info objectForKey:@"VPort"], [info objectForKey:@"ReadCount"]];
	}
	else if ([logString isEqualToString:@"OP_VPORT_WRITE"] == YES)
	{
		logString = [NSString stringWithFormat:@"%@ %@[%@] DataByte[%@]\n", rightNow, [info objectForKey:@"OpCode"], [info objectForKey:@"VPort"], [info objectForKey:@"DataByte"]];
	}
	else
	{
		logString = [NSString stringWithFormat:@"%@ %@\n", rightNow, logString];
	}
	
    [self performSelectorOnMainThread:@selector(updateLog:) withObject:logString waitUntilDone:YES];
}

@end
