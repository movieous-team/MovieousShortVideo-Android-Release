#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <st_mobile_sticker_transition.h>
#include "st_mobile_license.h"
#include "st_mobile_sticker.h"
#include "stmobile_sound_play_jni.h"
#include "utils.h"
#include "jvmutil.h"

#define  LOG_TAG    "STMobileSticker"


extern "C" {
    JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_generateActiveCode(JNIEnv * env, jobject obj, jstring licensePath);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_checkActiveCode(JNIEnv * env, jobject obj, jstring licensePath, jstring activationCode);
    JNIEXPORT jstring JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_generateActiveCodeFromBuffer(JNIEnv * env, jobject obj, jstring licenseBuffer, jint licenseSize);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_checkActiveCodeFromBuffer(JNIEnv * env, jobject obj, jstring licenseBuffer, jint licenseSize, jstring activationCode);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_createInstanceNative(JNIEnv * env, jobject obj);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_processTexture(JNIEnv * env, jobject obj, jint textureIn, jobject humanAction, jint rotate, jint frontRotate, jint imageWidth, jint imageHeight, jboolean needsMirroring, jobject inputParams, jint textureOut);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_processTextureAndOutputBuffer(JNIEnv * env, jobject obj,jint textureIn, jobject humanAction, jint rotate, jint frontRotate, jint imageWidth, jint imageHeight, jboolean needsMirroring, jobject inputParams, jint textureOut, jint outFmt, jbyteArray imageOut);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_changeSticker(JNIEnv * env, jobject obj, jstring path);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_changeStickerFromAssetsFile(JNIEnv * env, jobject obj, jstring file_path, jobject assetManager);

    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_createSticker(JNIEnv * env, jobject obj, jint id);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_addSticker(JNIEnv * env, jobject obj, jstring path);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_addStickerFromAssetsFile(JNIEnv * env, jobject obj, jstring file_path, jobject assetManager);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeSticker(JNIEnv * env, jobject obj, jint id);
    JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeAllStickers(JNIEnv * env, jobject obj);


    JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_destroyInstanceNative(JNIEnv * env, jobject obj);
    JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getTriggerAction(JNIEnv * env, jobject obj);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setWaitingMaterialLoaded(JNIEnv * env, jobject obj, jboolean needWait);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setMaxMemory(JNIEnv * env, jobject obj, jint value);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setSoundPlayDone(JNIEnv *env, jobject obj, jstring name);
//    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setMinInterval(JNIEnv * env, jobject obj, jfloat value);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_loadAvatarModel(JNIEnv * env, jobject obj, jstring modelpath);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_loadAvatarModelFromAssetFile(JNIEnv * env, jobject obj, jstring model_path, jobject assetManager);
    JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeAvatarModel(JNIEnv * env, jobject obj);

    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_createModule(JNIEnv * env, jobject obj, jint moduleType, jint packageId, jint moudleId);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_moveModuleToPackage(JNIEnv * env, jobject obj, jint packageId, jint moudleId);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeModule(JNIEnv * env, jobject obj, jint moudleId);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_addModuleTransition(JNIEnv * env, jobject obj, jint moduleId, jint targetState, jobjectArray conditionArray, jobjectArray paramArray, jintArray transId);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeTransition(JNIEnv * env, jobject obj, jint transId);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_clearModuleTransition(JNIEnv * env, jobject obj, jint moduleId);
    JNIEXPORT jobjectArray JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getModules(JNIEnv * env, jobject obj);
    JNIEXPORT jintArray JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getPackageIds(JNIEnv * env, jobject obj);

    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setParamInt(JNIEnv * env, jobject obj, jint moduleId, jint paramType, jint value);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setParamLong(JNIEnv * env, jobject obj, jint moduleId, jint paramType, jlong value);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setParamBool(JNIEnv * env, jobject obj, jint moduleId, jint paramType, jboolean value);
    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setParamStr(JNIEnv * env, jobject obj, jint moduleId, jint paramType, jstring value);

    JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getParamInt(JNIEnv * env, jobject obj, jint moduleId, jint paramType);
    JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getNeededInputParams(JNIEnv * env, jobject obj);
};

