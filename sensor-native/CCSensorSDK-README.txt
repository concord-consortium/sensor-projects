CCSensorSDK  1/20/2005

FILES

JavaTest/                    - demo java application
SensorNative/
  Makefile                   - gnu make file for building all native code

  maven.xml                  \
  project.xml                 -  maven files for building jave code  
  project.properites         /

  .cdtproject                \
  .classpath                  -  eclipse project files for work with native and java code  
  .project                   /  

  include/
    CCSensorDevice.h         - main include file and documentation of API
  src/
    c/                       - native SensorDevice implementations
      TISensorDevice.c       - template for native SensorDevice
      PsuedoSensorDevice.c   - template for native SensorDevice
      GoLinkSensorDevice.c   - working sensor device talks to GoLink
    swig/                    - wrapper files for java native interface (JNI)
      CCSensorDevice.i       - main swig file, the rest of the files in this are generated      
    java/                    - partial java source for native sensors
  nativelib/                 - built .o files 
    swig/                    - built wrapper files need to build Java native interface dll
    test/                    - built test .o files
  bin/                       - built dlls
  lib/                       - built java classes
  target/                    - more built java classes

MAKEFILE

There is a gnu make file included in the SensorNative directory.  This make
file has only been tested inside of the cygwin environment.

The following targets Will build the TiSensorDeivce.c file.

bin/ti_ccsd.dll - builds a Java JNI compatible dll.  This can be used 
  with the test java application in the JavaTest folder.

bin/TIQuery - native only test application that asks the device for
  which sensors are attached and prints out the devices response.

bin/TIPrintData - this requests a temperature sensor config, starts
  the device and prints the result.  It can be customized by editing
  the SensorNative/src/c/test/PrintData.c file.


BUILDING NATIVE TESTS

It is probably best to build the TIQuery or TIPrintData executables first.
If you have cygwin installed you should be able to use the included Makefile
to build them with the targest above.  Otherwise here are the files for each:

TIQuery:
include/CCSensorDevice.h
src/c/test/QueryDevice.c
src/c/test/CCSensorUtils.c
src/c/test/CCSensorUtils.h
src/c/TISensorDevice.c

TIPrintData:
include/CCSensorDevice.h
src/c/test/PrintData.c
src/c/test/CCSensorUtils.c
src/c/test/CCSensorUtils.h
src/c/TISensorDevice.c


BUILDING JNI DLL

Several generated files are included so you can build this dll without extra
tools or sdks. 
If you have cygwin installed you can probably build it by using the target above.
Otherwise follow these steps:

1. compile TISensorDevice.c
  You should only need include/CCSensorDevice.h to do this.

2. create the dll by linking the following files:
  CCSensorDeivce.o            (found in nativelib/ if you used included Makefile)
  nativelib/TISensorDevice_wrap.o

Java is picky about the format of dlls it loads.  gcc must be passed 
"--add-stdcall-alias" so that it adds pascal style declarations to the dll.  I 
think the Visual Studio compiler does this automatically.
     

RUNNING JAVA TEST
This program should run as is.  So it would be good to make sure it works before
replacing any of the dlls with ones you have built.

Test it as is:
- install the java sdk
- execute run.bat  
    this bat file assumes java.exe is in your path.  the java sdk installer will
   do this for you.  there is also a "java net beans" installer that doesn't put
   it on the path automatically.
- watch the console for messages.  
- you should see a window with a tree on the left.  
- if you click on the items of the tree it will open the sensor device
and if you click the start button it should collect data from the device.

Test a dll you've built
- make sure the program is stopped
- copy the jni dll to JavaTest/nativelib
- if there are other support dlls put them in JavaTest/nativelib to
- the main dll must be named ti_ccsd.dll

The configs that are passed to the Sensor device are taken from:
JavaTest/sensor_config.xml
  Each item on the tree cooresponds to a OTSensorDataProxy element.  The config
  itself is in the OTSensorRequest element.

Please call or write if you have questions:
Scott Cytacki
978 371 3488
scott@concord.org