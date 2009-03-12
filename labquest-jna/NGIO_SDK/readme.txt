NGIO Software Development Kit

The NGIO SDK is intended to provide software developers with the ability to access Vernier Software & Technology LabQuest devices from their own applications.

The heart of the SDK is the NGIO_DLL library.
The API to this library is documented in \NGIO_SDK\redist\include\NGIO_lib_interface.h.

The Windows version of this library is NGIO_lib.dll. Since NGIO_lib.dll is a standard Microsoft Windows DLL, you can access the library from a variety of languages including C, C++, Basic, LabView, and Java.

The Apple version of the NGIO_lib library is libNGIO.dylib.
libNGIO has been ported to Linux, but it is packaged separately.

====================================================================================================================

NGIO_Measure is a sample application that is coded to the NGIO_lib API. The Windows version source code was written in Microsoft Visual C++ version 6.0. 

To run this application, you need to plug a LabQuest into a USB port, click on the Devices menu, and then click on one of the listed devices.

Install LoggerPro version 3.6 or later prior to running NGIO_Measure to guarantee that the USB device driver for the LabQuest is properly installed.
NGIO_Measure is only ported to windows at this time.

====================================================================================================================

Release notes:

Version 1.35
NGIO_lib is reasonably mature, but NGIO_SDK still needs more sample code. Only windows sample code is currently available(NGIO_Measure).
