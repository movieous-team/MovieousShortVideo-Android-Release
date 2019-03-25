#ifndef COM_STMOBILE_SOUND_PLAY_H
#define COM_STMOBILE_SOUND_PLAY_H

#ifdef __cplusplus
extern "C" {
#endif

void soundLoad(void* handle, void* sound, const char* sound_name, int length);
void soundPlay(void* handle, const char* sound_name, int loop);
void soundStop(void* handle, const char* sound_name);
void soundPause(void* handle, const char* sound_name);
void soundResume(void* handle, const char* sound_name);

#ifdef __cplusplus
}
#endif

#endif
