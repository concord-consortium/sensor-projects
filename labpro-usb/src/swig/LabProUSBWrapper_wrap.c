/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.29
 * 
 * This file is not intended to be easily readable and contains a number of 
 * coding conventions designed to improve portability and efficiency. Do not make
 * changes to this file unless you know what you are doing--modify the SWIG 
 * interface file instead. 
 * ----------------------------------------------------------------------------- */

/* -----------------------------------------------------------------------------
 *  This section contains generic SWIG labels for method/variable
 *  declarations/attributes, and other compiler dependent labels.
 * ----------------------------------------------------------------------------- */

/* template workaround for compilers that cannot correctly implement the C++ standard */
#ifndef SWIGTEMPLATEDISAMBIGUATOR
# if defined(__SUNPRO_CC)
#   if (__SUNPRO_CC <= 0x560)
#     define SWIGTEMPLATEDISAMBIGUATOR template
#   else
#     define SWIGTEMPLATEDISAMBIGUATOR 
#   endif
# else
#   define SWIGTEMPLATEDISAMBIGUATOR 
# endif
#endif

/* inline attribute */
#ifndef SWIGINLINE
# if defined(__cplusplus) || (defined(__GNUC__) && !defined(__STRICT_ANSI__))
#   define SWIGINLINE inline
# else
#   define SWIGINLINE
# endif
#endif

/* attribute recognised by some compilers to avoid 'unused' warnings */
#ifndef SWIGUNUSED
# if defined(__GNUC__)
#   if !(defined(__cplusplus)) || (__GNUC__ > 3 || (__GNUC__ == 3 && __GNUC_MINOR__ >= 4))
#     define SWIGUNUSED __attribute__ ((__unused__)) 
#   else
#     define SWIGUNUSED
#   endif
# elif defined(__ICC)
#   define SWIGUNUSED __attribute__ ((__unused__)) 
# else
#   define SWIGUNUSED 
# endif
#endif

#ifndef SWIGUNUSEDPARM
# ifdef __cplusplus
#   define SWIGUNUSEDPARM(p)
# else
#   define SWIGUNUSEDPARM(p) p SWIGUNUSED 
# endif
#endif

/* internal SWIG method */
#ifndef SWIGINTERN
# define SWIGINTERN static SWIGUNUSED
#endif

/* internal inline SWIG method */
#ifndef SWIGINTERNINLINE
# define SWIGINTERNINLINE SWIGINTERN SWIGINLINE
#endif

/* exporting methods */
#if (__GNUC__ >= 4) || (__GNUC__ == 3 && __GNUC_MINOR__ >= 4)
#  ifndef GCC_HASCLASSVISIBILITY
#    define GCC_HASCLASSVISIBILITY
#  endif
#endif

#ifndef SWIGEXPORT
# if defined(_WIN32) || defined(__WIN32__) || defined(__CYGWIN__)
#   if defined(STATIC_LINKED)
#     define SWIGEXPORT
#   else
#     define SWIGEXPORT __declspec(dllexport)
#   endif
# else
#   if defined(__GNUC__) && defined(GCC_HASCLASSVISIBILITY)
#     define SWIGEXPORT __attribute__ ((visibility("default")))
#   else
#     define SWIGEXPORT
#   endif
# endif
#endif

/* calling conventions for Windows */
#ifndef SWIGSTDCALL
# if defined(_WIN32) || defined(__WIN32__) || defined(__CYGWIN__)
#   define SWIGSTDCALL __stdcall
# else
#   define SWIGSTDCALL
# endif 
#endif

/* Deal with Microsoft's attempt at deprecating C standard runtime functions */
#if !defined(SWIG_NO_CRT_SECURE_NO_DEPRECATE) && defined(_MSC_VER)
# define _CRT_SECURE_NO_DEPRECATE
#endif


/* Fix for jlong on some versions of gcc on Windows */
#if defined(__GNUC__) && !defined(__INTELC__)
  typedef long long __int64;
#endif

/* Fix for jlong on 64-bit x86 Solaris */
#if defined(__x86_64)
# ifdef _LP64
#   undef _LP64
# endif
#endif

#include <jni.h>
#include <stdlib.h>
#include <string.h>