//void item_callback(const char* material_name, st_material_status statusCode) {
//    LOGI("-->> item_callback: start item callback");
//
//    int status;
//    JNIEnv *env;
//    bool isAttached = false;
//    status = gJavaVM->AttachCurrentThread(&env, NULL);
//    if(status<0) {
//        LOGE("-->> item_callback: failed to attach current thread!");
//        return;
//    }
//    isAttached = true;
//
//    jclass interfaceClass = env->GetObjectClass(gStickerObject);
//    if(!interfaceClass) {
//        LOGE("-->> item_callback: failed to get class reference");
//        if(isAttached) gJavaVM->DetachCurrentThread();
//        return;
//    }
//
//    /* Find the callBack method ID */
//    jmethodID method = env->GetStaticMethodID(interfaceClass, "item_callback", "(Ljava/lang/String;I)V");
//    if(!method) {
//        LOGE("item_callback: failed to get method ID");
//        if(isAttached) gJavaVM->DetachCurrentThread();
//        return;
//    }
//
//    jstring resultStr = env->NewStringUTF(material_name);
//    LOGI("-->> item_callback: resultStr=%s, status=%d",material_name, statusCode);
//
//    //get callback datas
//
//    env->CallStaticVoidMethod(interfaceClass, method, resultStr, (jint)statusCode);
//    env->DeleteLocalRef(interfaceClass);
//    env->DeleteLocalRef(resultStr);
//    LOGI("-->> material_name , status_string =&&&&");
//	//env->ReleaseStringChars(resultStr, material_name);
////	if(isAttached) gJavaVM->DetachCurrentThread();
//}

static inline jfieldID getStickerHandleField(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    // J is the type signature for long:
    return env->GetFieldID(c, "nativeStickerHandle", "J");
}

void setStickerHandle(JNIEnv *env, jobject obj, void *h)
{
    jlong handle = reinterpret_cast<jlong>(h);
    env->SetLongField(obj, getStickerHandleField(env, obj), handle);
}

