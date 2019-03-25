#include "stmobile_sound_play_jni.h"
#include "utils.h"
#include "jvmutil.h"

#define  LOG_TAG    "STMobileSticker"

const char *kSoundPlayPath = "com/sensetime/stmobile/STSoundPlay";

void soundLoad(void* handle, void* sound, const char* sound_name, int length)
{
    JNIEnv *env;
    bool isAttached = false;
    getEnv(&env,&isAttached);
    if(!env) {
        return;
    }

    LOGE("soundLoad");

    jclass soundPlayCls = env->FindClass(kSoundPlayPath);
    if(!soundPlayCls) {
        LOGE("Failed to get %s class", kSoundPlayPath);
        return;
    }
    jobject soundPlayObject = getSoundPlayObjInSticker(env);
    if(!soundPlayObject){
        return;
    }

    jmethodID method = env->GetMethodID(soundPlayCls, "onSoundLoaded", "(Ljava/lang/String;[B)V");
    if(!method) {
        LOGE("Failed to get method ID onSoundLoaded");
        return;
    }

    jstring soundName = stoJstring(env, (char*)sound_name);
    jbyteArray soundBytes = env->NewByteArray(length);
    env->SetByteArrayRegion(soundBytes, 0, length, (jbyte *)sound);
    env->CallVoidMethod(soundPlayObject, method, soundName, soundBytes);

    env->DeleteLocalRef(soundBytes);
    env->DeleteLocalRef(soundPlayCls);
    env->DeleteLocalRef(soundPlayObject);

    if (isAttached) {
        gJavaVM->DetachCurrentThread();
    }
}

void soundPlay(void* handle, const char* sound_name, int loop)
{
    JNIEnv *env;
    bool isAttached = false;
    getEnv(&env,&isAttached);
    if(!env) {
        return;
    }

    LOGE("soundPlay");

    jclass soundPlayCls = env->FindClass(kSoundPlayPath);
    if(!soundPlayCls) {
        LOGE("Failed to get %s class", kSoundPlayPath);
        return;
    }

    jobject soundPlayObject = getSoundPlayObjInSticker(env);
    if(!soundPlayObject) {
        return;
    }

    jmethodID method = env->GetMethodID(soundPlayCls, "onStartPlay", "(Ljava/lang/String;I)V");
    if(!method) {
        LOGE("Failed to get method ID onStartPlay");
        return;
    }

    jstring soundName = stoJstring(env, (char*)sound_name);
    env->CallVoidMethod(soundPlayObject, method, soundName, loop);

    env->DeleteLocalRef(soundPlayCls);
    env->DeleteLocalRef(soundPlayObject);

    if (isAttached) {
        gJavaVM->DetachCurrentThread();
    }
}

void soundStop(void* handle, const char* sound_name)
{
    JNIEnv *env;
    bool isAttached = false;
    getEnv(&env,&isAttached);
    if(!env) {
        return;
    }

    jclass soundPlayCls = env->FindClass(kSoundPlayPath);
    if(!soundPlayCls) {
        LOGE("Failed to get %s class", kSoundPlayPath);
        return;
    }

    LOGE("soundStop");

    jobject soundPlayObject = getSoundPlayObjInSticker(env);
    if(!soundPlayObject){
        return;
    }

    jmethodID method = env->GetMethodID(soundPlayCls, "onStopPlay", "(Ljava/lang/String;)V");
    if(!method) {
        LOGE("Failed to get method ID onStopPlay");
        return;
    }

    jstring soundName = stoJstring(env, (char*)sound_name);
    env->CallVoidMethod(soundPlayObject, method, soundName);

    env->DeleteLocalRef(soundPlayCls);
    env->DeleteLocalRef(soundPlayObject);

    if (isAttached) {
        gJavaVM->DetachCurrentThread();
    }
}

void soundPause(void* handle, const char* sound_name)
{
    JNIEnv *env;
    bool isAttached = false;
    getEnv(&env,&isAttached);
    if(!env) {
        return;
    }

    jclass soundPlayCls = env->FindClass(kSoundPlayPath);
    if(!soundPlayCls) {
        LOGE("Failed to get %s class", kSoundPlayPath);
        return;
    }

    LOGE("soundPause");

    jobject soundPlayObject = getSoundPlayObjInSticker(env);
    if(!soundPlayObject){
        return;
    }

    jmethodID method = env->GetMethodID(soundPlayCls, "onSoundPause", "(Ljava/lang/String;)V");
    if(!method) {
        LOGE("Failed to get method ID onSoundPause");
        return;
    }

    jstring soundName = stoJstring(env, (char*)sound_name);
    env->CallVoidMethod(soundPlayObject, method, soundName);

    env->DeleteLocalRef(soundPlayCls);
    env->DeleteLocalRef(soundPlayObject);

    if (isAttached) {
        gJavaVM->DetachCurrentThread();
    }
}

void soundResume(void* handle, const char* sound_name)
{
    JNIEnv *env;
    bool isAttached = false;
    getEnv(&env,&isAttached);
    if(!env) {
        return;
    }

    jclass soundPlayCls = env->FindClass(kSoundPlayPath);
    if(!soundPlayCls) {
        LOGE("Failed to get %s class", kSoundPlayPath);
        return;
    }

    LOGE("soundResume");

    jobject soundPlayObject = getSoundPlayObjInSticker(env);
    if(!soundPlayObject){
        return;
    }

    jmethodID method = env->GetMethodID(soundPlayCls, "onSoundResume", "(Ljava/lang/String;)V");
    if(!method) {
        LOGE("Failed to get method ID onSoundResume");
        return;
    }

    jstring soundName = stoJstring(env, (char*)sound_name);
    env->CallVoidMethod(soundPlayObject, method, soundName);

    env->DeleteLocalRef(soundPlayCls);
    env->DeleteLocalRef(soundPlayObject);

    if (isAttached) {
        gJavaVM->DetachCurrentThread();
    }
}

