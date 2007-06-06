%module LabProUSB

// The code in this block is included verbatum into the swig generated code.
// it is not included into swig.
%{
#include "windows.h"
#include "LabProUSB_interface.h"
%}

// Customize how the arrays are handled by java
%include "arrays_java.i"
// Apply the "signed char []" typemap to the LPVOID lpBuffer arguments
// the signed char [] typemap is deinfed in arrays_java.i  this allows 
// byte arrays to be passed to the java wrapper around FTClassicPort
// I don't know if I actually have to setup this type map it might happen
// automatically
%apply signed char [] {signed char *buffer};

// We'll try to use static method on the module class.  The library doesn't
// have a handle so it seems to be essentially be static/global anyhow
%inline %{

short int open()
{
   return LabProUSB_Open();
}

short int close()
{
	return LabProUSB_Close();
}

short int isOpen()
{
	return LabProUSB_IsOpen();
}

long  int getAvailableBytes()
{
	return LabProUSB_GetAvailableBytes();
}

// returns the number of bytes read or -1 if there was an error
long int readBytes( long numBytesRequested, signed char *buffer )
{
    short ret = 0;
	long N = numBytesRequested;
	
    // long *N on input is the number of bytes requested
	// long *N on output is the number of bytes recieved.
	ret =  LabProUSB_ReadBytes( &N, buffer );
	if(ret >= 0) {
	  return N;
	}

	// ret should be negative one in this case	
	return ret;
}

// returns the number of bytes written or -1 if there was an error
short int writeBytes( short numBytesRequested, signed char *buffer )
{

    short ret = 0;
	short N = numBytesRequested;

    // long *N - Input: number of bytes requested.  Output: number actually written
	ret = LabProUSB_WriteBytes( &N, buffer );
	if(ret >= 0) {
	  return N;
	}

	// ret should be negative one in this case	
	return ret;	
}

short int clearInputs( short int nothing )
{
	return LabProUSB_ClearInputs( nothing );
}

short int setNumChannelsAndModes(int nNumChannels, short bBinaryMode, short bRealTime)
{
	return LabProUSB_SetNumChannelsAndModes(nNumChannels, bBinaryMode, bRealTime);
}


%}
