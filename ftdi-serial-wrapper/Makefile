CFLAGS = -Iftdi-release

#SWIG = swig
SWIG = /cygdrive/c/swig-1.3.24/swig

SWIG_OUTPUT_DIR = src/swig/java

# this is used to build native test executables
# this filter grabs the .o and .lib files from the dependency list
define build-static-executable
gcc -o $@ $(filter %.o %.lib,$^)  
endef

# this builds a jni comptable dll with gcc
# this filter grabs the .o and .lib files from the dependency list
define build-jni-dll
gcc -mno-cygwin -shared $(filter %.o %.lib,$^) -Wl,--add-stdcall-alias -o $@
endef

all: swig

nativelib/test/%.o : src/c/test/%.c include/CCSensorDevice.h src/c/test/CCSensorUtils.h nativelib/test
	$(CC) $(CFLAGS) -c $< -o $@

nativelib/swig/%.o : src/swig/%.c nativelib/swig
	$(CC) -c $< -I/usr/java/include -I/usr/java/include/win32 -Iftdi-release -o $@

nativelib/%.o : src/c/%.c include/CCSensorDevice.h nativelib nativelib/test
	$(CC) $(CFLAGS) -c $< -o $@

bin nativelib nativelib/test nativelib/swig $(SWIG_OUTPUT_DIR)/ccsd/vernier $(SWIG_OUTPUT_DIR)/org/concord/ftdi:
	mkdir -p $@

.PHONY : swig

# you must install swig to run this target. http://swig.org
# it expects the binary to be on your path and the include files to
# be in a stardard include folder.

swig : ftdi-release/FTD2XX.h src/swig/FtdiSerialWrapper.i $(SWIG_OUTPUT_DIR)/org/concord/ftdi
	$(SWIG) -java -IC:/cygwin/usr/include/w32api -Iftdi-release -package org.concord.ftdi -outdir src/swig/java/org/concord/ftdi -o src/swig/FtdiSerialWrapper_wrap.c src/swig/FtdiSerialWrapper.i  

bin/ftdi_wrapper.dll : 	swig nativelib/swig/FtdiSerialWrapper_wrap.o ftdi-release/FTD2XX.lib bin
	$(build-jni-dll)

clean:

# remove the default rule so we can change the source
# and lib folders
%.o : %.c	