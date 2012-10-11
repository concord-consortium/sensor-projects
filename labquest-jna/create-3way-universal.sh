#!/bin/sh

#
# The Vernier 1.95 version of the NGIO SDK comes with 3 mac libraries:
#   libNGIO  (fat file with i386 ppc)
#   libNGIOUniversal (fat file with i386 ppc740 x86_64)
#   libNGIO64 (thin file x86_64)
#
# The script creates a single file that has all the architectures.
#
# Notes:
# If you extract the x86_64 file from libNGIOUniversal it is identical to libNGIO64
# But if you extract the i386 file from libNGIO and libNGIOUniversal they are not the same
# the libNGIO version of the i386 works better. the one in libNGIOUniversal can't reopen a device
# after closing it

export NGIO_MAC_DIR=NGIO_SDK/redist/NGIO_lib/mac

mkdir -p target/ngio
rm -f target/ngio/*
lipo -extract ppc     $NGIO_MAC_DIR/libNGIO.dylib          -output target/ngio/libNGIO1.dylib
lipo -extract i386    $NGIO_MAC_DIR/libNGIO.dylib          -output target/ngio/libNGIO2.dylib
lipo -extract ppc7400 $NGIO_MAC_DIR/libNGIOUniversal.dylib -output target/ngio/libNGIO3.dylib
lipo -extract x86_64  $NGIO_MAC_DIR/libNGIOUniversal.dylib -output target/ngio/libNGIO4.dylib

lipo -create target/ngio/* -output src/main/resources/org/concord/sensor/labquest/jna/darwin/libNGIOUniversal3way.dylib
lipo -info src/main/resources/org/concord/sensor/labquest/jna/darwin/libNGIOUniversal3way.dylib

rm -r target/ngio
