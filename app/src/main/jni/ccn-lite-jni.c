// src/android/jni/ccn-lite-jni.c

#include <string.h>
#include <jni.h>
#include <android/log.h>

int jni_bleSend(unsigned char *data, int len);

#include "../../ccn-lite-android.c"

static JavaVM *jvm;
static jclass ccnLiteClass;
static jobject ccnLiteObject;

/*Init Relay*/

JNIEXPORT jstring JNICALL
Java_ch_unibas_ccn_1lite_1android_CcnLiteAndroid_relayInit(JNIEnv* env,
                                                            jobject thiz)
{
    char *hello;

    (*env)->GetJavaVM(env, &jvm);

    if (ccnLiteClass == NULL) {
        jclass localRefCls = (*env)->FindClass(env,
                             "ch/unibas/ccn_lite_android/CcnLiteAndroid");
        if (localRefCls != NULL)
            ccnLiteClass = (*env)->NewGlobalRef(env, localRefCls);
        (*env)->DeleteLocalRef(env, localRefCls);
    }
    if (ccnLiteObject == NULL)
        ccnLiteObject = (*env)->NewGlobalRef(env, thiz);

    hello = ccnl_android_init();
    return (*env)->NewStringUTF(env, hello);
}



/**
 * androidPeek
 * calls the ccnl_android_peek function from ccnl-lite-android-peek.c
 * returns the string returned from that function
 */
JNIEXPORT jstring JNICALL
Java_ch_unibas_ccn_1lite_1android_CcnLiteAndroid_androidPeek(JNIEnv* env,
                                jobject thiz, jstring suiteString, jstring ipString, jint portString, jstring contentString)
{
    char buf[128];
    const char *ip = (*env)->GetStringUTFChars(env, ipString, 0);
    const char *suite = (*env)->GetStringUTFChars(env, suiteString, 0);
    int port = (int) portString;
    const char *content = (*env)->GetStringUTFChars(env, contentString, 0);
    return (*env)->NewStringUTF(env, ccnl_android_peek(suite, ip, port, content));
}




void jni_append_to_log(char *line)
{
    JNIEnv *env;
    int len = strlen(line);

    if (len > 0 && line[len - 1] == '\n')
        line[len - 1] = '\0';

    (*jvm)->GetEnv(jvm, (void**)&env, JNI_VERSION_1_4);
    jmethodID method = (*env)->GetMethodID(env, ccnLiteClass,
                                           "appendToLog",
                                           "(Ljava/lang/String;)V");
    (*env)->CallVoidMethod(env, ccnLiteObject, method,
                           (*env)->NewStringUTF(env, line));
}


