CFLAGS = -Iftdi-release

SWIG = swig
#SWIG = /cygdrive/c/swig-1.3.24/swig

SWIG_OUTPUT_DIR = src/swig/java
SWIG_PACKAGE_DIR = $(SWIG_OUTPUT_DIR)/org/concord/sensor/labprousb
SWIG_PACKAGE = org.concord.sensor.labprousb
SWIG_MODULE = LabProUSBWrapper

SWIG_DLL_FILE = labprousb_wrapper.dll

LIBRARY_INCLUDE_DIR = labpro-sdk/API
LIBRARY_INCLUDE_FILE = labpro-sdk/API/LabProUSB_interface.h
LIBRARY_LIBRARY_FILE = labpro-sdk/API/LabProUSB.lib

SWIG_MODULE_C_FILE = src/swig/${SWIG_MODULE}_wrap.c 
SWIG_MODULE_O_FILE = nativelib/swig/${SWIG_MODULE}_wrap.o
SWIG_MODULE_INTERFACE_FILE = src/swig/${SWIG_MODULE}.i  

WIN32_API_DIR = C:/cygwin/usr/include/w32api

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

nativelib/swig/%.o : src/swig/%.c nativelib/swig
	$(CC) -c $< -I/usr/java/include -I/usr/java/include/win32 -I${LIBRARY_INCLUDE_DIR} -o $@

bin nativelib nativelib/test nativelib/swig $(SWIG_PACKAGE_DIR):
	mkdir -p $@

.PHONY : swig

# you must install swig to run this target. http://swig.org
# it expects the binary to be on your path and the include files to
# be in a stardard include folder.

${SWIG_MODULE_C_FILE} : ${LIBRARY_INCLUDE_FILE} ${SWIG_MODULE_INTERFACE_FILE} $(SWIG_PACKAGE_DIR)
	$(SWIG) -java -I${WIN32_API_DIR} -I${LIBRARY_INCLUDE_DIR} -package ${SWIG_PACKAGE} -outdir $(SWIG_PACKAGE_DIR) -o ${SWIG_MODULE_C_FILE} ${SWIG_MODULE_INTERFACE_FILE}  

swig: ${SWIG_MODULE_C_FILE}

bin/${SWIG_DLL_FILE} : 	${SWIG_MODULE_O_FILE}  ${LIBRARY_LIBRARY_FILE}
	$(build-jni-dll)

clean:

# remove the default rule so we can change the source
# and lib folders
%.o : %.c	