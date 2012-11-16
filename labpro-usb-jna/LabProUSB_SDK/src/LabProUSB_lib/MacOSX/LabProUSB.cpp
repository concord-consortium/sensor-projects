/*
 *  LabProUSB_interface.cpp
 *  Mac OS X framework for access to the LabPro.
 *
 *  Created by Steve Splonskowski on Thursday May 10 2007.
 *  Copyright (c) 2007 Vernier Software & Technology. All rights reserved.
 *
 */

#include "LabProUSB_interface.h"
#include "VST_USB.h"


const unsigned int kLabProUSBVendorID  = 0x08F7;
const unsigned int kLabProUSBProductID = 0x0001;
//const unsigned int kDefaultLabProIndex = 0;

static	unsigned char	bLabProOpen = false;		// true while the current device is open
//static MUSBXPort* pUSBPort;
static VST_USBBulkDevice* pLabPro = NULL;
static VST_USBSpecArrayRef pSpecArray = NULL;		// TODO: this does not need to be global?


static OSStatus SetupPort()
{
//	if (!MUSBX::IsInitialized())
//	{	
//		CFURLRef vst_usb_bundleURL = 0;
//		
//		CFBundleRef appBundle = CFBundleGetMainBundle();
//		if  (appBundle == NULL)
//			return -9812;
//		else
//		{
//			OSErr err = -9123; // fnfErr;
//			CFURLRef myAppFolderURL = CFBundleCopyBundleURL(appBundle);
//			if (myAppFolderURL == NULL) 
//				return err; 
//			
//			//vst_usb_bundleURL = ::CFBundleCopyResourceURL( 
//			//						myAppFolderURL, 
//			//						CFSTR("VST_USB.bundle"),
//			//						NULL,
//			//						NULL);
//									
//			vst_usb_bundleURL = ::CFURLCreateCopyAppendingPathComponent ( kCFAllocatorDefault , myAppFolderURL, CFSTR("VST_USB.bundle"), false ); 
//
//			if (vst_usb_bundleURL == NULL)
//				return -9855;
//			
//			err = MUSBX::InitMUSBX(vst_usb_bundleURL);
//			
//			::CFRelease(vst_usb_bundleURL);
//	//		::CFRelease(vst_usb_bundleURL);
//		}
//		
//	}
//		
//	if (!MUSBX::IsInitialized())
//		return -111;
		
    if (bLabProOpen)
        LabProUSB_Close();
    
	//pSpecArray = MUSBX::GetUSBSpecsForDevice(kLabProUSBVendorID,  kLabProUSBProductID );
	VST_GetUSBSpecsForDevice(kLabProUSBVendorID, kLabProUSBProductID, false, &pSpecArray);
	
	OSErr nErr = noErr;
	if (pSpecArray == NULL || CFArrayGetCount(pSpecArray) < 1)
		nErr = -1;
	else
	{
        VST_USBSpec* pSpec = NULL;
		
		nErr = -1;			// was -201
		
		// find the first available LabPro
		int nIndex;
		int bDone = 0;
		for (nIndex=0; !bDone && nIndex < CFArrayGetCount(pSpecArray); ++nIndex)
		{
			pSpec = (VST_USBSpec*) CFArrayGetValueAtIndex(pSpecArray, nIndex);
			if (pSpec == NULL) 
				break;
			
			//pLabPro = MUSBX::MakeUSBBulkDeviceFromSpec(pSpec);
			pLabPro = VST_MakeUSBBulkDeviceFromSpec(pSpec);
			
			if (pLabPro != NULL)
			{
				//if (!MUSBX::PortIsAvailable(pLabPro))
				if (VST_IsDeviceInUse(pLabPro))
					nErr = -1;
				else
				{
					//pUSBPort = new MUSBXPort( pLabPro, "LabPro-USB" );
					
//					if (pUSBPort == NULL)
//					{
//						LabProUSB_Close();
//						nErr = -555;
//					}
//					else
//					{
//						nErr = pUSBPort->OpenForIO();
//					}
					
//					nErr = VST_ClearInput(pLabPro, 0);			NOT NEEDED - returns device not ready everytime
					nErr = noErr;
					if (nErr == noErr) 
						nErr = VST_OpenUSBPortForIO(pLabPro);
					
					if (nErr == noErr)
					{
						bLabProOpen = true;
						//nErr = pUSBPort->SetTextMode( true );
						nErr = VST_SetTextMode(pLabPro, true);
						bDone = 1;
						return nErr;		// was 1717
					}
				}
			}
		}
		
		//MUSBX::ReleaseUSBSpecs( pSpecArray );
		VST_ReleaseUSBSpecArray(pSpecArray);
		pSpecArray = NULL;
	}
	
	return nErr;
}

static OSStatus ForceLabProOpen(void)
{
	if (LabProUSB_IsOpen() /*&& (pUSBPort != NULL)*/ && pLabPro != NULL) 
		return noErr;
	
	return LabProUSB_Open();  // try to open it automatically
}

