#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "st_mobile_filter.h"
#include "utils.h"
#include<fcntl.h>
#define  LOG_TAG    "STMobileFilterNative"

extern "C" {
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_createInstance(JNIEnv * env, jobject obj);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_setStyle(JNIEnv * env, jobject obj, jstring stylepath);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_setParam(JNIEnv * env, jobject obj, jint type,jfloat value);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_process(JNIEnv * env, jobject obj, jbyteArray pInputImage,
        jint informat, jint imageWidth, jint imageHeight, jbyteArray pOutputImage, jint outformat);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_destroyInstance(JNIEnv * env, jobject obj);
};

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_createInstance(JNIEnv * env, jobject obj)
{
    st_handle_t handle;
    int result = (int)st_mobile_filter_create(&handle);
    if(result != 0)
    {
        LOGE("create handle failed");
        return result;
    }
    setHandle(env, obj, handle);
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_setStyle(JNIEnv * env, jobject obj, jstring stylepath)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    const char *pathChars = NULL;
    if (stylepath != NULL) {
        pathChars = env->GetStringUTFChars(stylepath, 0);
    }
     if(handle != NULL)
     {
         result = st_mobile_filter_set_style(handle,pathChars);
     }
     if(stylepath != NULL){
     env->ReleaseStringUTFChars(stylepath, pathChars);
     }

     return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_setParam(JNIEnv * env, jobject obj, jint type,jfloat value)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    if(handle != NULL)
    {
        result = st_mobile_filter_set_param(handle,(st_filter_type)type,value);
    }
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_process(JNIEnv * env, jobject obj, jbyteArray pInputImage,
        jint informat, jint imageWidth, jint imageHeight,jbyteArray pOutputImage, jint outformat)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    jbyte *srcdata = (jbyte*) (env->GetPrimitiveArrayCritical(pInputImage, 0));
    jbyte *dstdata = (jbyte*) env->GetPrimitiveArrayCritical(pOutputImage, 0);
    st_pixel_format pixel_format = (st_pixel_format)informat;
    int stride = getImageStride(pixel_format, imageWidth);
    if(handle != NULL)
    {
        result = st_mobile_filter_process(handle,(unsigned char *)srcdata,pixel_format,imageWidth,imageHeight,stride,
            (unsigned char *)dstdata,(st_pixel_format)outformat);
    }
     env->ReleasePrimitiveArrayCritical(pInputImage, srcdata, 0);
     env->ReleasePrimitiveArrayCritical(pOutputImage, dstdata, 0);

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFilterNative_destroyInstance(JNIEnv * env, jobject obj)
{
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    if(handle == NULL)
    {
        return ST_E_HANDLE;
    }
    setHandle<st_handle_t>(env, obj, NULL);
    st_mobile_filter_destroy(handle);
    return ST_OK;
}
