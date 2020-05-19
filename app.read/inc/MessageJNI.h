/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class MessageJNI */

#ifndef _Included_MessageJNI
#define _Included_MessageJNI
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     MessageJNI
 * Method:    messageSend
 * Signature: (LMessage;I)I
 */
JNIEXPORT jint JNICALL Java_MessageJNI_messageSend
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     MessageJNI
 * Method:    messageInit
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_MessageJNI_messageInit
  (JNIEnv *, jclass, jstring, jstring, jstring);

/*
 * Class:     MessageJNI
 * Method:    messageReceive
 * Signature: (LMessage;)I
 */
JNIEXPORT jint JNICALL Java_MessageJNI_messageReceive
  (JNIEnv *, jclass, jobject);

/*
 * Class:     MessageJNI
 * Method:    messageSubscribe
 * Signature: (SLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_MessageJNI_messageSubscribe
  (JNIEnv *, jclass, jshort, jstring);

/*
 * Class:     MessageJNI
 * Method:    messageUnSubscribe
 * Signature: (SLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_MessageJNI_messageUnSubscribe
  (JNIEnv *, jclass, jshort, jstring);

/*
 * Class:     MessageJNI
 * Method:    messageExit
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_MessageJNI_messageExit
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