LABPROUSB_INTERFACE_DECL gtype_int16 LabProUSB_Open(void)
{
	if (LabProUSB_IsOpen())
		LabProUSB_Close();
	
	// FIXME this will lose some error precision since SetupPort returns an OSStatus or int
	short int err = (short int)SetupPort();
	return err;
}

LABPROUSB_INTERFACE_DECL gtype_int16 LabProUSB_Close(void)
{
	bLabProOpen = false;
	
//	if (pUSBPort != NULL)
//	{
//		pUSBPort->Close();
//		delete pUSBPort;
//		pUSBPort = NULL;
//	}
	
	if (pLabPro != NULL) 
	{
		VST_CloseUSBPort(pLabPro);
		pLabPro = NULL;
	}	
	
	return (short int)noErr;
}

LABPROUSB_INTERFACE_DECL gtype_int16 LabProUSB_IsOpen(void)
{		
	return bLabProOpen;
}

LABPROUSB_INTERFACE_DECL gtype_int32 LabProUSB_GetAvailableBytes(void)
{
	if (ForceLabProOpen() != noErr) 
		return -1;
	
//	return pUSBPort->BytesAvailable();
	return VST_BytesAvailable(pLabPro, 0);
}

LABPROUSB_INTERFACE_DECL gtype_int16 LabProUSB_ReadBytes( gtype_int32 *ioCnt, char *buffer )
{
	if (ForceLabProOpen() != noErr) 
		return -1;
	
	UInt32 nBytesToRead = *ioCnt;
	UInt32 nTimeoutInms = 100;
//	OSErr nErr = pUSBPort->ReadBytes( buffer, &nBytesToRead, nTimeoutInms);
	OSErr nErr = VST_ReadBytes(pLabPro, buffer, &nBytesToRead, nTimeoutInms, 0);
	
	if (nErr != noErr)
	{
		nErr = -1;
		*ioCnt = 0;
	}
	else
		*ioCnt = nBytesToRead;
	
	return (short int)nErr;
}

LABPROUSB_INTERFACE_DECL gtype_int16 LabProUSB_WriteBytes( gtype_int16 *ioCnt, char *buffer )
{
	if (ForceLabProOpen() != noErr) 
		return -1;
	
//	OSErr nErr = pUSBPort->WriteBytes( (char *) buffer, *N );
	// note VST_WriteBytes doesn't actually know how many bytes were written
	// I guess the assumption is that it is all or nothing
	OSErr nErr = VST_WriteBytes(pLabPro, (void*)buffer, *ioCnt);
	
	if (nErr != noErr) {
		nErr = -1;
		*ioCnt = 0;
	}
	return (short int)nErr;
}

LABPROUSB_INTERFACE_DECL gtype_int16 LabProUSB_ClearInputs( gtype_int16 nothing )
{
	if (ForceLabProOpen() != noErr) 
		return -1;
	
//	OSErr nErr = pUSBPort->ClearInputBuffer();
	OSErr nErr = VST_ClearInput(pLabPro, 0);
	
	if (nErr != noErr)
		nErr = -1;
	
	return (short int)nErr;
}

LABPROUSB_INTERFACE_DECL gtype_int16 LabProUSB_SetNumChannelsAndModes(gtype_int32 nNumChannels, gtype_int16 bBinaryMode, gtype_int16 bRealTime)
{
	if (ForceLabProOpen() != noErr) 
		return -1;
	
//	pUSBPort->SetTextMode( !bBinaryMode );
	VST_SetTextMode(pLabPro, !bBinaryMode);
//	pUSBPort->NoSubPackets();
	VST_NoSubPackets(pLabPro);
	
	if (!bBinaryMode) 
	{
		if (bRealTime) 
		{
			int nSubPacketSizeInBytes = 4 + ( (nNumChannels-1) * 2 );	
//			pUSBPort->ExpectSubPackets( 16, nSubPacketSizeInBytes );
			VST_ExpectSubPackets(pLabPro, 16, nSubPacketSizeInBytes);
		}
	}
	
	return noErr;
}

OSStatus LabProUSB_RawMode(void)
{
	if (ForceLabProOpen() != noErr) 
		return -1;
	
	return LabProUSB_SetNumChannelsAndModes( 1, 1, 0 );
}

OSStatus LabProUSB_TextMode(void)
{
	if (ForceLabProOpen() != noErr) 
		return -1;
	
	return LabProUSB_SetNumChannelsAndModes( 1, 0, 0 );
}

OSStatus LabProUSB_BinaryRealTimeMode(short nPackets, short nPacketSize)
{
	if (ForceLabProOpen() != noErr) 
		return -1;
	
	int nChannels = 1 + ((nPacketSize-4)/2);
	return LabProUSB_SetNumChannelsAndModes(nChannels, 1, 1);
}

OSStatus LabProUSB_BinaryBlockMode(short nBlockSize)
{
	if (ForceLabProOpen() != noErr) 
		return -1;
	
	return LabProUSB_SetNumChannelsAndModes(1, 1, 0);
}
