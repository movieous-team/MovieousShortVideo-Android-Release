#include <jni.h>

#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include "prebuilt/include/st_mobile_common.h"
#include "prebuilt/include/st_mobile_sticker.h"
#include "utils.h"

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include<fcntl.h>

#define  LOG_TAG    "STMobileHumanAction"

extern "C" {
	JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_createInstance(JNIEnv * env, jobject obj, jstring modelpath, jint config);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_createInstanceFromAssetFile(JNIEnv * env, jobject obj, jstring model_path, jint config, jobject assetManager);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_createInstanceWithSubModels(JNIEnv * env, jobject obj, jobjectArray modelPaths, jint modelCount, jint config);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_addSubModel(JNIEnv * env, jobject obj, jstring modelpath);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_addSubModelFromAssetFile(JNIEnv * env, jobject obj, jstring model_path, jobject assetManager);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_removeSubModelByConfig(JNIEnv * env, jobject obj, jint config);
	JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_humanActionDetect(JNIEnv * env, jobject obj,
	    jbyteArray pInputImage, jint imageFormat, jlong detect_config, jint rotate, jint imageWidth, jint imageHeight);
	JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_reset(JNIEnv * env, jobject obj);
	JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_destroyInstance(JNIEnv * env, jobject obj);
	JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_setParam(JNIEnv * env, jobject obj, jint type, jfloat value);
    JNIEXPORT jbooleanArray JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_getExpression(JNIEnv * env, jobject obj, jobject humanAction, jint orientation, jboolean needMirror);
    JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_setExpressionThreshold(JNIEnv * env, jobject obj, jint detectExpression, jfloat threshold);

    JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_model_STHumanAction_humanActionMirror(JNIEnv * env, jobject obj, jint width, jobject humanAction);
    JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_model_STHumanAction_humanActionRotate(JNIEnv * env, jobject obj, jint width, jint height, jint orientation, jboolean rotateBackground, jobject humanAction);
    JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_model_STHumanAction_humanActionResize(JNIEnv * env, jobject obj, jfloat scale, jobject humanAction);
};

static inline jfieldID getHumanActionHandleField(JNIEnv *env, jobject obj)
{
	jclass c = env->GetObjectClass(obj);
	// J is the type signature for long:
	return env->GetFieldID(c, "nativeHumanActionHandle", "J");
}

void setHumanActionHandle(JNIEnv *env, jobject obj, void * h)
{
	jlong handle = reinterpret_cast<jlong>(h);
	env->SetLongField(obj, getHumanActionHandleField(env, obj), handle);
}