/* Support for throwing Java exceptions */
typedef enum {
  SWIG_JavaOutOfMemoryError = 1, 
  SWIG_JavaIOException, 
  SWIG_JavaRuntimeException, 
  SWIG_JavaIndexOutOfBoundsException,
  SWIG_JavaArithmeticException,
  SWIG_JavaIllegalArgumentException,
  SWIG_JavaNullPointerException,
  SWIG_JavaDirectorPureVirtual,
  SWIG_JavaUnknownError
} SWIG_JavaExceptionCodes;

typedef struct {
  SWIG_JavaExceptionCodes code;
  const char *java_exception;
} SWIG_JavaExceptions_t;


static void SWIGUNUSED SWIG_JavaThrowException(JNIEnv *jenv, SWIG_JavaExceptionCodes code, const char *msg) {
  jclass excep;
  static const SWIG_JavaExceptions_t java_exceptions[] = {
    { SWIG_JavaOutOfMemoryError, "java/lang/OutOfMemoryError" },
    { SWIG_JavaIOException, "java/io/IOException" },
    { SWIG_JavaRuntimeException, "java/lang/RuntimeException" },
    { SWIG_JavaIndexOutOfBoundsException, "java/lang/IndexOutOfBoundsException" },
    { SWIG_JavaArithmeticException, "java/lang/ArithmeticException" },
    { SWIG_JavaIllegalArgumentException, "java/lang/IllegalArgumentException" },
    { SWIG_JavaNullPointerException, "java/lang/NullPointerException" },
    { SWIG_JavaDirectorPureVirtual, "java/lang/RuntimeException" },
    { SWIG_JavaUnknownError,  "java/lang/UnknownError" },
    { (SWIG_JavaExceptionCodes)0,  "java/lang/UnknownError" } };
  const SWIG_JavaExceptions_t *except_ptr = java_exceptions;

  while (except_ptr->code != code && except_ptr->code)
    except_ptr++;

  (*jenv)->ExceptionClear(jenv);
  excep = (*jenv)->FindClass(jenv, except_ptr->java_exception);
  if (excep)
    (*jenv)->ThrowNew(jenv, excep, msg);
}


/* Contract support */

#define SWIG_contract_assert(nullreturn, expr, msg) if (!(expr)) {SWIG_JavaThrowException(jenv, SWIG_JavaIllegalArgumentException, msg); return nullreturn; } else


#include "windows.h"
#include "LabProUSB_interface.h"


#if defined(SWIG_NOINCLUDE) || defined(SWIG_NOARRAYS)


int SWIG_JavaArrayInSchar (JNIEnv *jenv, jbyte **jarr, signed char **carr, jbyteArray input);
void SWIG_JavaArrayArgoutSchar (JNIEnv *jenv, jbyte *jarr, signed char *carr, jbyteArray input);
jbyteArray SWIG_JavaArrayOutSchar (JNIEnv *jenv, signed char *result, jsize sz);


int SWIG_JavaArrayInUchar (JNIEnv *jenv, jshort **jarr, unsigned char **carr, jshortArray input);
void SWIG_JavaArrayArgoutUchar (JNIEnv *jenv, jshort *jarr, unsigned char *carr, jshortArray input);
jshortArray SWIG_JavaArrayOutUchar (JNIEnv *jenv, unsigned char *result, jsize sz);


int SWIG_JavaArrayInShort (JNIEnv *jenv, jshort **jarr, short **carr, jshortArray input);
void SWIG_JavaArrayArgoutShort (JNIEnv *jenv, jshort *jarr, short *carr, jshortArray input);
jshortArray SWIG_JavaArrayOutShort (JNIEnv *jenv, short *result, jsize sz);


int SWIG_JavaArrayInUshort (JNIEnv *jenv, jint **jarr, unsigned short **carr, jintArray input);
void SWIG_JavaArrayArgoutUshort (JNIEnv *jenv, jint *jarr, unsigned short *carr, jintArray input);
jintArray SWIG_JavaArrayOutUshort (JNIEnv *jenv, unsigned short *result, jsize sz);


int SWIG_JavaArrayInInt (JNIEnv *jenv, jint **jarr, int **carr, jintArray input);
void SWIG_JavaArrayArgoutInt (JNIEnv *jenv, jint *jarr, int *carr, jintArray input);
jintArray SWIG_JavaArrayOutInt (JNIEnv *jenv, int *result, jsize sz);


