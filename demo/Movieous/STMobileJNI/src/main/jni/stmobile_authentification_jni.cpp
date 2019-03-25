#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <sys/time.h>
#include <time.h>
#include "st_mobile_license.h"
#include "utils.h"

#include<fcntl.h>

#define  LOG_TAG    "STMobileAuthentificationNative"

extern "C" {
JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_generateActiveCode(JNIEnv * env, jobject obj, jobject context, jstring licensePath);
JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_checkActiveCode(JNIEnv * env, jobject obj, jobject context, jstring licensePath, jstring activationCode, jint codeSize);
JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_generateActiveCodeFromBuffer(JNIEnv * env, jobject obj, jobject context, jstring licenseBuffer, jint licenseSize);
JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_checkActiveCodeFromBuffer(JNIEnv * env, jobject obj, jobject context, jstring licenseBuffer, jint licenseSize, jstring activationCode, jint codeSize);
JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_generateActiveCodeOnline(JNIEnv * env, jobject obj, jobject context, jstring licenseBuffer, jint licenseSize, jstring activationCode, jint codeSize);
JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_generateActiveCodeFromBufferOnline(JNIEnv * env, jobject obj, jobject context, jstring licenseBuffer, jint licenseSize);
};

JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_generateActiveCode(JNIEnv * env, jobject obj, jobject context, jstring licensePath) {
    LOGI("-->> 111generateActiveCode: start genrate active code");
//    const char *targetProductName = env->GetStringUTFChars(productName, 0);
    const char *targetLicensePath = env->GetStringUTFChars(licensePath, 0);
    char * activationCode = new char[1024];
    memset(activationCode, 0, 1024);
    int len = 1024;
    //	jint *len = (jint*) (env->GetPrimitiveArrayCritical(activeCodeLen, 0));
    LOGI("-->> targetLicensePath=%x, targetActivationCode=%x, activeCodeLen=%x", targetLicensePath, activationCode, len);
    int res = st_mobile_generate_activecode(env, context, targetLicensePath, activationCode, &len);
	LOGI("-->> targetLicensePath=%s, targetActivationCode=%s",targetLicensePath, activationCode);
    LOGI("-->> generateActiveCode: res=%d",res);
    jstring targetActivationCode = env->NewStringUTF(activationCode);

    env->ReleaseStringUTFChars(licensePath, targetLicensePath);
    delete[] activationCode;
    //	env->ReleasePrimitiveArrayCritical(activeCodeLen, len, 0);
    return targetActivationCode;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_checkActiveCode(JNIEnv * env, jobject obj, jobject context, jstring licensePath, jstring activationCode, jint codeSize) {
    if(codeSize>1023) {
        LOGE("checkActiveCode too long");
        return ST_JNI_ERROR_ACTIVE_CODE;
    }
    LOGI("-->> checkActiveCode: start check active code");
//    const char *targetProductName = env->GetStringUTFChars(productName, 0);
    const char *targetLicensePath = env->GetStringUTFChars(licensePath, 0);
    const char *targetActivationCode = env->GetStringUTFChars(activationCode, 0);
    //	LOGI("-->> targetProductName=%s, targetLicensePath=%s, targetActivationCode=%s",targetProductName, targetLicensePath, targetActivationCode);
    int res = st_mobile_check_activecode(env, context, targetLicensePath, targetActivationCode, codeSize);
    	LOGI("-->> checkActiveCode: res=%d",res);
    env->ReleaseStringUTFChars(licensePath, targetLicensePath);
    env->ReleaseStringUTFChars(activationCode, targetActivationCode);
    return res;
}

JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_generateActiveCodeFromBuffer(JNIEnv * env, jobject obj, jobject context, jstring licenseBuffer, jint licenseSize) {
    LOGI("-->> 222generateActiveCodeFromBuffer: start genrate active code");
    const char *targetLicenseBuffer = env->GetStringUTFChars(licenseBuffer, 0);
    char * activationCode = new char[1024];
    memset(activationCode, 0, 1024);
    int len = 1024;
    int res = st_mobile_generate_activecode_from_buffer(env, context, targetLicenseBuffer, licenseSize, activationCode, &len);
    LOGI("-->> targetLicenseBuffer=%s, license_size=%d, targetActivationCode=%s",targetLicenseBuffer, licenseSize, activationCode);
    LOGI("-->> generateActiveCode: res=%d",res);
    jstring targetActivationCode = env->NewStringUTF(activationCode);

    env->ReleaseStringUTFChars(licenseBuffer, targetLicenseBuffer);
    delete[] activationCode;
    return targetActivationCode;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_checkActiveCodeFromBuffer(JNIEnv * env, jobject obj, jobject context, jstring licenseBuffer, jint licenseSize, jstring activationCode, jint codeSize) {
    if(codeSize>1023) {
        LOGE("checkActiveCode too long");
       return ST_JNI_ERROR_ACTIVE_CODE;
    }

    LOGI("-->> checkActiveCodeFromBuffer: start check active code");
    const char *targetLicenseBuffer = env->GetStringUTFChars(licenseBuffer, 0);
    const char *targetActiveCode = env->GetStringUTFChars(activationCode, 0);

    char * activationCodeString = new char[1024];
    memset(activationCodeString, 0, 1024);
    memcpy(activationCodeString,targetActiveCode, codeSize);

//    int license_size = licenseSize;
    int res = st_mobile_check_activecode_from_buffer(env, context, targetLicenseBuffer, licenseSize, activationCodeString, codeSize);
    LOGI("-->> checkActiveCodeFromBuffer: res=%d",res);

    delete[] activationCodeString;
    env->ReleaseStringUTFChars(licenseBuffer, targetLicenseBuffer);
    env->ReleaseStringUTFChars(activationCode, targetActiveCode);
    return res;
}

JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_generateActiveCodeOnline(JNIEnv * env, jobject obj, jobject context, jstring licensePath) {
    LOGI("-->> 111generateActiveCode: start genrate active code");
//    const char *targetProductName = env->GetStringUTFChars(productName, 0);
    const char *targetLicensePath = env->GetStringUTFChars(licensePath, 0);
    char * activationCode = new char[1024];
    memset(activationCode, 0, 1024);
    int len = 1024;
    //	jint *len = (jint*) (env->GetPrimitiveArrayCritical(activeCodeLen, 0));
    LOGI("-->> targetLicensePath=%x, targetActivationCode=%x, activeCodeLen=%x", targetLicensePath, activationCode, len);
    int res = st_mobile_generate_activecode_online(env, context, targetLicensePath, activationCode, &len);
    LOGI("-->> targetLicensePath=%s, targetActivationCode=%s",targetLicensePath, activationCode);
    LOGI("-->> generateActiveCode: res=%d",res);
    jstring targetActivationCode = env->NewStringUTF(activationCode);

    env->ReleaseStringUTFChars(licensePath, targetLicensePath);
    delete[] activationCode;
    //	env->ReleasePrimitiveArrayCritical(activeCodeLen, len, 0);
    return targetActivationCode;
}

JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileAuthentificationNative_generateActiveCodeFromBufferOnline(JNIEnv * env, jobject obj, jobject context, jstring licenseBuffer, jint licenseSize) {
    LOGI("-->> 222generateActiveCodeFromBuffer: start genrate active code");
    const char *targetLicenseBuffer = env->GetStringUTFChars(licenseBuffer, 0);
    char * activationCode = new char[1024];
    memset(activationCode, 0, 1024);
    int len = 1024;
    int res = st_mobile_generate_activecode_from_buffer_online(env, context, targetLicenseBuffer, licenseSize, activationCode, &len);
    LOGE("-->> targetLicenseBuffer=%s, license_size=%d, targetActivationCode=%s",targetLicenseBuffer, licenseSize, activationCode);
    LOGE("-->> generateActiveCode: res=%d",res);
    jstring targetActivationCode = env->NewStringUTF(activationCode);

    env->ReleaseStringUTFChars(licenseBuffer, targetLicenseBuffer);
    delete[] activationCode;
    return targetActivationCode;
}