void* getHumanActionHandle(JNIEnv *env, jobject obj)
{
	jlong handle = env->GetLongField(obj, getHumanActionHandleField(env, obj));
	return reinterpret_cast<void *>(handle);
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_createInstance(JNIEnv * env, jobject obj, jstring modelpath, jint config)
{
	st_handle_t  ha_handle = NULL;
	if (modelpath == NULL) {
	    LOGE("model path is null");
	    return ST_JNI_ERROR_INVALIDARG;
	}
    const char *modelpathChars = env->GetStringUTFChars(modelpath, 0);
    LOGI("-->> modelpath=%s, config=%d", modelpathChars, config);
    int result = st_mobile_human_action_create(modelpathChars, config, &ha_handle);
    if(result != 0){
        LOGE("create handle for human action failed");
        env->ReleaseStringUTFChars(modelpath, modelpathChars);
        return result;
    }
    setHumanActionHandle(env, obj, ha_handle);
    env->ReleaseStringUTFChars(modelpath, modelpathChars);
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_createInstanceFromAssetFile(JNIEnv * env, jobject obj, jstring model_path, jint config, jobject assetManager){
    st_handle_t handle = NULL;
    if(NULL == model_path){
        LOGE("model_path is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    if(NULL == assetManager){
        LOGE("assetManager is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    const char* model_file_name_str = env->GetStringUTFChars(model_path, 0);
    if(NULL == model_file_name_str){
        LOGE("change model_path to c_str failed");
        return ST_JNI_ERROR_INVALIDARG;
    }
    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(NULL == mgr){
        LOGE("native assetManager is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    LOGE("asset %s",model_file_name_str);
    AAsset* asset = AAssetManager_open(mgr, model_file_name_str, AASSET_MODE_UNKNOWN);
    env->ReleaseStringUTFChars(model_path, model_file_name_str);
    unsigned char* buffer = NULL;
    int size = 0;
    if (NULL == asset){
        LOGE("open asset file failed");
        return ST_JNI_ERROR_FILE_OPEN_FIALED;
    }

    size = AAsset_getLength(asset);
    buffer = new unsigned char[size];
    memset(buffer,'\0',size);
    int readSize = AAsset_read(asset,buffer,size);

    if (readSize != size){
        AAsset_close(asset);
        if(buffer){
            delete[] buffer;
            buffer = NULL;
        }
        return ST_JNI_ERROR_FILE_SIZE;
    }

    AAsset_close(asset);

    if (size < 1000){
        LOGE("Model file is too samll");
        if(buffer){
            delete[] buffer;
            buffer = NULL;
        }
        return ST_JNI_ERROR_FILE_SIZE;
    }

    int result = st_mobile_human_action_create_from_buffer(buffer, size, (int)config, &handle);
    if(buffer){
        delete[] buffer;
        buffer = NULL;
    }

    if(result != 0){
        LOGE("create handle failed, %d",result);
        return result;
    }

    setHumanActionHandle(env, obj, handle);
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_createInstanceWithSubModels(JNIEnv * env, jobject obj, jobjectArray modelPaths, jint modelCount, jint config)
{
    st_handle_t human_action_handle = NULL;

    if (modelPaths == NULL) {
        LOGE("model path is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    jint count = env->GetArrayLength(modelPaths);
    const char *modelpathChars[count];
    jstring modelPathStrings[count];

    for(int i = 0; i < count; i++){
        modelPathStrings[i] = (jstring)env->GetObjectArrayElement(modelPaths, i);
        modelpathChars[i] = env->GetStringUTFChars(modelPathStrings[i], 0);
    }

    int result = st_mobile_human_action_create_with_sub_models(modelpathChars, count, config, &human_action_handle);

    if(result == 0){
        setHumanActionHandle(env, obj, human_action_handle);
    }

    for(int i = 0; i < count; i++){
        env->ReleaseStringUTFChars(modelPathStrings[i], modelpathChars[i]);
    }
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_addSubModel(JNIEnv * env, jobject obj, jstring modelpath)
{
    timeval time_start;
    gettimeofday(&time_start, NULL);

    st_handle_t humanActionhandle = getHumanActionHandle(env, obj);
    if(humanActionhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if (modelpath == NULL) {
        LOGE("model path is null");
        return ST_JNI_ERROR_INVALIDARG;
    }
    const char *modelpathChars = env->GetStringUTFChars(modelpath, 0);
    int result = st_mobile_human_action_add_sub_model(humanActionhandle, modelpathChars);

    LOGI("add sub model %s", modelpathChars);

    timeval time_end;
    gettimeofday(&time_end, NULL);
    LOGE("add sub model cost time: %.2f ms\n",
         ((time_end.tv_sec - time_start.tv_sec) * 1000) +
         (time_end.tv_usec - time_start.tv_usec) / 1000.f);

    LOGE("add sub model result: %d", result);
    env->ReleaseStringUTFChars(modelpath, modelpathChars);
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_addSubModelFromAssetFile(JNIEnv * env, jobject obj, jstring model_path, jobject assetManager){

    timeval time_start;
    gettimeofday(&time_start, NULL);

    st_handle_t humanActionhandle = getHumanActionHandle(env, obj);
    if(humanActionhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

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

    LOGI("add sub model asset %s",model_file_name_str);

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

    int result = st_mobile_human_action_add_sub_model_from_buffer(humanActionhandle, buffer, size);
    if(buffer){
        delete[] buffer;
        buffer = NULL;
    }

    if(result != 0){
        LOGE("add sub model failed, %d",result);
        return result;
    }


    timeval time_end;
    gettimeofday(&time_end, NULL);
    LOGI("add sub model cost time: %.2f ms\n",
         ((time_end.tv_sec - time_start.tv_sec) * 1000) +
         (time_end.tv_usec - time_start.tv_usec) / 1000.f);

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_removeSubModelByConfig(JNIEnv * env, jobject obj, jint config){
    st_handle_t humanActionhandle = getHumanActionHandle(env, obj);
    if(humanActionhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    int ret = st_mobile_human_action_remove_model_by_config(humanActionhandle, config);

    return ret;
}

JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_humanActionDetect(JNIEnv * env, jobject obj, jbyteArray pInputImage, jint imageFormat,
jlong detect_config, jint rotate, jint imageWidth, jint imageHeight)
{
	LOGI("humanActionDetect, the width is %d, the height is %d, the rotate is %d",imageWidth, imageHeight, rotate);
    st_handle_t humanActionhandle = getHumanActionHandle(env, obj);
    if(humanActionhandle == NULL)
    {
        LOGE("handle is null");
        return NULL;
    }

    if (pInputImage == NULL) {
        LOGE("input image is null");
        return NULL;
    }

    jbyte *srcdata = (jbyte*) (env->GetByteArrayElements(pInputImage, 0));
    int image_stride = getImageStride((st_pixel_format)imageFormat, imageWidth);

    st_mobile_human_action_t human_action;

    int result = ST_JNI_ERROR_DEFAULT;
    long startTime = getCurrentTime();
    if(humanActionhandle != NULL)
    {
        LOGI("before detect");
        result =  st_mobile_human_action_detect(humanActionhandle, (unsigned char *)srcdata,  (st_pixel_format)imageFormat,  imageWidth,
                                            imageHeight, image_stride, (st_rotate_type)rotate, detect_config, &human_action);
        LOGI("st_mobile_human_action_detect --- result is %d", result);
    }

    long afterdetectTime = getCurrentTime();
    LOGI("the human action detected time is %ld", (afterdetectTime - startTime));
    LOGI("the face count is %d", human_action.face_count);
    env->ReleaseByteArrayElements(pInputImage, srcdata, 0);

    jobject humanAction = NULL;
    if (result == ST_OK)
    {
        humanAction = convert2HumanAction(env, &human_action);
    }

    return humanAction;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_reset(JNIEnv * env, jobject obj)
{
	st_handle_t humanActionhandle = getHumanActionHandle(env, obj);
	if(humanActionhandle != NULL)
	{
	    return st_mobile_human_action_reset(humanActionhandle);
	}

	return ST_E_HANDLE;
}
JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_destroyInstance(JNIEnv * env, jobject obj)
{
	st_handle_t humanActionhandle = getHumanActionHandle(env, obj);
	if(humanActionhandle != NULL)
	{
	    LOGI(" human action destory");
	    setHumanActionHandle(env,obj,NULL);
	    st_mobile_human_action_destroy(humanActionhandle);
	}
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_setParam(JNIEnv * env, jobject obj, jint type, jfloat value)
{
    st_handle_t handle = getHumanActionHandle(env, obj);
    if(handle == NULL)
    {
        return ST_E_HANDLE;
    }
    LOGE("set Param for %d, %f", type, value);
    int result = (int)st_mobile_human_action_setparam(handle,(st_human_action_type)type,value);
    return result;
}

JNIEXPORT jbooleanArray JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_getExpression(JNIEnv * env, jobject obj, jobject humanAction, jint orientation, jboolean needMirror)
{
    if(humanAction == NULL){
        LOGE("humanAction is null");
        return NULL;
    }

    st_mobile_human_action_t human_action = {0};

    if (!convert2HumanAction(env, humanAction, &human_action)) {
        memset(&human_action, 0, sizeof(st_mobile_human_action_t));
    }

    bool expressionResult[ST_MOBILE_EXPRESSION_COUNT];

    int result = st_mobile_get_expression(&human_action, (st_rotate_type)orientation, (bool)needMirror, expressionResult);
    releaseHumanAction(&human_action);

    if(result != ST_OK){
        return NULL;
    }

    jboolean expression[ST_MOBILE_EXPRESSION_COUNT];
    for (int i = 0; i < ST_MOBILE_EXPRESSION_COUNT; ++i) {
        expression[i] = expressionResult[i];
    }
    jbooleanArray expressionArray = env->NewBooleanArray(ST_MOBILE_EXPRESSION_COUNT);
    env->SetBooleanArrayRegion(expressionArray, 0, ST_MOBILE_EXPRESSION_COUNT, expression);

    return expressionArray;
}

JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileHumanActionNative_setExpressionThreshold(JNIEnv * env, jobject obj, jint detectExpression, jfloat threshold)
{
    int result = st_mobile_set_expression_threshold((ST_MOBILE_EXPRESSION)detectExpression, threshold);
}

JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_model_STHumanAction_humanActionMirror(JNIEnv * env, jobject obj, jint width, jobject humanAction)
{
    if(humanAction == NULL){
        LOGE("humanAction is null");
        return NULL;
    }

    st_mobile_human_action_t human_action = {0};

    if (!convert2HumanAction(env, humanAction, &human_action)) {
        memset(&human_action, 0, sizeof(st_mobile_human_action_t));
    }

    st_mobile_human_action_mirror(width, &human_action);

    humanAction = convert2HumanAction(env, &human_action);

    releaseHumanAction(&human_action);

    return humanAction;
}

JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_model_STHumanAction_humanActionRotate(JNIEnv * env, jobject obj, jint width, jint height, jint orientation, jboolean rotateBackground, jobject humanAction)
{
    if(humanAction == NULL){
        LOGE("humanAction is null");
        return NULL;
    }

    st_mobile_human_action_t human_action = {0};

    if (!convert2HumanAction(env, humanAction, &human_action)) {
        memset(&human_action, 0, sizeof(st_mobile_human_action_t));
    }

    st_mobile_human_action_rotate(width, height, (st_rotate_type)orientation, (bool)rotateBackground, &human_action);

    humanAction = convert2HumanAction(env, &human_action);

    releaseHumanAction(&human_action);

    return humanAction;
}

JNIEXPORT jobject JNICALL Java_com_sensetime_stmobile_model_STHumanAction_humanActionResize(JNIEnv * env, jobject obj,
                                                                                            jfloat scale, jobject humanAction)
{
    if(humanAction == NULL){
        LOGE("humanAction is null");
        return NULL;
    }

    st_mobile_human_action_t human_action = {0};

    if (!convert2HumanAction(env, humanAction, &human_action)) {
        memset(&human_action, 0, sizeof(st_mobile_human_action_t));
    }

    st_mobile_human_action_resize(scale, &human_action);

    humanAction = convert2HumanAction(env, &human_action);

    releaseHumanAction(&human_action);

    return humanAction;
}
