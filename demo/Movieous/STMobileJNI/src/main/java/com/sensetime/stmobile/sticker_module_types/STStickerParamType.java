package com.sensetime.stmobile.sticker_module_types;


public class STStickerParamType {

    public static final int ST_STICKER_PARAM_MAX_IMAGE_MEMORY_INT = 0;          ///< 设置贴纸素材图像所占用的最大内存
    public static final int ST_STICKER_PARAM_WAIT_MATERIAL_LOADED_BOOL = 1;     ///< 等待素材加载完毕后再渲染，用于希望等待模型加载完毕再渲染的场景，比如单帧或较短视频的3D绘制等

    public static final int ST_STICKER_PARAM_SOUND_LOAD_FUNC_PTR = 2;           ///< 设置音乐加载回调函数
    public static final int ST_STICKER_PARAM_SOUND_PLAY_FUNC_PTR = 3;           ///< 设置音乐播放回调函数
    public static final int ST_STICKER_PARAM_SOUND_PAUSE_FUNC_PTR = 4;          ///< 设置音乐暂停回调函数
    public static final int ST_STICKER_PARAM_SOUND_STOP_FUNC_PTR = 5;           ///< 设置音乐停止回调函数
    public static final int ST_STICKER_PARAM_SOUND_COMPLETED_STR = 6;           ///< 设置已经播放完成的音乐
    public static final int ST_STICKER_PARAM_SOUND_UNLOAD_FUNC_PTR = 7;         ///< 设置音乐数据删除回调函数
    public static final int ST_STICKER_PARAM_MODULES_COUNT_INT = 8;             ///< 获取贴纸模块个数

    public static final int ST_STICKER_PARAM_PACKAGE_STATE_FUNC_PTR = 9;    ///< 设置package状态回调函数
    public static final int ST_STICKER_PARAM_SOUND_RESUME_FUNC_PTR = 10;          ///< 设置音乐继续回调函数
}
