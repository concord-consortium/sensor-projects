### Summary

This is a set of Java libraries for communicating with various types of sensor interfaces that
are typically used in schools.

### Getting started

This project uses maven for building. Most developement is done in Eclipse with the m2e plugin,
but you should be able to work on it with just maven at the commandline or any IDE with maven support.

The current place to get started is to look at the ExampleSensorApp class. You can subclass this
and modify the deviceId and possibly openString in order to test out a specific device.
The list of available 'deviceId's is in the DeviceID interface.

If you don't have any hardware you can start with the PseudoSensorExampleApp which extends ExampleSensorApp.

### Architecture

There code in the sensor sub project provides two APIs

1. one for application developers to use sensors without writing vendor or device specific code
2. one to add support for a new device to the framework

The main interface for both APIs is the org.concord.sensor.device.SensorDevice
There are javadocs in this interface describing how it is intended to be used.

A summary of one way to use the application API goes like this:

1. The Application uses a DeviceFactory to get an instance of a SensorDevice
2. The Application creates a ExperientRequest consisting of one or more SensorRequests.
   This describes what sensors and timing the Application wants to use.
3. The Application calls SensorDevice#configure with this ExperimentRequest
4. The SensorDevice implementation asks the hardware what sensors are actually attached,
   compares it to the requested sensors, and then returns a ExperiementConfig that contains
   the set of sensors which the SensorDevice feels is the best match for the request.
   This ExperiementConfig also indicates if the SensorDevice thinks the config meets the
   requirements of the request. So for example if the ExperimentRequest contains a temperature
   sensor but no temperature sensor is attached, the returned ExperimentConfig should indicate
   the requirements are not met.
5. The Application then calls SensorDevice#start to start collecting data from the sensors
   specified in the most recently returned ExperimentConfig.
6. The Application calls SensorDevice#read again and again to get the resulting data.
7. The Application calls SensorDevice#stop when it doesn't want to read anymore data.


### TODO

- move ftdi-serial-wrapper into its own repository, it isn't used by any of this code and could be a useful independent project for someone
- get jars deployed to maven central
- update documentation to make it really easy to try out by pulling jars from central
- remove sensor native
- make sure the native libraries are not loaded multiple times in a single jvm
- make a command line app that lists the available devices, and runs the 2 tests on whichever device the user selects
- move the matching of current configuration of sensors to a different API layer. This simplifies the
  the implementation for the Vendors, and it also provides a simplier API for Application developers
- update the API to support checking if a device is attached without actually opening it, also this API
  should support opening multiple devices of the same type attached to the same computer. Perhaps it would work to
  have the DeviceFactory return a list of devices which aren't opened. These devices can then be queried to see
  if they are actually available(they might be in use by us or some other program). And they can be opened. It is also nice
  though to work without the device factory. So it might be best to introduce a 'Library' style object for each device.
  So a GoIOLibrary sigleton can be created and this can be used to list the GoIO devices. The DeviceFactory can use this.
- boil DeviceService down to just logging and user messages, the rest of the methods can be removed or made static, they
  are legacy for Waba support
- Move DeviceFactory out of this library and make the creation of devices more like 'new SomethingDevice(deviceService, optional_type)'
