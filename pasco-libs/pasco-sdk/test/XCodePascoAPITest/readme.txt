All of this should be cleaned up with a script, or by tweaking the XCode project config.

To make the executable find it dylibs regardless of what the current directory is the following command should be run:
install_name_tool -change pasco-sdk.dylib @loader_path/pasco-sdk.dylib XCodePascoAPITest
install_name_tool -change libqtcore4d.dylib @loader_path/libqtcore4d.dylib XCodePascoAPITest

I believe that can be taken care of in the XCode project config.

You can run otool -D to list the install_name of a library (that is what gets changed by the -id param to install_name_tool)
And you can run otool -L to list all of the referenced libraries.

Also the pasco-sdk.dylib and libqtcore4d.dylib had their id changed after they were copied into the lib/os_x folder, by running:
install_name_tool -id pasco-sdk.dylib pasco-sdk.dylib

Doing that caused linker when it built XCodePascoAPITest executable to use the original install_names listed above.