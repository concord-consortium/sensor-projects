//
//  LabProConsoleController.m
//  LabPro_console
//
//  Created by Steve Splonskowski on 5/22/07.
//  Copyright 2007 Vernier Software & Technology. All rights reserved.
//

#import "LabProConsoleController.h"
#import "LabProUSB_interface.h"


@implementation LPTransaction

- (id) initWithTime:(NSDate*)time input:(BOOL)input andData:(NSData*)data
{
	if (self = [super init]) {
		mTime = [time retain];
		mInput = input;
		mData = [data retain];
	}
	
	return self;
}

- (void) dealloc
{
	[mTime release];
	[mData release];
	[super dealloc];
}

- (NSDate*) time
{
	return mTime;
}

- (BOOL) input
{
	return mInput;
}

- (NSData*) data
{
	return mData;
}

@end


@implementation LabProConsoleController

- (void) startReadTimer
{
	if (mReadTimer != nil) 
		return;
	
	mReadTimer = [[NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(readResultsFromLabPro:) userInfo:nil repeats:YES] retain];
}

- (void) stopReadTimer
{
	[mReadTimer invalidate];
	[mReadTimer release];
	mReadTimer = nil;
}

- (void) readResultsFromLabPro:(NSTimer*)timer
{
	do {
		gtype_int32 avail = LabProUSB_GetAvailableBytes();
		if (avail == 0) 
			return;
		
		if (avail > 32) 
			avail = 32;
		
		char buf[32];
		LabProUSB_ReadBytes(&avail, buf);
		
		// add the input to the transaction log
		LPTransaction* ta = [[LPTransaction alloc] initWithTime:[NSDate date] input:TRUE andData:[NSData dataWithBytes:buf length:avail]];
		[mTransactions addObject:ta];
		[mLogTableView reloadData];
		[mLogTableView scrollRowToVisible:[mLogTableView numberOfRows]-1];
	} while (TRUE);
}

- (void) sendCommandToLabPro:(NSString*)cmd
{
	if ([cmd length] == 0) 
		return;
	
	// save away the command for type completion (strip off the CR)
	NSString* saveCmd = [NSString stringWithString:cmd];
	if ([saveCmd characterAtIndex:[saveCmd length]-1] == '\r') 
		saveCmd = [saveCmd substringToIndex:[saveCmd length]-1];
	[mCommandHistory removeObject:saveCmd];
	[mCommandHistory insertObject:saveCmd atIndex:0];
	mHistoryBrosweIndex = -1;
	
	// send the command to the LabPro
	short len = [cmd length];
	const char* cs = [cmd cStringUsingEncoding:NSASCIIStringEncoding];
	LabProUSB_WriteBytes(&len, (char*)cs);
	
	// add the command to the transaction log
	LPTransaction* ta = [[LPTransaction alloc] initWithTime:[NSDate date] input:FALSE andData:[cmd dataUsingEncoding:NSUTF8StringEncoding]];
	[mTransactions addObject:ta];
	[mLogTableView reloadData];
	[mLogTableView scrollRowToVisible:[mLogTableView numberOfRows]-1];
}

- (void) awakeFromNib
{
	[NSApp setDelegate:self];
	mTransactions = [[NSMutableArray alloc] init];
	mCommandHistory = [[NSMutableArray alloc] init];
	mHistoryBrosweIndex = -1;
	
	if (LabProUSB_Open() == noErr) {
		[self sendCommandToLabPro:@"s\r"];		// send reset and status commands
		[self getStatusFromLabPro:self];
		[self startReadTimer];
	}
}

- (void) dealloc
{
	[mTransactions release];
	[mCommandHistory release];
	[super dealloc];
}

- (void) applicationWillTerminate:(NSNotification*)notification
{
	[self disconnectFromLabPro:self];
}

- (BOOL) applicationShouldTerminateAfterLastWindowClosed:(NSApplication*)app
{
	return YES;
}

- (BOOL) validateMenuItem:(id <NSMenuItem>)menuItem
{
	BOOL labProOpen = (LabProUSB_IsOpen() != 0);
	if ([menuItem action] == @selector(connectToLabPro:)) 
		return !labProOpen;
	return labProOpen;
}

