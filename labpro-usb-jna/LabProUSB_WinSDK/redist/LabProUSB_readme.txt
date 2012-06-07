LabProUSB.dll version 4.00 is a 32 bit dll that can be used to successfully access the LabPro on both 32 bit and 64 bit Windows systems. Earlier versions of LabProUSB.dll only worked on 32 bit Windows systems. Because LabProUSB.dll version 4.00 is only a 32 bit binary, it cannot be used by 64 bit programs.

LabProUSB.dll version 4.00 is dependent on wdapi921.dll.

Wdapi921.dll comes in two flavors, a version that runs on Windows 32 bit systems called wdapi921_for_x32.dll, and a version that runs on Windows 64 bit systems called wdapi921_for_x64.dll. When you install LabProUSB.dll on your system, you must copy the correct version of wdapi921_for_xNN.dll into the same folder where LabProUSB.dll resides, and rename wdapi921_for_xNN.dll to wdapi921.dll.

So on Windows 7, Vista, and XP 32 bit systems, rename wdapi921_for_x32.dll to wdapi921.dll.

On Windows 7, Vista, and XP 64 bit systems, rename wdapi921_for_x64.dll to wdapi921.dll.
