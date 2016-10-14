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
  * @desc connect to node with specific ip addr and send interest object
  * @param string $ipString - ip addr,integer-$portString-port,string -$contentString- name of interest object
  * @return String - content Object
*/

/**
 * androidMkc
 * calls the ccnl_android_mkc function from ccnl-lite-android-mkc.c
 * returns the sucess or failure message string
 */


JNIEXPORT jstring JNICALL
Java_ch_unibas_ccn_1lite_1android_PreferencesFragment_androidMkc(JNIEnv* env, jobject thiz, jstring suiteString,
                                                                 jstring ipString, jint portInt,
                                                                 jstring uriString, jstring bodyString)
{

    const char *suite = (*env)->GetStringUTFChars(env, suiteString, 0);
    const char *addr = (*env)->GetStringUTFChars(env, ipString, 0);
    int port = (int) portInt;
    const char *uri = (*env)->GetStringUTFChars(env, uriString, 0);
    const char *body = (*env)->GetStringUTFChars(env, bodyString, 0);

    return (*env)->NewStringUTF(env, ccnl_android_mkC(suite, addr, port, uri, body));
}



/**
 * androidPeek
 * calls the ccnl_android_peek function from ccnl-lite-android-peek.c
 * returns the string returned from that function
 */
JNIEXPORT jstring JNICALL
Java_ch_unibas_ccn_1lite_1android_CcnLiteAndroid_androidPeek(JNIEnv* env,
                                                                jobject thiz, jstring ipString, jint portString, jstring contentString)
{
    char buf[128];
    const char *ip = (*env)->GetStringUTFChars(env, ipString, 0);
    int port = (int) portString;
    const char *content = (*env)->GetStringUTFChars(env, contentString, 0);
    return (*env)->NewStringUTF(env, ccnl_android_peek("ccnx2015", ip, port, content));
}

/**
 * androidPeek
 * calls the ccnl_android_peek function from ccnl-lite-android-peek.c
 * returns the string returned from that function
 */
JNIEXPORT jstring JNICALL
Java_ch_unibas_ccn_1lite_1android_RelayService_androidPeek(JNIEnv* env,
                                                                jobject thiz, jstring ipString, jint portString, jstring contentString)
{
    char buf[128];
    const char *ip = (*env)->GetStringUTFChars(env, ipString, 0);
    int port = (int) portString;
    const char *content = (*env)->GetStringUTFChars(env, contentString, 0);
    return (*env)->NewStringUTF(env, ccnl_android_peek("ccnx2015", ip, port, content));
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


