LabProUSB_WinSDK from Vernier Software & Technology

The LabProUSB Windows software development kit(SDK) is intended to enable application developers to communicate with the LabPro via USB. The key technology provided in the SDK is in LabProUSB.dll, a standard Windows dynamic link library to which developers can link their applications.

The SDK includes an installation program, 'LabQuestLabProJungoDeviceDriver921.exe', which installs the Jungo driver windrvr6.sys. LabProUSB.dll uses  windrvr6.sys to communicate with the USB hardware.

The API to LabProUSB.dll is documented in \API\LabProUSB_interface.h. LabProUSB_interface.h is a C language style header that is compatible with Microsoft Visual C. The source to LabProUSB.dll is not available to third party developers.

A sample application, LabProUSB_console.exe, is provided with source. This application illustrates how to use the LabProUSB.dll API. LabProUSB_console was written in C++ using the Microsoft Foundation Classes.

The full details of how to communicate with the LabPro are well beyond the scope of the SDK. Check out http://www2.vernier.com/labpro/labpro_tech_manual.pdf. 

==============================================================

LabProUSB.dll version 4.00 is a 32 bit dll that can be used to successfully access the LabPro on both 32 bit and 64 bit Windows systems. Earlier versions of LabProUSB.dll only worked on 32 bit Windows systems. Because LabProUSB.dll version 4.00 is only a 32 bit binary, it cannot be used by 64 bit programs.

LabProUSB.dll version 4.00 is dependent on wdapi921.dll.

Wdapi921.dll comes in two flavors, a version that runs on Windows 32 bit systems called wdapi921_for_x32.dll, and a version that runs on Windows 64 bit systems called wdapi921_for_x64.dll. When you install LabProUSB.dll on your system, you must copy the correct version of wdapi921_for_xNN.dll into the same folder where LabProUSB.dll resides, and rename wdapi921_for_xNN.dll to wdapi921.dll.

So on Windows 7, Vista, and XP 32 bit systems, rename wdapi921_for_x32.dll to wdapi921.dll.

On Windows 7, Vista, and XP 64 bit systems, rename wdapi921_for_x64.dll to wdapi921.dll.

You are free to redistribute LabProUSB.dll and wdapi921.dll with your application.

==============================================================

The SDK file list:
readme.txt
LabQuestLabProJungoDeviceDriver921.exe - installs the Jungo device driver(windrvr6.sys) that LabProUSB.dll uses to communicate with the USB hardware.
\API\LabProUSB.lib - import library for LabProUSB.dll; use this to implicitly link to the dll from your app
\API\LabProUSB_interface.h - documents the function API to LabProUSB.dll
\LabPro_console_exe\LabPro_console.exe - sample application; This logs input/output data traffic to the LabPro.
\LabPro_console_exe\LabProUSB.dll - Use functions in this dll to communicate to the LabPro via USB.
\LabPro_console_exe\wdapi921_for_x32.dll - Version of wdapi921.dll intended for 32 bit operating systems.
\LabPro_console_exe\wdapi921_for_x64.dll - Version of wdapi921.dll intended for 64 bit operating systems.
\LabPro_console_src - Folder containing source code to LabPro_console.exe. You need Microsoft Visual C++ version 6 or later to build this.
redist\LabProUSB.dll - Another copy of the dll. Use functions in this dll to communicate to the LabPro via USB.
redist\wdapi921_for_x32.dll - Another copy of wdapi921_for_x32.dll. Version of wdapi921.dll intended for 32 bit operating systems.
redist\wdapi921_for_x64.dll - Another copy of wdapi921_for_x64.dll. Version of wdapi921.dll intended for 64 bit operating systems.

Release Notes:
Version 4.00
LabProUSB.dll works with 32 bit applications running in both 32 and 64 bit Windows operating systems.

version 3.02
Support new Jungo device driver for 32 bit XP and Vista. 64 bit operating systems are not supported yet.
