#ifndef _ST_UTILS_H__
#define _ST_UTILS_H__

#include <sys/time.h>
#include <time.h>
#include <jni.h>
#include "st_mobile_sticker.h"
#include "st_mobile_face_attribute.h"

#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <string.h>
#include <st_mobile_sticker_transition.h>

#define LOGGABLE 0

#define LOGI(...) if(LOGGABLE) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#ifndef MIN
#define MIN(x,y) ((x)<(y)?(x):(y))
#endif

#ifndef safe_delete
#define safe_delete(x)  { if (x) { delete (x); (x) = NULL; } }
#endif

#ifndef safe_delete_array
#define safe_delete_array(x)  { if (x) { delete[] (x); (x) = NULL; } }
#endif

#define ST_JNI_ERROR_DEFAULT                -1000  ///< JNI定义的参数默认值

#define ST_JNI_ERROR_INVALIDARG             -1001  ///< 输入参数为null或无效
#define ST_JNI_ERROR_FILE_OPEN_FIALED       -1002  ///< 打开文件失败
#define ST_JNI_ERROR_FILE_SIZE              -1003  ///< 文件大小错误
#define ST_JNI_ERROR_ACTIVE_CODE            -1004  ///< 激活码错误

static inline jfieldID getHandleField(JNIEnv *env, jobject obj)
{
    jclass c = env->GetObjectClass(obj);
    // J is the type signature for long:
    return env->GetFieldID(c, "nativeHandle", "J");
}

template <typename T>
T *getHandle(JNIEnv *env, jobject obj)
{
    jlong handle = env->GetLongField(obj, getHandleField(env, obj));
    return reinterpret_cast<T *>(handle);
}

template <typename T>
void setHandle(JNIEnv *env, jobject obj, T *t)
{
    jlong handle = reinterpret_cast<jlong>(t);
    env->SetLongField(obj, getHandleField(env, obj), handle);
}

long getCurrentTime();
int getImageStride(const st_pixel_format &pixel_format, const int &outputWidth);
// convert c object to java object
jobject convert2STRect(JNIEnv *env, const st_rect_t &object_rect);
jobject convert2HumanAction(JNIEnv *env, const st_mobile_human_action_t *human_action);
void convert2HumanAction(JNIEnv *env, const st_mobile_human_action_t *human_action, jobject humanActionObj);
jobject convert2MobileFace106(JNIEnv *env, const st_mobile_106_t &mobile_106);
jobject convert2FaceAttribute(JNIEnv *env, const st_mobile_attributes_t *faceAttribute);
jobject convert2Image(JNIEnv *env, const st_image_t *image);
jobject convert2HandInfo(JNIEnv *env, const st_mobile_hand_t *hand_info);
jobject convert2FaceInfo(JNIEnv *env, const st_mobile_face_t *face_info);
jobject convert2BodyInfo(JNIEnv *env, const st_mobile_body_t *body_info);
jobject convert2ModuleInfo(JNIEnv *env, const st_module_info *module_info);

// convert java object to c object
bool convert2st_rect_t(JNIEnv *env, jobject rectObject, st_rect_t &rect);
bool convert2HumanAction(JNIEnv *env, jobject humanActionObject, st_mobile_human_action_t *human_action);
bool convert2mobile_106(JNIEnv *env, jobject face106, st_mobile_106_t &mobile_106);
bool convert2Image(JNIEnv *env, jobject image, st_image_t *background);
bool convert2HandInfo(JNIEnv *env, jobject handInfoObject, st_mobile_hand_t *hand_info);
bool convert2FaceInfo(JNIEnv *env, jobject faceInfoObject, st_mobile_face_t *face_info);
bool convert2BodyInfo(JNIEnv *env, jobject bodyInfoObject, st_mobile_body_t *body_info);
bool convert2Condition(JNIEnv *env, jobject conditionObject, st_condition &condition);
bool convert2TransParam(JNIEnv *env, jobject paramObject, st_trans_param &param);
bool convert2TriggerEvent(JNIEnv *env, jobject triggerEventObject, st_trigger_event &trigger_event);
bool convert2StickerInputParams(JNIEnv *env, jobject eventObject, st_mobile_input_params &input_params);

//for release human_action
void releaseHumanAction(st_mobile_human_action_t *human_action);

#endif // _ST_UTILS_H__