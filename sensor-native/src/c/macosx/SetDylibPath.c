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
         char *needPath = NULL;
         char *lastSlash = strrchr((const char *)imagename,'/');
         int length1 = (lastSlash == NULL)?strlen(imagename):(int)(lastSlash - imagename);
         needPath = (char *)malloc(length1+1);
         strncpy(needPath,(const char *)imagename,length1);
         needPath[length1] = 0;
         printf("needPath %s\n",needPath);
         chdir(needPath);
         free(needPath);
     }
     return JNI_VERSION_1_4;
}

