I assume you are reading this if you downloaded the archive of the source for the Sensor
package.  This should have given you the following folders:

AppleStub - this is a stub for classes available on the mac
Framework - this is a set of interfaces used throughout our code
ProjectSets - this is where the global build system files are stored
Sensor - this is the main folder for this distribution
thirdparty - a set of libraries need for this distribution 
WabaJVM - waba classes for running waba on a jvm

To build this project you need:
- apache ant 1.6.1 or later.
Then go to the Sensor directory and run 
> ant all-depends

To run this project you need:
- rxtx 2.1.7 binary drivers.  you can download these at rxtx.org
To test it run 
> java -jar Sensor/lib/sensor.jar
This should start serial port and look for a CC interface.  By default
it looks for a temperature probe attached to the CC interface.

The first time you run it it should show a dialog asking you to 
pick your serial port.  
You might have to run it again after picking this port.  

Included in the project are project files for eclipse 3.0.
(www.eclispe.org)
as well as ant build files.

During the distribution bundling process this file is copied to the top level
folder of the distribution.  If you want to change this file you should change
Sensor/doc/dist-README.txt

