// LabProUSB_interface.h: Defines the exported methods for the LabProUSB.dll.
//

#ifdef _LABVIEW_FOR_OSX

// don't need this on the Mac
#define LABPROUSB_INTERFACE_DECL 

#else

#ifdef _LABPROUSB_SRC
#define LABPROUSB_INTERFACE_DECL _declspec(dllexport)
#else
#define LABPROUSB_INTERFACE_DECL _declspec(dllimport)
#endif

#ifdef __cplusplus
extern "C" {
#endif

#endif


/***************************************************************************************************************************
	Function Name: LabProUSB_Open()
	
	Purpose:	LabProUSB_Open() opens up communication with the LabPro via USB. It assumes that the Jungo USB device driver
				for the LabPro has been previously installed. Note that only one LabPro may be connected to your PC at a time.

				If you call LabProUSB_Open() when the LabPro is already open, then this function will close the LabPro device
				and then reopen it.

				Normally, you should call this function before any of the other LabProUSB functions. However, if you do call one
				of the other functions first, such as GetAvailableBytes(), then this function will be called IMPLICITLY during
				the other function call. If the called function succeeds, then the LabPro device will remain open, and will
				eventually require a call to LabProUSB_Close().
				
				This Dll keeps track of whether or not the LabPro USB device is open, but does not maintain a count of how many 
				LabProUSB_Open() calls succeed. If this routine has successfully executed 1 or more times, then your application
				must make a single call to LabProUSB_Close() to close the device. Failing to call LabProUSB_Close() before your
				application shuts down can have unpredictable effects on the USB device driver.

	Arguments:	none

	Return:		0 if successful, -1 if unsuccessful.

****************************************************************************************************************************/
LABPROUSB_INTERFACE_DECL short int LabProUSB_Open(void);

/***************************************************************************************************************************
	Function Name: LabProUSB_Close()
	
	Purpose:	LabProUSB_Cloae() closes down communication with the LabPro via USB.
	
				If you call LabProUSB_Close() when the LabPro is already closed, then this function just does nothing.
				
				Normally, you should call LabProUSB_Open() before any of the other LabProUSB functions. However, if you do call 
				one of the other functions first, such as GetAvailableBytes(), then LabProUSB_Open() will be called IMPLICITLY 
				during the other function call. If the called function succeeds, then the LabPro device will remain open, and 
				will eventually require a call to LabProUSB_Close().

				This Dll keeps track of whether or not the LabPro USB device is open, but does not maintain a count of how many 
				LabProUSB_Open() calls succeed. If LabProUSB_Open() has successfully executed 1 or more times, then your 
				application must make a single call to LabProUSB_Close() to close the device. Failing to call LabProUSB_Close() 
				before your application shuts down can have unpredictable effects on the USB device driver.

	Arguments:	none

	Return:		1 (always succeeds)

****************************************************************************************************************************/
LABPROUSB_INTERFACE_DECL short int LabProUSB_Close(void);

/***************************************************************************************************************************
	Function Name: LabProUSB_IsOpen()
	
	Purpose:	Determine if the LabPro device is currently open. 

				Normally, you should call LabProUSB_Open() before any of the other LabProUSB functions. However, if you do call 
				one of the other functions first, such as GetAvailableBytes(), then LabProUSB_Open() will be called IMPLICITLY 
				during the other function call. If the called function succeeds, then the LabPro device will remain open, and 
				will eventually require a call to LabProUSB_Close().

				This Dll keeps track of whether or not the LabPro USB device is open, but does not maintain a count of how many 
				LabProUSB_Open() calls succeed. If LabProUSB_Open() has successfully executed 1 or more times, then your 
				application must make a single call to LabProUSB_Close() to close the device. Failing to call LabProUSB_Close() 
				before your application shuts down can have unpredictable effects on the USB device driver.

	Arguments:	none

	Return:		1 if the LabPro device is open, 0 if the LabPro device is closed.

****************************************************************************************************************************/
LABPROUSB_INTERFACE_DECL short int LabProUSB_IsOpen(void);

/***************************************************************************************************************************
	Function Name: LabProUSB_GetAvailableBytes()
	
	Purpose:	LabProUSB_GetAvailableBytes() informs the caller of how many bytes have been sent from the LabPro and stored 
				in the LabProUSB input buffer. You may read these bytes into your application with LabProUSB_ReadBytes().

				If you have not previously called LabProUSB_Open(), then LabProUSB_GetAvailableBytes() will IMPLICITLY 
				call LabProUSB_Open()! The LabPro device will remain open, and will eventually require a call to 
				LabProUSB_Close().
				
	Arguments:	none

	Return:		-1 if unsuccessful, otherwise # of bytes available.

****************************************************************************************************************************/
LABPROUSB_INTERFACE_DECL long  int LabProUSB_GetAvailableBytes(void);

/***************************************************************************************************************************
	Function Name: LabProUSB_ReadBytes()
	
	Purpose:	LabProUSB_ReadBytes() reads a specified # of bytes from the LabProUSB input buffer. 
	
				If you have not previously called LabProUSB_Open(), then LabProUSB_ReadBytes() will IMPLICITLY 
				call LabProUSB_Open()! The LabPro device will remain open, and will eventually require a call to 
				LabProUSB_Close().
				
	Arguments:	long *N - Input: number of bytes requested.  Output: number actually read
				char *buffer - ptr to buffer to copy bytes to

	Return:		0 if successful, -1 if unsuccessful.

****************************************************************************************************************************/
LABPROUSB_INTERFACE_DECL short int LabProUSB_ReadBytes( long *N, char *buffer );

/***************************************************************************************************************************
	Function Name: LabProUSB_WriteBytes()
	
	Purpose:	LabProUSB_WriteBytes() writes a specified # of bytes to the LabPro via USB. This routine will timeout and
				return an error if the write takes more than 2 seconds to finish.
	
				If you have not previously called LabProUSB_Open(), then LabProUSB_WriteBytes() will IMPLICITLY 
				call LabProUSB_Open()! The LabPro device will remain open, and will eventually require a call to 
				LabProUSB_Close().
				
	Arguments:	long *N - Input: number of bytes requested.  Output: number actually written
				char *buffer - ptr to buffer to copy bytes from

	Return:		0 if successful, -1 if unsuccessful.

****************************************************************************************************************************/
LABPROUSB_INTERFACE_DECL short int LabProUSB_WriteBytes( short *N, char *buffer );

/***************************************************************************************************************************
	Function Name: LabProUSB_ClearInputs()
	
	Purpose:	LabProUSB_ClearInputs() clears the input buffer that holds data read back from the LabPro.
	
				If you have not previously called LabProUSB_Open(), then LabProUSB_WriteBytes() will IMPLICITLY 
				call LabProUSB_Open()! The LabPro device will remain open, and will eventually require a call to 
				LabProUSB_Close().
				
	Arguments:	int nothing - just a place holder for now.

	Return:		0 if successful, -1 if unsuccessful.

****************************************************************************************************************************/
LABPROUSB_INTERFACE_DECL short int LabProUSB_ClearInputs( short int nothing );

/***************************************************************************************************************************
	Function Name: LabProUSB_SetNumChannelsAndModes()
	
	Purpose:	LabProUSB_SetNumChannelsAndModes() configures LabProUSB.dll so that it can successfully parse the data 
				packets coming back from the LabPro via USB.

				Note that LabProUSB_SetNumChannelsAndModes() does not actually send any data to the LabPro. This function
				merely configures LabProUSB.dll's input parser so that it correctly extracts data out of the packets coming
				back from the LabPro via USB. Some LabPro USB packets are padded with additional meaningless data in order 
				to reach a desired size(eg. 64 bytes). The format of the padding data varies according to how the LabPro is 
				configured. For example, the padding scheme for text data is different than the padding scheme for binary data.

				POSSIBLY USEFUL FACT: If you call LabProUSB_SetNumChannelsAndModes(chans, bBinaryMode, bRealTime) with 
				bBinaryMode = 1 and bRealTime = 0 then the data returned by LabProUSB_ReadBytes() will just be the raw data
				sent across the USB wire. The input parser does not strip out any of the padding bytes in this case.

				If the LabProUSB_SetNumChannelsAndModes parameters are set correctly, then the data returned by 
				LabProUSB_ReadBytes() should be identical to the data read back using a serial cable(not USB).

				If you send a command to the LabPro that changes the binary/text mode, the real time/non-real time mode, or 
				the number of channels that are collecting measurements from probes, then you should first call 
				LabProUSB_SetNumChannelsAndModes() with the appropriate parameters. More details on configuring the LabPro 
				may be found at http://www2.vernier.com/labpro/labpro_tech_manual.pdf .

				Example 1:
				LabProUSB_SetNumChannelsAndModes(1, 1, 1);//Configure LabProUSB.dll for 1 probe, binary data, real time.
				Send s<CR> to the LabPro. This wakes up the LabPro.
				Send s{0}<CR> to the LabPro. This resets the LabPro. Data transfer mode is text, no channels are configured.
				Send s{1,1,11} to the LabPro. This sets up channel 1 to return Fahrenheit temperature.
				Send s{4, 0, -1}<CR> to the LabPro. Sets up binary transfer mode.
				Send s{3, 1.0, -1}<CR> to the LabPro. Sets up 1 measurement per second, real time collection.
				
				Example 2:
				LabProUSB_SetNumChannelsAndModes(1, 0, 0);//Configure LabProUSB.dll for 1 probe, text data, non-real time 
					collection.
				Send s<CR> to the LabPro. This wakes up the LabPro.
				Send s{0}<CR> to the LabPro. This resets the LabPro. Data transfer mode is text, no channels are configured.
				Send s{1,1,11} to the LabPro. This sets up channel 1 to return Fahrenheit temperature.
				Send s{3, 1.0, 5}<CR> to the LabPro. Sets up 1 measurement per second, collect 5 measurements using 
					non-real time data collection. Triggering of the first measurement is manual by default.
				

				If you have not previously called LabProUSB_Open(), then LabProUSB_WriteBytes() will IMPLICITLY 
				call LabProUSB_Open()! The LabPro device will remain open, and will eventually require a call to 
				LabProUSB_Close().
				
	Arguments:	int nNumChannels - Number of probes plugged into the LabPro that are collecting measurement data.
				short bBinaryMode - binary mode if 1, else text mode. Note that binary mode only applies to probe measurements. 
					Non-measurement data coming back from the LabPro such as a GetStatus(s{7}) reply always comes back as text.
				short bRealTime - real time if 1, else non-real time.

	Return:		0 if successful, -1 if unsuccessful.

****************************************************************************************************************************/
LABPROUSB_INTERFACE_DECL short int LabProUSB_SetNumChannelsAndModes(int nNumChannels, short bBinaryMode, short bRealTime);



#ifdef _LABVIEW_FOR_OSX

#else

#ifdef __cplusplus
} //extern "C"
#endif

#endif