int SWIG_JavaArrayInUint (JNIEnv *jenv, jlong **jarr, unsigned int **carr, jlongArray input);
void SWIG_JavaArrayArgoutUint (JNIEnv *jenv, jlong *jarr, unsigned int *carr, jlongArray input);
jlongArray SWIG_JavaArrayOutUint (JNIEnv *jenv, unsigned int *result, jsize sz);


int SWIG_JavaArrayInLong (JNIEnv *jenv, jint **jarr, long **carr, jintArray input);
void SWIG_JavaArrayArgoutLong (JNIEnv *jenv, jint *jarr, long *carr, jintArray input);
jintArray SWIG_JavaArrayOutLong (JNIEnv *jenv, long *result, jsize sz);


int SWIG_JavaArrayInUlong (JNIEnv *jenv, jlong **jarr, unsigned long **carr, jlongArray input);
void SWIG_JavaArrayArgoutUlong (JNIEnv *jenv, jlong *jarr, unsigned long *carr, jlongArray input);
jlongArray SWIG_JavaArrayOutUlong (JNIEnv *jenv, unsigned long *result, jsize sz);


int SWIG_JavaArrayInLonglong (JNIEnv *jenv, jlong **jarr, jlong **carr, jlongArray input);
void SWIG_JavaArrayArgoutLonglong (JNIEnv *jenv, jlong *jarr, jlong *carr, jlongArray input);
jlongArray SWIG_JavaArrayOutLonglong (JNIEnv *jenv, jlong *result, jsize sz);


int SWIG_JavaArrayInFloat (JNIEnv *jenv, jfloat **jarr, float **carr, jfloatArray input);
void SWIG_JavaArrayArgoutFloat (JNIEnv *jenv, jfloat *jarr, float *carr, jfloatArray input);
jfloatArray SWIG_JavaArrayOutFloat (JNIEnv *jenv, float *result, jsize sz);


int SWIG_JavaArrayInDouble (JNIEnv *jenv, jdouble **jarr, double **carr, jdoubleArray input);
void SWIG_JavaArrayArgoutDouble (JNIEnv *jenv, jdouble *jarr, double *carr, jdoubleArray input);
jdoubleArray SWIG_JavaArrayOutDouble (JNIEnv *jenv, double *result, jsize sz);


#else


