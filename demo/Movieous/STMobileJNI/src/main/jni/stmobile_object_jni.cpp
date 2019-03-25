#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "st_mobile_object.h"
#include "utils.h"
#include<fcntl.h>
#define  LOG_TAG    "STMobileObjectNative"

extern "C" {
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_createInstance(JNIEnv * env, jobject obj);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_setTarget(JNIEnv * env, jobject obj, jbyteArray pInputImage,
              jint informat, jint imageWidth, jint imageHeight, jobject inputRect);
    JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_objectTrack(JNIEnv * env, jobject obj, jbyteArray pInputImage,
            jint informat, jint imageWidth, jint imageHeight, jfloatArray resultScore);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_reset(JNIEnv * env, jobject obj);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_destroyInstance(JNIEnv * env, jobject obj);
};

static inline jfieldID getObjectHandleField(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    // J is the type signature for long:
    jfieldID fieldID = env->GetFieldID(c, "objectTrackNativeHandle", "J");
    env->DeleteLocalRef(c);
    return fieldID;
}

void setObjectHandle(JNIEnv *env, jobject obj, void * h)
{
    jlong handle = reinterpret_cast<jlong>(h);
    env->SetLongField(obj, getObjectHandleField(env, obj), handle);
}

void* getObjectHandle(JNIEnv *env, jobject obj)
{
    jlong handle = env->GetLongField(obj, getObjectHandleField(env, obj));
    return reinterpret_cast<void *>(handle);
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_createInstance(JNIEnv * env, jobject obj)
{
    st_handle_t handle;
    int result = (int)st_mobile_object_tracker_create(&handle);
    if(result != 0)
    {
        LOGE("create handle failed");
        return result;
    }
    setObjectHandle(env, obj, handle);
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_setTarget(JNIEnv * env, jobject obj, jbyteArray pInputImage,
          jint informat, jint imageWidth, jint imageHeight, jobject inputRect)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getObjectHandle(env, obj);
    if(handle == NULL){
       LOGE("object handle is null");
       return ST_E_HANDLE;
    }

    st_rect_t rect = {0};
    if (!convert2st_rect_t(env, inputRect, rect)) {
       memset(&rect, 0, sizeof(st_rect_t));
    }

    jbyte *srcdata = (jbyte*) (env->GetPrimitiveArrayCritical(pInputImage, 0));
    st_pixel_format pixel_format = (st_pixel_format)informat;

    int stride = getImageStride(pixel_format, imageWidth);
    if(handle != NULL)
    {
       result = st_mobile_object_tracker_set_target(handle,(unsigned char *)srcdata,pixel_format,imageWidth,imageHeight,stride, &rect);
    }
    env->ReleasePrimitiveArrayCritical(pInputImage, srcdata, 0);

    return result;
}

JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_objectTrack(JNIEnv * env, jobject obj, jbyteArray pInputImage,
        jint informat, jint imageWidth, jint imageHeight, jfloatArray resultScore)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t handle = getObjectHandle(env, obj);
    if(handle == NULL){
       LOGE("object handle is null");
       return (jobject)ST_E_HANDLE;
    }

    jbyte *srcdata = (jbyte*) (env->GetPrimitiveArrayCritical(pInputImage, 0));
    st_pixel_format pixel_format = (st_pixel_format)informat;
    st_rect_t out_rect;
    int stride = getImageStride(pixel_format, imageWidth);
    float score = 0.0;
    if(handle != NULL)
    {
        result = st_mobile_object_tracker_track(handle,(unsigned char *)srcdata,pixel_format,imageWidth,imageHeight,stride, &out_rect, &score);
    }
    env->ReleasePrimitiveArrayCritical(pInputImage, srcdata, 0);

    jobject rectObject = NULL;
    if(result == ST_OK){
        rectObject = convert2STRect(env, out_rect);
        jfloat scores[1];
        scores[0] = score;
        //env->SetFloatArrayElement(resultScore, 0, score);
        env->SetFloatArrayRegion(resultScore, 0, 1, scores);
        LOGE("object ret: %f",score);
    }

    return rectObject;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_reset(JNIEnv * env, jobject obj)
{
    st_handle_t handle = getObjectHandle(env, obj);

    if(handle == NULL){
       LOGE("object handle is null");
       return ST_E_HANDLE;
    }

    st_mobile_object_tracker_reset(handle);
    return ST_OK;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileObjectTrackNative_destroyInstance(JNIEnv * env, jobject obj)
{
    st_handle_t handle = getObjectHandle(env, obj);
    if(handle == NULL)
    {
        return ST_E_HANDLE;
    }
    setObjectHandle(env, obj, NULL);
    st_mobile_object_tracker_destroy(handle);
    return ST_OK;
}