- (void) setupSendButton
{
	// enable the Send button based on having command text
	[mSendButton setEnabled:[[mCmdTextField stringValue] length] > 0 && LabProUSB_IsOpen()];
}

- (void) controlTextDidChange:(NSNotification*)notification
{
	[self setupSendButton];
}

- (NSArray*) control:(NSControl*)control textView:(NSTextView*)textView completions:(NSArray*)words forPartialWordRange:(NSRange)charRange indexOfSelectedItem:(int*)index
{
	*index = 0;
	NSMutableArray* cmds = [NSMutableArray arrayWithCapacity:3];
	NSString* partial = [[[textView textStorage] string] substringWithRange:charRange];
	
	// iterate command sent array looking for matches
	NSString* cmd = nil;
	NSEnumerator* enmr = [mCommandHistory objectEnumerator];
	while (cmd = [enmr nextObject]) {
		if ([cmd hasPrefix:partial]) 
			[cmds addObject:cmd];
	}
	
	return cmds;
}

- (BOOL) control:(NSControl*)control textView:(NSTextView*)textView doCommandBySelector:(SEL)command
{
	if (command != @selector(moveUp:) && command != @selector(moveDown:))
		return FALSE;
	
	int newIndex = mHistoryBrosweIndex;
	if (command == @selector(moveUp:) && newIndex < (int)[mCommandHistory count]-1) 
		++newIndex;
	else if (command == @selector(moveDown:) && newIndex >= 0)
		--newIndex;
	
	if (newIndex != mHistoryBrosweIndex) {
		NSString* cmd = (newIndex >= 0 ? [mCommandHistory objectAtIndex:newIndex] : @"");
		[mCmdTextField setStringValue:cmd];
		[mCmdTextField selectText:nil];
		mHistoryBrosweIndex = newIndex;
		[self setupSendButton];
	}
	
	return TRUE;
}

- (IBAction) connectToLabPro:(id)sender;
{
	if (LabProUSB_Open() != 0) 
		NSBeep();
	
	if (LabProUSB_IsOpen())
		[self startReadTimer];
	
	[self setupSendButton];
}

- (IBAction) reconnectToLabPro:(id)sender;
{
	LabProUSB_Close();
	[self connectToLabPro:sender];
}

- (IBAction) disconnectFromLabPro:(id)sender;
{
	LabProUSB_Close();
	[self setupSendButton];
	[self stopReadTimer];
}

- (IBAction) getStatusFromLabPro:(id)sender;
{
	[self sendCommandToLabPro:@"s{7}\r"];		// send status command
}

- (IBAction) setNumChannelsAndModes:(id)sender
{
	[NSApp beginSheet:mChannelsAndModesSheet modalForWindow:[mSendButton window] modalDelegate:nil didEndSelector:nil contextInfo:nil];
}

- (IBAction) okChannelsAndModes:(id)sender
{
	int numChannels = [mChannelsTextField intValue];
	short binaryMode = ([mBinaryModeCheckbox intValue] != 0);
	short realtimeMode = ([mRealtimeModeCheckbox intValue] != 0);
	LabProUSB_SetNumChannelsAndModes(numChannels, binaryMode, realtimeMode);
	
	[mChannelsAndModesSheet orderOut:sender];
	[NSApp endSheet:mChannelsAndModesSheet];
}

- (IBAction) cancelChannelsAndModes:(id)sender
{
	[mChannelsAndModesSheet orderOut:sender];
	[NSApp endSheet:mChannelsAndModesSheet];
}

- (IBAction) sendCommand:(id)sender
{
	NSString* cmd = [mCmdTextField stringValue];
	if ([mAppendCRCheckbox intValue]) 
		cmd = [cmd stringByAppendingString:@"\015"];
	[self sendCommandToLabPro:cmd];
	[mCmdTextField selectText:self];
}

// transaction table data source
- (int) numberOfRowsInTableView:(NSTableView*)tableView
{
	return [mTransactions count];
}

static NSString* DataToHexString(NSData* data)
{
	NSMutableString* s = [[data description] mutableCopy];
	
	//	start with:		<01020304 05060708 09>
	//	convert to:		01 02 03 04  05 06 07 08  09
	
	// remove the angle brackets
	[s deleteCharactersInRange:NSMakeRange(0,1)];
	[s deleteCharactersInRange:NSMakeRange([s length]-1,1)];
	
	// insert spaces between bytes - they are grouped into runs of 4 bytes
	unsigned loc = 2;
	unsigned cnt = 0;
	while (loc < [s length]) {
		[s insertString:@" " atIndex:loc];
		loc += 3;
		if (++cnt % 4 == 0) 
			++loc;
	}
	
	return s;
}

