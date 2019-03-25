#include <jni.h>
#include <android/log.h>
#include "utils.h"

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#define  LOG_TAG "STMobileFaceAttributeNative"

extern "C" {
JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFaceAttributeNative_createInstance(JNIEnv * env, jobject obj, jstring modelpath);
	JNIEXPORT int JNICALL Java_com_sensetime_stmobile_STMobileFaceAttributeNative_detect(JNIEnv * env, jobject obj,
	    jbyteArray pInputImage, jint imageFormat, jint imageWidth, jint imageHeight, jobjectArray face106Array, jobjectArray arrAttributes);
	JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileFaceAttributeNative_destroyInstance(JNIEnv * env, jobject obj);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFaceAttributeNative_createInstanceFromAssetFile(JNIEnv * env, jobject obj, jstring model_path, jobject assetManager);
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFaceAttributeNative_createInstance(JNIEnv * env, jobject obj, jstring modelpath) {
    st_handle_t  ha_handle = NULL;
	if (modelpath == NULL) {
	    LOGE("model path is null");
	    return ST_E_INVALIDARG;
	}
    const char *modelpathChars = env->GetStringUTFChars(modelpath, 0);
    LOGI("-->> modelpath=%s", modelpathChars);
    int result = st_mobile_face_attribute_create(modelpathChars, &ha_handle);
    if(result != 0) {
        LOGE("create handle for face attribute failed");
        env->ReleaseStringUTFChars(modelpath, modelpathChars);
        return result;
    }
    setHandle(env, obj, ha_handle);
    env->ReleaseStringUTFChars(modelpath, modelpathChars);
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileFaceAttributeNative_createInstanceFromAssetFile(JNIEnv * env, jobject obj, jstring model_path, jobject assetManager){
    st_handle_t handle = NULL;
    if(NULL == model_path){
        LOGE("model_file_name is null, create handle with null model");
        return ST_JNI_ERROR_INVALIDARG;
    }

    if(NULL == assetManager){
        LOGE("assetManager is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    const char* model_file_name_str = env->GetStringUTFChars(model_path, 0);
    if(NULL == model_file_name_str) {
        LOGE("change model_file_name to c_str failed");
        return ST_JNI_ERROR_INVALIDARG;
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(NULL == mgr) {
        LOGE("native assetManager is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    AAsset* asset = AAssetManager_open(mgr, model_file_name_str, AASSET_MODE_UNKNOWN);
    env->ReleaseStringUTFChars(model_path, model_file_name_str);
    if (NULL == asset) {
        LOGE("open asset file failed");
        return ST_JNI_ERROR_FILE_OPEN_FIALED;
    }

    unsigned char* buffer = NULL;
    long size = 0;
    size = AAsset_getLength(asset);
    buffer = new unsigned char[size];
    memset(buffer, '\0', size);

    long readSize = AAsset_read(asset, buffer, size);
    if (readSize != size) {
        AAsset_close(asset);
        if(buffer){
            delete[] buffer;
            buffer = NULL;
        }
        return ST_JNI_ERROR_FILE_SIZE;
    }

    AAsset_close(asset);

    if (size < 1000) {
        LOGE("Model file is too short");
        if (buffer) {
            delete[] buffer;
            buffer = NULL;
        }
        return ST_JNI_ERROR_FILE_SIZE;
    }

    int result = st_mobile_face_attribute_create_from_buffer(buffer, size, &handle);
    if(buffer){
        delete[] buffer;
        buffer = NULL;
    }

    if(result != 0){
        LOGE("create handle failed, %d",result);
        return result;
    }

    setHandle(env, obj, handle);
    return result;
}

JNIEXPORT int JNICALL Java_com_sensetime_stmobile_STMobileFaceAttributeNative_detect(JNIEnv * env, jobject obj,
    jbyteArray pInputImage, jint imageFormat, jint imageWidth, jint imageHeight, jobjectArray face106Array, jobjectArray arrAttributes){
    LOGI("faceAttribute, the width is %d, the height is %d",imageWidth, imageHeight);
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
    if(handle == NULL)
    {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if (pInputImage == NULL) {
        LOGE("input image is null");
        return ST_E_INVALIDARG;
    }

    if (face106Array == NULL) {
        LOGE("face information is null");
        return ST_E_INVALIDARG;
    }

    jbyte *srcdata = (jbyte*) (env->GetByteArrayElements(pInputImage, 0));
    int image_stride = getImageStride((st_pixel_format)imageFormat, imageWidth);

    st_mobile_106_t *p_mobile_106 = NULL;

    int len = env->GetArrayLength(face106Array);
    if (len > 0) {
        p_mobile_106 = new st_mobile_106_t[len];
        for (int i = 0; i < len; ++i) {
            jobject face106 = env->GetObjectArrayElement(face106Array, i);
            if (!convert2mobile_106(env, face106, p_mobile_106[i])) {
                   memset(&p_mobile_106[i], 0, sizeof(st_mobile_106_t));
               }
            env->DeleteLocalRef(face106);
        }
    }
    LOGE("before detect： format: %d, %x, w: %d, h: %d,  stride: %d, %x, len: %d", imageFormat, srcdata, imageWidth, imageHeight, image_stride, p_mobile_106, len);
    st_mobile_attributes_t *pAttrbutes = NULL;
    int result = st_mobile_face_attribute_detect(handle, (unsigned char *)srcdata, (st_pixel_format)imageFormat, imageWidth, imageHeight, image_stride, p_mobile_106, len, &pAttrbutes);
    env->ReleaseByteArrayElements(pInputImage, srcdata, 0);
    LOGE("result detect : %d", result);

    if (result == ST_OK) {
        if (arrAttributes == NULL) {
            LOGE("face attribute array is null, please allocate it on java");
        } else {
            for (int i = 0; i < len; ++i) {
                jobject attrObj = convert2FaceAttribute(env, &pAttrbutes[i]);
                if (attrObj != NULL) {
                    env->SetObjectArrayElement(arrAttributes, i, attrObj);
                }
                env->DeleteLocalRef(attrObj);
            }
        }
    }

    if (p_mobile_106) {
        delete[] p_mobile_106;
        p_mobile_106 = NULL;
    }

    return result;
}


JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileFaceAttributeNative_destroyInstance(JNIEnv * env, jobject obj)
{
    st_handle_t handle = getHandle<st_handle_t>(env, obj);
	if(handle != NULL)
	{
	    LOGI(" face attribute destory");
	    setHandle<st_handle_t>(env, obj, NULL);
	    st_mobile_face_attribute_destroy(handle);
	}
}