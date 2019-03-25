#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "st_mobile_filter.h"
#include "utils.h"

#define  LOG_TAG    "STMobileStreamFilterNative"

extern "C" {
    JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_createInstance(JNIEnv * env, jobject obj);
    JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_setStyle(JNIEnv * env, jobject obj, jstring stylepath);
    JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_setParam(JNIEnv * env, jobject obj, jint type,jfloat value);
    JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_processTexture(JNIEnv * env, jobject obj, jint textureIn,
        jint imageWidth,jint imageHeight,jint textureOut);
    JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_processTextureAndOutputBuffer(JNIEnv * env, jobject obj, jint textureIn,
        jint imageWidth,jint imageHeight,jint textureOut,jbyteArray pOutputImage, jint outformat);
    JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_processBuffer(JNIEnv * env, jobject obj, jbyteArray pInputImage,
        jint informat,jint imageWidth,jint imageHeight,jbyteArray pOutputImage, jint outformat);
    JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_processPicture(JNIEnv * env, jobject obj, jbyteArray pInputImage,
        jint informat,jint imageWidth,jint imageHeight,jbyteArray pOutputImage, jint outformat);
    JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_destroyInstance(JNIEnv * env, jobject obj);
};

JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_createInstance(JNIEnv * env, jobject obj)
{
    st_handle_t handle;
    int result = (int)st_mobile_gl_filter_create(&handle);
    if(result != 0)
    {
        LOGE("create handle failed");
        return result;
    }
    setHandle(env, obj, handle);
    return result;
}

JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_setStyle(JNIEnv * env, jobject obj, jstring stylepath)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    const char *pathChars = NULL;
    if (stylepath != NULL) {
        pathChars = env->GetStringUTFChars(stylepath, 0);
    }
     if(handle != NULL)
     {
         result = st_mobile_gl_filter_set_style(handle,pathChars);
     }
     if(stylepath != NULL){
         env->ReleaseStringUTFChars(stylepath, pathChars);
     }

     return result;
}

JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_setParam(JNIEnv * env, jobject obj, jint type,jfloat value)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    if(handle != NULL)
    {
        result = st_mobile_gl_filter_set_param(handle,(st_gl_filter_type)type,value);
    }
    return result;
}

JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_processTexture(JNIEnv * env, jobject obj, jint textureIn,
        jint imageWidth,jint imageHeight,jint textureOut)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    if(handle != NULL)
    {
        result = st_mobile_gl_filter_process_texture(handle,textureIn,imageWidth,imageHeight,textureOut);
    }
    return result;
}

JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_processTextureAndOutputBuffer(JNIEnv * env, jobject obj, jint textureIn,
        jint imageWidth,jint imageHeight,jint textureOut,jbyteArray pOutputImage, jint outformat)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    jbyte *dstdata = (jbyte*) env->GetPrimitiveArrayCritical(pOutputImage, 0);


    if(handle != NULL)
    {
        result = st_mobile_gl_filter_process_texture_and_output_buffer(handle,textureIn,imageWidth,imageHeight,
            textureOut,(unsigned char *)dstdata,(st_pixel_format)outformat);
    }
     env->ReleasePrimitiveArrayCritical(pOutputImage, dstdata, 0);
     return result;
}

JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_processBuffer(JNIEnv * env, jobject obj, jbyteArray pInputImage,
        jint informat,jint imageWidth,jint imageHeight,jbyteArray pOutputImage, jint outformat)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    jbyte *srcdata = (jbyte*) (env->GetPrimitiveArrayCritical(pInputImage, 0));
    jbyte *dstdata = (jbyte*) env->GetPrimitiveArrayCritical(pOutputImage, 0);
    st_pixel_format pixel_format = (st_pixel_format)informat;
    int stride = getImageStride(pixel_format, imageWidth);
    if(handle != NULL)
    {
        result = st_mobile_gl_filter_process_buffer(handle,(unsigned char *)srcdata,pixel_format,imageWidth,imageHeight,stride,
            (unsigned char *)dstdata,(st_pixel_format)outformat);
    }
     env->ReleasePrimitiveArrayCritical(pInputImage, srcdata, 0);
     env->ReleasePrimitiveArrayCritical(pOutputImage, dstdata, 0);

    return result;
}

/*******************************
JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_processPicture(JNIEnv * env, jobject obj, jbyteArray pInputImage,
        jint informat,jint imageWidth,jint imageHeight,jbyteArray pOutputImage, jint outformat)
{
    int result = ST_E_INVALIDARG;
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    jbyte *srcdata = (jbyte*) (env->GetPrimitiveArrayCritical(pInputImage, 0));
    jbyte *dstdata = (jbyte*) env->GetPrimitiveArrayCritical(pOutputImage, 0);
    st_pixel_format pixel_format = (st_pixel_format)informat;
    int stride = getImageStride(pixel_format, imageWidth);
    if(handle != NULL)
    {
        result = st_mobile_gl_filter_process_buffer(handle,(unsigned char *)srcdata,pixel_format,imageWidth,imageHeight,stride,
            (unsigned char *)dstdata,(st_pixel_format)outformat);
    }
     env->ReleasePrimitiveArrayCritical(pInputImage, srcdata, 0);
     env->ReleasePrimitiveArrayCritical(pOutputImage, dstdata, 0);

    return result;
}********************************/


JNIEXPORT jint Java_com_sensetime_stmobile_STMobileStreamFilterNative_destroyInstance(JNIEnv * env, jobject obj)
{
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    if(handle == NULL)
    {
        return ST_E_HANDLE;
    }
    setHandle<st_handle_t>(env, obj, NULL);
    st_mobile_gl_filter_destroy(handle);
    return ST_OK;
}
