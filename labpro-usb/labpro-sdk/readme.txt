LabProUSB_WinSDK from Vernier Software & Technology

The LabProUSB Windows software development kit(SDK) is intended to enable application developers to communicate with the LabPro via USB. The key technology provided in the SDK is in LabProUSB.dll, a standard Windows dynamic link library to which developers can link their applications.

The SDK includes an installation program, 'LabPro USB device driver installer.exe', which installs the Jungo driver windrvr6.sys. LabProUSB.dll uses  windrvr6.sys to communicate with the USB hardware.

The API to LabProUSB.dll is documented in \API\LabProUSB_interface.h. LabProUSB_interface.h is a C language style header that is compatible with Microsoft Visual C. The source to LabProUSB.dll is not available to third party developers.

A sample application, LabProUSB_console.exe, is provided with source. This application illustrates how to use the LabProUSB.dll API. LabProUSB_console was written in C++ using the Microsoft Foundation Classes.

The full details of how to communicate with the LabPro are well beyond the scope of the SDK. Check out http://www2.vernier.com/labpro/labpro_tech_manual.pdf. 


The SDK file list:
readme.txt
LabPro USB device driver installer.exe - installs the Jungo device driver(windrvr6.sys) that LabProUSB.dll uses to communicate with the USB hardware.
\API\LabProUSB.lib - import library for LabProUSB.dll; use this to implicitly link to the dll from your app
\API\LabProUSB_interface.h - documents the function API to LabProUSB.dll
\LabPro_console_exe\LabPro_console.exe - sample application; This logs input/output data traffic to the LabPro.
\LabPro_console_exe\LabProUSB.dll - Use functions in this dll to communicate to the LabPro via USB.
\LabPro_console_src - Folder containing source code to LabPro_console.exe. You need Microsoft Visual C++ version 6 or later to build this.
redist\LabProUSB.dll - Another copy of the dll. Use functions in this dll to communicate to the LabPro via USB.

Release Notes:
version 3.02
Support new Jungo device driver for 32 bit XP and Vista. 64 bit operating systems are not supported yet.
