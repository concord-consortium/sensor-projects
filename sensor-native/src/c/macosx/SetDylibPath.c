#include <JavaVM/jni.h>
#include <string.h>
#include <stdlib.h>
#include <mach-o/dyld.h>
#include <mach-o/ldsyms.h> //defines _mh_bundle_header

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
     int i;
     struct mach_header* header = (struct mach_header*)&_mh_bundle_header;
     const char* imagename = 0;
     int cnt = _dyld_image_count();
     for (i = 1; i < cnt; i++) {
         if (_dyld_get_image_header(i) == header)
             imagename = _dyld_get_image_name(i);//contains absolute path to dylib
         if(imagename != NULL) break;
     }
     if(imagename != NULL){
         char *lastSlash = strrchr((const char *)imagename,'/');
         if(lastSlash != NULL) *lastSlash = 0;
         char *envVar = getenv("DYLD_LIBRARY_PATH");
         char *newPath = NULL;
         if(envVar == NULL){
             newPath = (char *)malloc(strlen(imagename)+1);
             strcpy(newPath,(const char *)imagename);
             newPath[strlen((const char *)imagename)] = 0;
         }else{
             newPath = (char *)malloc(strlen(imagename)+strlen(envVar)+2);
             char *toCopy = newPath;
             strcpy(toCopy,(const char *)envVar);
             toCopy += strlen(envVar);
             strcpy(toCopy,":");
             toCopy ++;
             strcpy(toCopy,(const char *)imagename);
             toCopy += strlen(imagename);
             *toCopy = 0;
         }
         setenv("DYLD_LIBRARY_PATH",newPath,1);
         free(newPath);
     }
     return JNI_VERSION_1_4;
}