static NSString* MakeStringCRVisible(NSString* s)
{
	NSMutableString* ms = [s mutableCopy];
	[ms replaceOccurrencesOfString:@"\r" withString:@"\\n" options:0 range:NSMakeRange(0, [ms length])];
	[ms replaceOccurrencesOfString:@"\n" withString:@"" options:0 range:NSMakeRange(0, [ms length])];
	return ms;
}

- (id) tableView:(NSTableView*)tableView objectValueForTableColumn:(NSTableColumn*)column row:(int)row
{
	LPTransaction* ta = [mTransactions objectAtIndex:row];
	
	if ([[column identifier] isEqualToString:@"time"]) {
		return [ta time];
	} else if ([[column identifier] isEqualToString:@"io"]) {
		return ([ta input] ? @"IN" : @"OUT");
	} else if ([[column identifier] isEqualToString:@"bytes"]) {
		return [NSNumber numberWithInt:[[ta data] length]];
	} else if ([[column identifier] isEqualToString:@"ascii"]) {
		NSString* s = [[[NSString alloc] initWithData:[ta data] encoding:NSUTF8StringEncoding] autorelease];
		return MakeStringCRVisible(s);
	} else if ([[column identifier] isEqualToString:@"hex"]) {
		return DataToHexString([ta data]);
	} else 
		return nil;
}

- (NSString*) textForTransactionsInSet:(NSIndexSet*)set
{
	NSMutableString* text = [NSMutableString string];
	
	// iterate selected rows
	unsigned row = [set firstIndex];
	while (row != NSNotFound) {
		
		// start new line for each row
		if ([text length] > 0)
			[text appendString:@"\n"];
		
		// iterate colums (in their current order)
		NSArray* cols = [mLogTableView tableColumns];
		NSEnumerator* enmr = [cols objectEnumerator];
		NSTableColumn* col = nil;
		while (col = [enmr nextObject]) {
			NSString* cellStr = [[self tableView:mLogTableView objectValueForTableColumn:col row:row] description];
			cellStr = MakeStringCRVisible(cellStr);
			[text appendString:cellStr];
			[text appendString:@"\t"];		// tab-deliminated values
		}
		
		// get next row index
		row = [set indexGreaterThanIndex:row];
	}
	
	return text;
}

// copy text of selected transactions to the clipboard
- (void) copy:(id)sender
{
	NSIndexSet* selSet = [mLogTableView selectedRowIndexes];
	if ([selSet count] < 1) 
		return;
	
	NSString* text = [self textForTransactionsInSet:selSet];
	[[NSPasteboard generalPasteboard] declareTypes:[NSArray arrayWithObject:NSStringPboardType] owner:nil];
	[[NSPasteboard generalPasteboard] setString:text forType:NSStringPboardType];
}

// save transaction log text to file
- (IBAction) saveTransacations:(id)sender
{
	NSIndexSet* fullSet = [NSIndexSet indexSetWithIndexesInRange:NSMakeRange(0, [mLogTableView numberOfRows]-1)];
	NSString* text = [[self textForTransactionsInSet:fullSet] retain];
	
	[[NSSavePanel savePanel] beginSheetForDirectory:nil file:@"log.txt" modalForWindow:[mSendButton window] modalDelegate:self didEndSelector:@selector(savePanelDidEnd:returnCode:contextInfo:) contextInfo:text];
}

- (void) savePanelDidEnd:(NSSavePanel*)sheet returnCode:(int)returnCode  contextInfo:(void*)contextInfo
{
	if (returnCode != NSOKButton) 
		return;
	
	NSData* textData = [(NSString*)contextInfo dataUsingEncoding:NSUTF8StringEncoding];
	[textData writeToFile:[sheet filename] atomically:NO];
	[(NSString*)contextInfo release];
}

- (IBAction) clearTransactions:(id)sender
{
	[mTransactions removeAllObjects];
	[mLogTableView reloadData];
}

@end
