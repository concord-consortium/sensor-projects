LabProUSB Software Development Kit

The LabProUSB SDK is intended to provide software developers with the ability to access Vernier Software & Technology LabPro devices from their own applications.

The heart of the SDK is the LabProUSB library.
The API to this library is documented in \LabProUSB_SDK\redist\include\LabProUSB_interface.h. The sequence of function calls into the library is best illustrated by the LabPro_console sample code. The API for LabProUSB is very 'thin'. The main documentation for the LabPro protocol is in the labpro_tech_manual.pdf, which is provided in this SDK.

The Windows version of the library is LabProUSB.dll. Since LabProUSB.dll is a standard Microsoft Windows DLL, you can access the library from a variety of languages including C, C++, LabView, and Java. Both 32 bit and 64 bit versions of LabProUSB.dll are provided.

The Mac OS versions of the library are packaged as LabProUSB.dylib and LabProUSB.framework. They both support 32 bit ppc, 32 and 64 bit Intel CPU modes, and are targeted for Mac OS 10.5 and higher.

Run VernierInterfaceDrivers_100.exe to install the Windows USB device drivers for the LabPro, LabQuest, LabQuest2, and the LabQuest Mini.

====================================================================================================================

The LabProUSB SDK is currently distributed with a very permissive BSD style license. See the license.txt file located in the same folder as this readme.txt file.

Basically we encourage everyone to use the SDK, and to freely redistribute the LabProUSB library binaries. If the restrictions set out in the license.txt file discourage you from using the SDK, please contact VST at http://www.vernier.com/tech/supportform.html .

VST does not guarantee that the code is bug free, but we try to make it so. If you find any bugs, please report them to http://www.vernier.com/tech/supportform.html .

====================================================================================================================

LabProUSB.dll is dependent on wdapi921.dll. This is only used on Windows systems.

Wdapi921.dll comes in 3 flavors:
wdapi921_WIN32forOS32.dll - used by 32 bit apps when running in a 32 bit operating system.
wdapi921_WIN32forOS64.dll - used by 32 bit apps when running in a 64 bit operating system.
wdapi921_WIN64forOS64.dll - used by 64 bit apps when running in a 64 bit operating system.

When you install LabProUSB.dll on your system, you must copy the correct version of wdapi921_WINmmforOSnn.dll into the same folder where NGIO_lib.dll resides, and rename wdapi921_WINmmforOSnn.dll to wdapi921.dll.

To simplify installation of 32 bit applications, we have placed a very basic installation program in the redist folders called copy_win32_wdapi_dll.exe. This program assumes that wdapi921_WIN32forOS32.dll and wdapi921_WIN32forOS64.dll are located in the same folder as copy_win32_wdapi_dll.exe. When copy_win32_wdapi_dll.exe runs, it checks to see if it is running in a 32 bit operating system, or a 64 bit operating system. If the current OS is 32 bit, then the program copies wdapi921_WIN32forOS32.dll into wdapi921.dll in the local folder. If the current OS is 64 bit, then the program copies wdapi921_WIN32forOS64.dll into wdapi921.dll in the local folder.

====================================================================================================================

Release notes:
Version 4.12

Merge Windows and Mac versions of LabProUSB SDK into a single package.