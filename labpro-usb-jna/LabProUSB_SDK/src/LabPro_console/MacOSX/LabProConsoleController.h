//
//  LabProConsoleController.h
//  LabPro_console
//
//  Created by Steve Splonskowski on 5/22/07.
//  Copyright 2007 Vernier Software & Technology. All rights reserved.
//

#import <Cocoa/Cocoa.h>


@interface LPTransaction : NSObject {
	NSDate*	mTime;
	BOOL	mInput;	// input or output
	NSData*	mData;
}

- (id) initWithTime:(NSDate*)time input:(BOOL)input andData:(NSData*)data;

- (NSDate*) time;
- (BOOL) input;
- (NSData*) data;

@end


@interface LabProConsoleController : NSObject {
	IBOutlet id	mCmdTextField;
	IBOutlet id	mAppendCRCheckbox;
	IBOutlet id	mSendButton;
	IBOutlet id	mLogTableView;
	
	IBOutlet id	mChannelsAndModesSheet;
	IBOutlet id	mChannelsTextField;
	IBOutlet id	mBinaryModeCheckbox;
	IBOutlet id	mRealtimeModeCheckbox;
	
	NSMutableArray*	mTransactions;
	
	NSMutableArray* mCommandHistory;
	int mHistoryBrosweIndex;
	
	NSTimer* mReadTimer;
}

- (IBAction) connectToLabPro:(id)sender;
- (IBAction) reconnectToLabPro:(id)sender;
- (IBAction) disconnectFromLabPro:(id)sender;
- (IBAction) getStatusFromLabPro:(id)sender;

- (IBAction) setNumChannelsAndModes:(id)sender;
- (IBAction) okChannelsAndModes:(id)sender;
- (IBAction) cancelChannelsAndModes:(id)sender;

- (IBAction) sendCommand:(id)sender;
- (IBAction) saveTransacations:(id)sender;
- (IBAction) clearTransactions:(id)sender;

@end
