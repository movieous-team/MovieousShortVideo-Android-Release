#ifndef _JVMUTIL_H
#define _JVMUTIL_H

#include <jni.h>

extern JavaVM *gJavaVM;
extern jobject gStickerObject;

extern jobject getSoundPlayObjInSticker(JNIEnv* env);
extern void getEnv(JNIEnv** env, bool* isAttached);
extern jstring stoJstring(JNIEnv* env, const char* pat);

#endif
