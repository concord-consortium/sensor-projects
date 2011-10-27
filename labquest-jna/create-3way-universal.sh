#!/bin/sh
lipo -extract ppc NGIO_SDK/redist/NGIO_lib/mac/libNGIO.dylib -output src/main/resources/org/concord/sensor/labquest/jna/darwin/libNGIOppc.dylib
lipo -create NGIO_SDK/redist/NGIO_lib/mac/libNGIOUniversal.dylib src/main/resources/org/concord/sensor/labquest/jna/darwin/libNGIOppc.dylib -output src/main/resources/org/concord/sensor/labquest/jna/darwin/libNGIOUniversal3way.dylib
rm -f src/main/resources/org/concord/sensor/labquest/jna/darwin/libNGIOppc.dylib
