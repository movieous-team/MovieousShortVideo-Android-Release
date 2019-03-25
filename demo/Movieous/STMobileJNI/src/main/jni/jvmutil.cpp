#include "jvmutil.h"
#include "utils.h"
#include <string.h>

#define  LOG_TAG    "STMobileSticker"

JavaVM *gJavaVM = NULL;
jobject gStickerObject = NULL;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env;
    gJavaVM = vm;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("JNI OnLoad Failed to get the environment using GetEnv()");
        return -1;
    }
    return JNI_VERSION_1_4;
}

jobject getSoundPlayObjInSticker(JNIEnv* env)
{
    const char *kStickerPath = "com/sensetime/stmobile/STMobileStickerNative";
    jclass cls = env->FindClass(kStickerPath);
    if(!cls) {
        LOGE("JNI OnLoad: failed to get %s class reference", kStickerPath);
        return NULL;
    }
    jfieldID fieldId = env->GetFieldID(cls, "mSoundPlay", "Lcom/sensetime/stmobile/STSoundPlay;");
    jobject obj = env->GetObjectField(gStickerObject, fieldId);

    env->DeleteLocalRef(cls);
    return obj;
}

void getEnv(JNIEnv** env, bool* isAttached)
{
    if(!gJavaVM)
        return;

    *isAttached = false;
    int status = gJavaVM->GetEnv((void **) env, JNI_VERSION_1_4);
    if (status != JNI_OK) {
        status = gJavaVM->AttachCurrentThread(env, NULL);

        if (status < 0) {
            LOGE("Failed to get the environment using GetEnv(), %d", status);
            return;
        }
        *isAttached = true;
    }
}

jstring stoJstring(JNIEnv* env, const char* pat)
{
    jclass strClass = env->FindClass("java/lang/String");

    jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");

    jbyteArray bytes = env->NewByteArray(strlen(pat));
    env->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
    jstring encoding = env->NewStringUTF("utf-8");

    jstring strResult = (jstring)env->NewObject(strClass, ctorID, bytes, encoding);
    env->DeleteLocalRef(strClass);
    env->DeleteLocalRef(bytes);
    env->DeleteLocalRef(encoding);
    return strResult;
}