/* signed char[] support */
int SWIG_JavaArrayInSchar (JNIEnv *jenv, jbyte **jarr, signed char **carr, jbyteArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetByteArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (signed char*) calloc(sz, sizeof(signed char)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (signed char)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutSchar (JNIEnv *jenv, jbyte *jarr, signed char *carr, jbyteArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jbyte)carr[i];
  (*jenv)->ReleaseByteArrayElements(jenv, input, jarr, 0);
}

jbyteArray SWIG_JavaArrayOutSchar (JNIEnv *jenv, signed char *result, jsize sz) {
  jbyte *arr;
  int i;
  jbyteArray jresult = (*jenv)->NewByteArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetByteArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jbyte)result[i];
  (*jenv)->ReleaseByteArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* unsigned char[] support */
int SWIG_JavaArrayInUchar (JNIEnv *jenv, jshort **jarr, unsigned char **carr, jshortArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetShortArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (unsigned char*) calloc(sz, sizeof(unsigned char)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (unsigned char)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutUchar (JNIEnv *jenv, jshort *jarr, unsigned char *carr, jshortArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jshort)carr[i];
  (*jenv)->ReleaseShortArrayElements(jenv, input, jarr, 0);
}

jshortArray SWIG_JavaArrayOutUchar (JNIEnv *jenv, unsigned char *result, jsize sz) {
  jshort *arr;
  int i;
  jshortArray jresult = (*jenv)->NewShortArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetShortArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jshort)result[i];
  (*jenv)->ReleaseShortArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* short[] support */
int SWIG_JavaArrayInShort (JNIEnv *jenv, jshort **jarr, short **carr, jshortArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetShortArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (short*) calloc(sz, sizeof(short)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (short)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutShort (JNIEnv *jenv, jshort *jarr, short *carr, jshortArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jshort)carr[i];
  (*jenv)->ReleaseShortArrayElements(jenv, input, jarr, 0);
}

jshortArray SWIG_JavaArrayOutShort (JNIEnv *jenv, short *result, jsize sz) {
  jshort *arr;
  int i;
  jshortArray jresult = (*jenv)->NewShortArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetShortArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jshort)result[i];
  (*jenv)->ReleaseShortArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* unsigned short[] support */
int SWIG_JavaArrayInUshort (JNIEnv *jenv, jint **jarr, unsigned short **carr, jintArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetIntArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (unsigned short*) calloc(sz, sizeof(unsigned short)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (unsigned short)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutUshort (JNIEnv *jenv, jint *jarr, unsigned short *carr, jintArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jint)carr[i];
  (*jenv)->ReleaseIntArrayElements(jenv, input, jarr, 0);
}

jintArray SWIG_JavaArrayOutUshort (JNIEnv *jenv, unsigned short *result, jsize sz) {
  jint *arr;
  int i;
  jintArray jresult = (*jenv)->NewIntArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetIntArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jint)result[i];
  (*jenv)->ReleaseIntArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* int[] support */
int SWIG_JavaArrayInInt (JNIEnv *jenv, jint **jarr, int **carr, jintArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetIntArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (int*) calloc(sz, sizeof(int)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (int)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutInt (JNIEnv *jenv, jint *jarr, int *carr, jintArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jint)carr[i];
  (*jenv)->ReleaseIntArrayElements(jenv, input, jarr, 0);
}

jintArray SWIG_JavaArrayOutInt (JNIEnv *jenv, int *result, jsize sz) {
  jint *arr;
  int i;
  jintArray jresult = (*jenv)->NewIntArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetIntArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jint)result[i];
  (*jenv)->ReleaseIntArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* unsigned int[] support */
int SWIG_JavaArrayInUint (JNIEnv *jenv, jlong **jarr, unsigned int **carr, jlongArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetLongArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (unsigned int*) calloc(sz, sizeof(unsigned int)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (unsigned int)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutUint (JNIEnv *jenv, jlong *jarr, unsigned int *carr, jlongArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jlong)carr[i];
  (*jenv)->ReleaseLongArrayElements(jenv, input, jarr, 0);
}

jlongArray SWIG_JavaArrayOutUint (JNIEnv *jenv, unsigned int *result, jsize sz) {
  jlong *arr;
  int i;
  jlongArray jresult = (*jenv)->NewLongArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetLongArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jlong)result[i];
  (*jenv)->ReleaseLongArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* long[] support */
int SWIG_JavaArrayInLong (JNIEnv *jenv, jint **jarr, long **carr, jintArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetIntArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (long*) calloc(sz, sizeof(long)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (long)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutLong (JNIEnv *jenv, jint *jarr, long *carr, jintArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jint)carr[i];
  (*jenv)->ReleaseIntArrayElements(jenv, input, jarr, 0);
}

jintArray SWIG_JavaArrayOutLong (JNIEnv *jenv, long *result, jsize sz) {
  jint *arr;
  int i;
  jintArray jresult = (*jenv)->NewIntArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetIntArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jint)result[i];
  (*jenv)->ReleaseIntArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* unsigned long[] support */
int SWIG_JavaArrayInUlong (JNIEnv *jenv, jlong **jarr, unsigned long **carr, jlongArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetLongArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (unsigned long*) calloc(sz, sizeof(unsigned long)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (unsigned long)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutUlong (JNIEnv *jenv, jlong *jarr, unsigned long *carr, jlongArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jlong)carr[i];
  (*jenv)->ReleaseLongArrayElements(jenv, input, jarr, 0);
}

jlongArray SWIG_JavaArrayOutUlong (JNIEnv *jenv, unsigned long *result, jsize sz) {
  jlong *arr;
  int i;
  jlongArray jresult = (*jenv)->NewLongArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetLongArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jlong)result[i];
  (*jenv)->ReleaseLongArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* jlong[] support */
int SWIG_JavaArrayInLonglong (JNIEnv *jenv, jlong **jarr, jlong **carr, jlongArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetLongArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (jlong*) calloc(sz, sizeof(jlong)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (jlong)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutLonglong (JNIEnv *jenv, jlong *jarr, jlong *carr, jlongArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jlong)carr[i];
  (*jenv)->ReleaseLongArrayElements(jenv, input, jarr, 0);
}

jlongArray SWIG_JavaArrayOutLonglong (JNIEnv *jenv, jlong *result, jsize sz) {
  jlong *arr;
  int i;
  jlongArray jresult = (*jenv)->NewLongArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetLongArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jlong)result[i];
  (*jenv)->ReleaseLongArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* float[] support */
int SWIG_JavaArrayInFloat (JNIEnv *jenv, jfloat **jarr, float **carr, jfloatArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetFloatArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (float*) calloc(sz, sizeof(float)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (float)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutFloat (JNIEnv *jenv, jfloat *jarr, float *carr, jfloatArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jfloat)carr[i];
  (*jenv)->ReleaseFloatArrayElements(jenv, input, jarr, 0);
}

jfloatArray SWIG_JavaArrayOutFloat (JNIEnv *jenv, float *result, jsize sz) {
  jfloat *arr;
  int i;
  jfloatArray jresult = (*jenv)->NewFloatArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetFloatArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jfloat)result[i];
  (*jenv)->ReleaseFloatArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


/* double[] support */
int SWIG_JavaArrayInDouble (JNIEnv *jenv, jdouble **jarr, double **carr, jdoubleArray input) {
  int i;
  jsize sz;
  if (!input) {
    SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "null array");
    return 0;
  }
  sz = (*jenv)->GetArrayLength(jenv, input);
  *jarr = (*jenv)->GetDoubleArrayElements(jenv, input, 0);
  if (!*jarr)
    return 0; 
  *carr = (double*) calloc(sz, sizeof(double)); 
  if (!*carr) {
    SWIG_JavaThrowException(jenv, SWIG_JavaOutOfMemoryError, "array memory allocation failed");
    return 0;
  }
  for (i=0; i<sz; i++)
    (*carr)[i] = (double)(*jarr)[i];
  return 1;
}

void SWIG_JavaArrayArgoutDouble (JNIEnv *jenv, jdouble *jarr, double *carr, jdoubleArray input) {
  int i;
  jsize sz = (*jenv)->GetArrayLength(jenv, input);
  for (i=0; i<sz; i++)
    jarr[i] = (jdouble)carr[i];
  (*jenv)->ReleaseDoubleArrayElements(jenv, input, jarr, 0);
}

jdoubleArray SWIG_JavaArrayOutDouble (JNIEnv *jenv, double *result, jsize sz) {
  jdouble *arr;
  int i;
  jdoubleArray jresult = (*jenv)->NewDoubleArray(jenv, sz);
  if (!jresult)
    return NULL;
  arr = (*jenv)->GetDoubleArrayElements(jenv, jresult, 0);
  if (!arr)
    return NULL;
  for (i=0; i<sz; i++)
    arr[i] = (jdouble)result[i];
  (*jenv)->ReleaseDoubleArrayElements(jenv, jresult, arr, 0);
  return jresult;
}


#endif



short int open()
{
   return LabProUSB_Open();
}

short int close()
{
	return LabProUSB_Close();
}

short int isOpen()
{
	return LabProUSB_IsOpen();
}

long  int getAvailableBytes()
{
	return LabProUSB_GetAvailableBytes();
}

// returns the number of bytes read or -1 if there was an error
long int readBytes( long numBytesRequested, signed char *buffer )
{
    short ret = 0;
	long N = numBytesRequested;
	
    // long *N on input is the number of bytes requested
	// long *N on output is the number of bytes recieved.
	ret =  LabProUSB_ReadBytes( &N, buffer );
	if(ret >= 0) {
	  return N;
	}

	// ret should be negative one in this case	
	return ret;
}

// returns the number of bytes written or -1 if there was an error
short int writeBytes( short numBytesRequested, signed char *buffer )
{

    short ret = 0;
	short N = numBytesRequested;

    // long *N - Input: number of bytes requested.  Output: number actually written
	ret = LabProUSB_WriteBytes( &N, buffer );
	if(ret >= 0) {
	  return N;
	}

	// ret should be negative one in this case	
	return ret;	
}

short int clearInputs( short int nothing )
{
	return LabProUSB_ClearInputs( nothing );
}

short int setNumChannelsAndModes(int nNumChannels, short bBinaryMode, short bRealTime)
{
	return LabProUSB_SetNumChannelsAndModes(nNumChannels, bBinaryMode, bRealTime);
}




#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jshort JNICALL Java_org_concord_sensor_labprousb_LabProUSBJNI_open(JNIEnv *jenv, jclass jcls) {
  jshort jresult = 0 ;
  short result;
  
  (void)jenv;
  (void)jcls;
  result = (short)open();
  jresult = (jshort)result; 
  return jresult;
}


JNIEXPORT jshort JNICALL Java_org_concord_sensor_labprousb_LabProUSBJNI_close(JNIEnv *jenv, jclass jcls) {
  jshort jresult = 0 ;
  short result;
  
  (void)jenv;
  (void)jcls;
  result = (short)close();
  jresult = (jshort)result; 
  return jresult;
}


JNIEXPORT jshort JNICALL Java_org_concord_sensor_labprousb_LabProUSBJNI_isOpen(JNIEnv *jenv, jclass jcls) {
  jshort jresult = 0 ;
  short result;
  
  (void)jenv;
  (void)jcls;
  result = (short)isOpen();
  jresult = (jshort)result; 
  return jresult;
}


JNIEXPORT jint JNICALL Java_org_concord_sensor_labprousb_LabProUSBJNI_getAvailableBytes(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  long result;
  
  (void)jenv;
  (void)jcls;
  result = (long)getAvailableBytes();
  jresult = (jint)result; 
  return jresult;
}


JNIEXPORT jint JNICALL Java_org_concord_sensor_labprousb_LabProUSBJNI_readBytes(JNIEnv *jenv, jclass jcls, jint jarg1, jbyteArray jarg2) {
  jint jresult = 0 ;
  long arg1 ;
  signed char *arg2 = (signed char *) 0 ;
  long result;
  jbyte *jarr2 ;
  
  (void)jenv;
  (void)jcls;
  arg1 = (long)jarg1; 
  if (!SWIG_JavaArrayInSchar(jenv, &jarr2, &arg2, jarg2)) return 0; 
  result = (long)readBytes(arg1,arg2);
  jresult = (jint)result; 
  SWIG_JavaArrayArgoutSchar(jenv, jarr2, arg2, jarg2); 
  free(arg2); 
  return jresult;
}


JNIEXPORT jshort JNICALL Java_org_concord_sensor_labprousb_LabProUSBJNI_writeBytes(JNIEnv *jenv, jclass jcls, jshort jarg1, jbyteArray jarg2) {
  jshort jresult = 0 ;
  short arg1 ;
  signed char *arg2 = (signed char *) 0 ;
  short result;
  jbyte *jarr2 ;
  
  (void)jenv;
  (void)jcls;
  arg1 = (short)jarg1; 
  if (!SWIG_JavaArrayInSchar(jenv, &jarr2, &arg2, jarg2)) return 0; 
  result = (short)writeBytes(arg1,arg2);
  jresult = (jshort)result; 
  SWIG_JavaArrayArgoutSchar(jenv, jarr2, arg2, jarg2); 
  free(arg2); 
  return jresult;
}


JNIEXPORT jshort JNICALL Java_org_concord_sensor_labprousb_LabProUSBJNI_clearInputs(JNIEnv *jenv, jclass jcls, jshort jarg1) {
  jshort jresult = 0 ;
  short arg1 ;
  short result;
  
  (void)jenv;
  (void)jcls;
  arg1 = (short)jarg1; 
  result = (short)clearInputs(arg1);
  jresult = (jshort)result; 
  return jresult;
}


JNIEXPORT jshort JNICALL Java_org_concord_sensor_labprousb_LabProUSBJNI_setNumChannelsAndModes(JNIEnv *jenv, jclass jcls, jint jarg1, jshort jarg2, jshort jarg3) {
  jshort jresult = 0 ;
  int arg1 ;
  short arg2 ;
  short arg3 ;
  short result;
  
  (void)jenv;
  (void)jcls;
  arg1 = (int)jarg1; 
  arg2 = (short)jarg2; 
  arg3 = (short)jarg3; 
  result = (short)setNumChannelsAndModes(arg1,arg2,arg3);
  jresult = (jshort)result; 
  return jresult;
}


#ifdef __cplusplus
}
#endif