void *getStickerHandle(JNIEnv *env, jobject obj)
{
    jlong handle = env->GetLongField(obj, getStickerHandleField(env, obj));
    return reinterpret_cast<void *>(handle);
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_createInstanceNative(JNIEnv * env, jobject obj)
{
    st_handle_t  ha_handle = NULL;

    gStickerObject = env->NewGlobalRef(obj);

    st_handle_t sticker_handle = NULL;
    int result = st_mobile_sticker_create(&sticker_handle);
    if(result != 0)
    {
        LOGE("st_mobile_sticker_create failed");
        return result;
    }

    st_mobile_sticker_set_param_ptr(sticker_handle, -1, ST_STICKER_PARAM_SOUND_LOAD_FUNC_PTR, (void*)&soundLoad);
    st_mobile_sticker_set_param_ptr(sticker_handle, -1, ST_STICKER_PARAM_SOUND_PLAY_FUNC_PTR, (void*)&soundPlay);
    st_mobile_sticker_set_param_ptr(sticker_handle, -1, ST_STICKER_PARAM_SOUND_STOP_FUNC_PTR, (void*)&soundStop);

    st_mobile_sticker_set_param_ptr(sticker_handle, -1, ST_STICKER_PARAM_SOUND_PAUSE_FUNC_PTR, (void*)&soundPause);
    st_mobile_sticker_set_param_ptr(sticker_handle, -1, ST_STICKER_PARAM_SOUND_RESUME_FUNC_PTR, (void*)&soundResume);


    setStickerHandle(env, obj, sticker_handle);

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_processTexture(JNIEnv * env, jobject obj, jint textureIn,
    jobject humanAction, jint rotate, jint frontRotate, jint imageWidth, jint imageHeight, jboolean needsMirroring, jobject inputParams, jint textureOut)
{
    LOGI("processTexture, the width is %d, the height is %d, the rotate is %d",imageWidth, imageHeight, rotate);
    int result = ST_JNI_ERROR_DEFAULT;

    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if(stickerhandle == NULL)
    {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    st_mobile_human_action_t human_action = {0};

    if (!convert2HumanAction(env, humanAction, &human_action)) {
        memset(&human_action, 0, sizeof(st_mobile_human_action_t));
    }

    st_mobile_input_params_t input_params = {0};

    if (!convert2StickerInputParams(env, inputParams, input_params)) {
        memset(&input_params, 0, sizeof(st_mobile_input_params_t));
    }

    long startTime = getCurrentTime();
    if(stickerhandle != NULL)
    {
        result  = st_mobile_sticker_process_texture(stickerhandle, textureIn, imageWidth, imageHeight, (st_rotate_type)rotate, (st_rotate_type)frontRotate, needsMirroring, &human_action, &input_params, textureOut);
        LOGI("-->>st_mobile_sticker_process_texture --- result is %d", result);
    }

    long afterStickerTime = getCurrentTime();
    LOGI("process sticker time is %ld", (afterStickerTime - startTime));
    //	env->ReleasePrimitiveArrayCritical(pInputImage, srcdata, 0);

    releaseHumanAction(&human_action);

    //jclass vm_class = env->FindClass("dalvik/system/VMDebug");
    //jmethodID dump_mid = env->GetStaticMethodID( vm_class, "dumpReferenceTables", "()V" );
    //env->CallStaticVoidMethod( vm_class, dump_mid );

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_processTextureAndOutputBuffer(JNIEnv * env, jobject obj,
    jint textureIn, jobject humanAction, jint rotate, jint frontRotate, jint imageWidth, jint imageHeight, jboolean needsMirroring, jobject inputParams,
    jint textureOut, jint outFmt, jbyteArray imageOut) {
    LOGI("processTexture, the width is %d, the height is %d, the rotate is %d",imageWidth, imageHeight, rotate);
    int result = ST_JNI_ERROR_DEFAULT;

    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    jbyte *dstdata = NULL;
    if (imageOut != NULL) {
        dstdata = (jbyte *) (env->GetByteArrayElements(imageOut, 0));
    }

    st_mobile_human_action_t human_action = {0};

    if (!convert2HumanAction(env, humanAction, &human_action)) {
        memset(&human_action, 0, sizeof(st_mobile_human_action_t));
    }

    st_mobile_input_params_t input_params = {0};

    if (!convert2StickerInputParams(env, inputParams, input_params)) {
        memset(&input_params, 0, sizeof(st_mobile_input_params_t));
    }

    long startTime = getCurrentTime();
    if (stickerhandle != NULL) {
        //result  = st_mobile_sticker_process_texture(stickerhandle, textureIn, imageWidth, imageHeight,  (st_rotate_type)rotate,needsMirroring, &human_action, item_callback, textureOut);
        result = st_mobile_sticker_process_and_output_texture(stickerhandle,
                                                              textureIn, imageWidth, imageHeight, (st_rotate_type) rotate, (st_rotate_type) frontRotate, needsMirroring,
                                                              &human_action, &input_params, textureOut, (unsigned char *) dstdata, (st_pixel_format) outFmt);
        LOGI("-->>st_mobile_sticker_process_and_output_texture --- result is %d", result);
    }

    releaseHumanAction(&human_action);

    long afterStickerTime = getCurrentTime();
    LOGI("process sticker time is %ld", (afterStickerTime - startTime));
    if (dstdata != NULL) {
        env->ReleaseByteArrayElements(imageOut, dstdata, 0);
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_changeSticker(JNIEnv * env, jobject obj, jstring path)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    const char *pathChars = NULL;
    if (path != NULL) {
        pathChars = env->GetStringUTFChars(path, 0);
    }

    int packageId = 0;

    if(stickerhandle != NULL) {
        result = st_mobile_sticker_change_package(stickerhandle, pathChars, &packageId);
    }
    if (pathChars != NULL) {
        env->ReleaseStringUTFChars(path, pathChars);
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_changeStickerFromAssetsFile(JNIEnv * env, jobject obj, jstring file_path, jobject assetManager)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if (stickerHandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    int packageId = 0;

    if(NULL == assetManager){
        LOGE("assetManager is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    if(file_path == NULL){
        result = st_mobile_sticker_change_package(stickerHandle, NULL, &packageId);
        LOGE("change sticker to null");
        return result;
    }

    const char* sticker_file_name_str = env->GetStringUTFChars(file_path, 0);
    if(NULL == sticker_file_name_str) {
        result = st_mobile_sticker_change_package(stickerHandle, NULL, &packageId);
        LOGE("file_name to c_str failed, change sticker to null");
        return result;
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(NULL == mgr) {
        LOGE("native assetManager is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    AAsset* asset = AAssetManager_open(mgr, sticker_file_name_str, AASSET_MODE_UNKNOWN);
    env->ReleaseStringUTFChars(file_path, sticker_file_name_str);
    if (NULL == asset) {
        LOGE("open asset file failed");
        result = st_mobile_sticker_change_package(stickerHandle, NULL, &packageId);
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

        result = st_mobile_sticker_change_package(stickerHandle, NULL, &packageId);
        return ST_JNI_ERROR_FILE_SIZE;
    }

    AAsset_close(asset);

    if (size < 100) {
        LOGE("Model file is too short");
        if (buffer) {
            delete[] buffer;
            buffer = NULL;
        }
        result = st_mobile_sticker_change_package(stickerHandle, NULL, &packageId);
        return ST_JNI_ERROR_FILE_SIZE;
    }

    if(stickerHandle != NULL) {
        result = st_mobile_sticker_change_package_from_buffer(stickerHandle, buffer, size, &packageId);
    }

    if(buffer){
        delete[] buffer;
        buffer = NULL;
    }

    if(result != 0){
        LOGE("change_package_from_buffer failed, %d",result);
        return result;
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_addSticker(JNIEnv * env, jobject obj, jstring path)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    const char *pathChars = NULL;
    if (path != NULL) {
        pathChars = env->GetStringUTFChars(path, 0);
    }

    int packageId = 0;
    if(stickerhandle != NULL) {
        result = st_mobile_sticker_add_package(stickerhandle,pathChars, &packageId);
    }
    if (pathChars != NULL) {
        env->ReleaseStringUTFChars(path, pathChars);
    }

    if(result == ST_OK){
        return packageId;
    } else{
        return result;
    }
}


JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_addStickerFromAssetsFile(JNIEnv * env, jobject obj, jstring file_path, jobject assetManager)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if (stickerHandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    int packageId = 0;

    if(NULL == assetManager){
        LOGE("assetManager is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    if(file_path == NULL){
        LOGE("add sticker null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    const char* sticker_file_name_str = env->GetStringUTFChars(file_path, 0);
    if(NULL == sticker_file_name_str) {
        LOGE("file_name to c_str failed, add sticker to null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(NULL == mgr) {
        LOGE("native assetManager is null");
        return ST_JNI_ERROR_INVALIDARG;
    }

    AAsset* asset = AAssetManager_open(mgr, sticker_file_name_str, AASSET_MODE_UNKNOWN);
    env->ReleaseStringUTFChars(file_path, sticker_file_name_str);
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

    if (size < 100) {
        LOGE("Model file is too short");
        if (buffer) {
            delete[] buffer;
            buffer = NULL;
        }
        return ST_JNI_ERROR_FILE_SIZE;
    }

    if(stickerHandle != NULL) {
        result = st_mobile_sticker_add_package_from_buffer(stickerHandle, buffer, size, &packageId);
    }

    if(buffer){
        delete[] buffer;
        buffer = NULL;
    }

    if(result == ST_OK){
        return packageId;
    } else{
        LOGE("add_package_from_buffer failed, %d",result);
        return result;
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_createSticker(JNIEnv * env, jobject obj, jint id){
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerhandle != NULL) {
        result = st_mobile_sticker_create_package(stickerhandle, &id);
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeSticker(JNIEnv * env, jobject obj, jint id){
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerhandle != NULL) {
        result = st_mobile_sticker_remove_package(stickerhandle,id);
    }

    return result;
}

JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeAllStickers(JNIEnv * env, jobject obj){
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
    }

    if(stickerhandle != NULL) {
        st_mobile_sticker_clear_packages(stickerhandle);
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_createModule(JNIEnv * env, jobject obj, jint moduleType, jint packageId, jint moudleId){
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerhandle != NULL) {
        result = st_mobile_sticker_create_module(stickerhandle, (st_module_type)moduleType, packageId, &moudleId);
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_moveModuleToPackage(JNIEnv * env, jobject obj, jint packageId, jint moudleId){
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerhandle != NULL) {
        result = st_mobile_sticker_move_module_to_package(stickerhandle, packageId, moudleId);
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeModule(JNIEnv * env, jobject obj, jint moudleId){
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerhandle != NULL) {
        result = st_mobile_sticker_remove_module(stickerhandle, moudleId);
    }

    return result;
}

JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_destroyInstanceNative(JNIEnv * env, jobject obj)
{
    st_handle_t stickerhandle = getStickerHandle(env, obj);
    if(stickerhandle != NULL)
    {
        LOGI(" sticker handle destory ");
        setStickerHandle(env, obj, NULL);
        st_mobile_sticker_destroy(stickerhandle);
    }

    if(gStickerObject != NULL) {
        env->DeleteGlobalRef(gStickerObject);
        gStickerObject = NULL;
    }
}

JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getTriggerAction(JNIEnv * env, jobject obj)
{
    st_handle_t stickerhandle = getStickerHandle(env, obj);
    if(stickerhandle != NULL)
    {
        unsigned long long action = -1;
        int result = st_mobile_sticker_get_trigger_action(stickerhandle, &action);
        if (result == ST_OK) {
            return action;
        }
    }

    return 0;
}

JNIEXPORT jlong JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getNeededInputParams(JNIEnv * env, jobject obj)
{
    st_handle_t stickerhandle = getStickerHandle(env, obj);
    if(stickerhandle != NULL)
    {
        int param_type = -1;
        int result = st_mobile_sticker_get_needed_input_params(stickerhandle, &param_type);
        if (result == ST_OK) {
            return param_type;
        }
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setWaitingMaterialLoaded(JNIEnv * env, jobject obj, jboolean needWait)
{
    bool need_wait = needWait;
    int result = ST_JNI_ERROR_DEFAULT;

    st_handle_t stickerhandle = getStickerHandle(env, obj);
    if(stickerhandle != NULL){
        //result = st_mobile_sticker_set_param_bool(stickerhandle, 0, ST_STICKER_PARAM_WAIT_MATERIAL_LOADED_BOOL, need_wait);
        result = st_mobile_sticker_set_waiting_material_loaded(stickerhandle, need_wait);
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setMaxMemory(JNIEnv * env, jobject obj, jint value)
{
    int result = ST_JNI_ERROR_DEFAULT;

    st_handle_t stickerhandle = getStickerHandle(env, obj);
    if(stickerhandle != NULL){
        result = st_mobile_sticker_set_param_int(stickerhandle, 0, ST_STICKER_PARAM_MAX_IMAGE_MEMORY_INT, value);
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setSoundPlayDone(JNIEnv *env, jobject obj, jstring name)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if(stickerHandle == NULL) {
        LOGE("stickerHandle is null");
        return ST_E_HANDLE;
    }

    if (name != NULL) {
        const char *nameCstr = NULL;
        nameCstr = env->GetStringUTFChars(name, 0);
        if (nameCstr != NULL) {
            st_mobile_sticker_set_param_str(stickerHandle, -1, ST_STICKER_PARAM_SOUND_COMPLETED_STR, nameCstr);
            env->ReleaseStringUTFChars(name, nameCstr);
        } else {
            LOGE("Sound name is NULL");
            return ST_JNI_ERROR_INVALIDARG;
        }

        LOGE("Set play done success");
        return ST_OK;
    }
}

//JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setMinInterval(JNIEnv * env, jobject obj, jfloat value)
//{
//    st_handle_t handle = getStickerHandle(env, obj);
//    if(handle == NULL)
//    {
//        return ST_E_HANDLE;
//    }
//    LOGE("sticker set min interval %f", value);
//    int result = (int)st_mobile_sticker_set_min_interval(handle, value);
//    return result;
//}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_loadAvatarModel(JNIEnv * env, jobject obj, jstring modelpath)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);
    if(stickerHandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if (modelpath == NULL) {
        LOGE("model path is null");
        return ST_JNI_ERROR_INVALIDARG;
    }
    const char *modelpathChars = env->GetStringUTFChars(modelpath, 0);
    int result = st_mobile_sticker_load_avatar_model(stickerHandle, modelpathChars);

    LOGE("load avatar model result: %d", result);
    env->ReleaseStringUTFChars(modelpath, modelpathChars);
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_loadAvatarModelFromAssetFile(JNIEnv * env, jobject obj, jstring model_path, jobject assetManager)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);
    if(stickerHandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(NULL == model_path){
        LOGE("model_file_name is null");
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

    int result = st_mobile_sticker_load_avatar_model_from_buffer(stickerHandle, (const char*)buffer, size);
    if(buffer){
        delete[] buffer;
        buffer = NULL;
    }

    if(result != 0){
        LOGE("load avatar model failed, %d",result);
        return result;
    }

    return result;
}

JNIEXPORT void JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeAvatarModel(JNIEnv * env, jobject obj)
{
    st_handle_t stickerhandle = getStickerHandle(env, obj);
    if(stickerhandle != NULL) {
        int result = st_mobile_sticker_remove_avatar_model(stickerhandle);

        if(result != ST_OK){
            LOGE("remove avatar model failed, %d",result);
        }
    }

}

JNIEXPORT jobjectArray JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getModules(JNIEnv * env, jobject obj)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return NULL;
    }

    int count = 0;
    result = st_mobile_sticker_get_param_int(stickerhandle, 0, ST_STICKER_PARAM_MODULES_COUNT_INT, &count);

    st_module_info* pModuleInfos = NULL;
    if (count > 0) {
        pModuleInfos = (st_module_info *)malloc(count * sizeof(st_module_info));
    }
    result = st_mobile_sticker_get_modules(stickerhandle, pModuleInfos, &count);
    jclass module_info_cls = env->FindClass("com/sensetime/stmobile/sticker_module_types/STModuleInfo");
    jobjectArray moduleInfos = (env)->NewObjectArray(count, module_info_cls, 0);

    if (result == ST_OK) {
        for (int i = 0; i < count; ++i) {
            jobject moduleInfoObj = convert2ModuleInfo(env, &pModuleInfos[i]);
            if (moduleInfoObj != NULL) {
                env->SetObjectArrayElement(moduleInfos, i, moduleInfoObj);
            }
            env->DeleteLocalRef(moduleInfoObj);
        }
    }

    env->DeleteLocalRef(module_info_cls);
    safe_delete_array(pModuleInfos);

    return moduleInfos;
}

JNIEXPORT jintArray JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getPackageIds(JNIEnv * env, jobject obj)
{
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return NULL;
    }

    int *packageIds = NULL;
    int count = 0;
    result = st_mobile_sticker_get_packages(stickerhandle, packageIds, &count);

    jintArray packageIdArray = (env)->NewIntArray(count);

    if (result == ST_OK) {
        env->SetIntArrayRegion(packageIdArray, 0, count, packageIds);
    }

    return packageIdArray;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setParamInt(JNIEnv * env, jobject obj, jint moduleId, jint paramType, jint value)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if(stickerHandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerHandle != NULL) {
        int result = st_mobile_sticker_set_param_int(stickerHandle, moduleId, paramType, value);

        if(result != ST_OK){
            LOGE("set param int failed, %d",result);
        }
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setParamLong(JNIEnv * env, jobject obj, jint moduleId, jint paramType, jlong value)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if(stickerHandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerHandle != NULL) {
        int result = st_mobile_sticker_set_param_ull(stickerHandle, moduleId, paramType, value);

        if(result != ST_OK){
            LOGE("set param long failed, %d",result);
        }
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setParamFloat(JNIEnv * env, jobject obj, jint moduleId, jint paramType, jfloat value)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if(stickerHandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerHandle != NULL) {
        int result = st_mobile_sticker_set_param_float(stickerHandle, moduleId, paramType, value);

        if(result != ST_OK){
            LOGE("set param float failed, %d",result);
        }
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setParamBool(JNIEnv * env, jobject obj, jint moduleId, jint paramType, jboolean value)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if(stickerHandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerHandle != NULL) {
        int result = st_mobile_sticker_set_param_bool(stickerHandle, moduleId, paramType, value);

        if(result != ST_OK){
            LOGE("set param bool failed, %d",result);
        }
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_setParamStr(JNIEnv * env, jobject obj, jint moduleId, jint paramType, jstring value)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if(stickerHandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    const char *pathChars = NULL;
    if (value != NULL) {
        pathChars = env->GetStringUTFChars(value, 0);
    }

    if(stickerHandle != NULL) {
        int result = st_mobile_sticker_set_param_str(stickerHandle, moduleId, paramType, pathChars);

        if(result != ST_OK){
            LOGE("set param str failed, %d",result);
        }
    }

    if (pathChars != NULL) {
        env->ReleaseStringUTFChars(value, pathChars);
    }
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_getParamInt(JNIEnv * env, jobject obj, jint moduleId, jint paramType)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if(stickerHandle == NULL) {
        LOGE("handle is null");
        return NULL;
    }

    int value = -1;

    if(stickerHandle != NULL) {
        int result = st_mobile_sticker_get_param_int(stickerHandle, moduleId, paramType, &value);

        if(result != ST_OK){
            LOGE("get param int failed, %d",result);
        }
    }
    return value;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_addModuleTransition(JNIEnv * env, jobject obj, jint moduleId, jint targetState, jobjectArray conditionArray, jobjectArray paramArray, jintArray transId)
{
    st_handle_t stickerHandle = getStickerHandle(env, obj);

    if(stickerHandle == NULL) {
        LOGE("handle is null");
        return NULL;
    }
    int result = ST_JNI_ERROR_DEFAULT;

    st_condition *conditions = NULL;
    int len =  env->GetArrayLength(conditionArray);
    if(len > 0){
        conditions = new st_condition[len];
        memset(conditions, 0, sizeof(st_condition)*len);
        for(int i = 0; i < len; i++){
            jobject conditionsObj = env->GetObjectArrayElement(conditionArray, i);
            convert2Condition(env, conditionsObj, conditions[i]);

            env->DeleteLocalRef(conditionsObj);
        }
    }

    st_trans_param *params = NULL;
    len = env->GetArrayLength(paramArray);
    if(len > 0){
        params = new st_trans_param[len];
        memset(params, 0, sizeof(st_trans_param)*len);
        for(int i = 0; i < len; i++){
            jobject paramsObj = env->GetObjectArrayElement(paramArray, i);
            convert2TransParam(env, paramsObj, params[i]);

            env->DeleteLocalRef(paramsObj);
        }
    }

    int trans_id = 0;
    if(stickerHandle != NULL){
        result = st_mobile_sticker_add_module_transition(stickerHandle, moduleId, (st_animation_state_type)targetState, conditions, params, &trans_id);

        if(result == ST_OK){
            env->SetIntArrayRegion(transId, 0, 1, &trans_id);
        }
    }
    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_removeTransition(JNIEnv * env, jobject obj, jint transId){
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerhandle != NULL) {
        result = st_mobile_sticker_remove_transition(stickerhandle, transId);
    }

    return result;
}

JNIEXPORT jint JNICALL Java_com_sensetime_stmobile_STMobileStickerNative_clearModuleTransition(JNIEnv * env, jobject obj, jint moduleId){
    int result = ST_JNI_ERROR_DEFAULT;
    st_handle_t stickerhandle = getStickerHandle(env, obj);

    if (stickerhandle == NULL) {
        LOGE("handle is null");
        return ST_E_HANDLE;
    }

    if(stickerhandle != NULL) {
        result = st_mobile_sticker_clear_module_transition(stickerhandle, moduleId);
    }

    return result;
}

