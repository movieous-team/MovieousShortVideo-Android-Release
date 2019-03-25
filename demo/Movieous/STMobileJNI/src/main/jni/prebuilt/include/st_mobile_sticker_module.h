#ifndef INCLUDE_STMOBILE_ST_MOBILE_STICKER_MODULE_H_
#define INCLUDE_STMOBILE_ST_MOBILE_STICKER_MODULE_H_

#include "st_mobile_common.h"

/// @brief 声音解压回调函数
/// @param[in] handle 贴纸句柄
/// @param[in] sound 声音内存数据
/// @param[in] sound_name 声音文件名
/// @param[in] length 声音数据长度
typedef void(*st_mobile_sticker_load_sound_func) (void* handle, void* sound, const char* sound_name, int length);

/// @brief 声音播放回调函数
/// @param[in] handle 贴纸句柄
/// @param[in] sound_name 声音文件名
/// @param[in] loop 所需播放循环数, 0代表无限循环
typedef void(*st_mobile_sticker_play_sound_func) (void* handle, const char* sound_name, int loop);

/// @brief 声音暂停播放回调函数.
/// @param[in] handle 贴纸句柄
/// @param[in] sound_name 声音名字.
typedef void(*st_mobile_sticker_pause_sound_func) (void* handle, const char* sound_name);

/// @brief 声音恢复播放回调函数
/// @param[in] handle 贴纸句柄
/// @param[in] sound_name 声音名字.
typedef void(*st_mobile_sticker_resume_sound_func) (void* handle, const char* sound_name);

/// @brief 声音停止回调函数
/// @param[in] handle 贴纸句柄
/// @param[in] sound_name 声音文件名
typedef void(*st_mobile_sticker_stop_sound_func) (void* handle, const char* sound_name);

/// @brief 请求删除声音数据的回调函数
/// @param[in] handle 贴纸句柄
/// @param[in] sound_name 声音文件名
typedef void(*st_mobile_sticker_unload_sound_func) (void* handle, const char* sound_name);

/// @brief 贴纸part播放event回调
/// @param[in] handle 贴纸句柄
/// @param[in] module_name 贴纸part名字
/// @param[in] module_id 贴纸part id
/// @param[in] animation_event 贴纸part播放event, 见st_mobile_sticker_transition.h中的st_animation_state_type
/// @param[in] current_frame 当前播放的帧数
/// @param[in] position_id 贴纸对应的position id, 即st_mobile_human_action_t结果中不同类型结果中的id
/// @param[in] position_type 贴纸对应的position种类, 见st_mobile_human_action_t中的动作类型
typedef void(*st_animation_event_handle_func)(void* handle, const char* module_name, int module_id, int animation_event, int current_frame, int position_id, unsigned long long position_type);

/// @brief 贴纸关键帧 event回调
/// @param[in] handle 贴纸句柄
/// @param[in] material_name 贴纸part名字
/// @param[in] frame 触发的关键帧
typedef void(*key_frame_event)(void* handle, const char* material_name, int frame);

/// @brief 贴纸package播放event回调
/// @param[in] handle 贴纸句柄
/// @param[in] package_name 贴纸package名字
/// @param[in] package_id 贴纸package id
/// @param[in] event 贴纸package播放event, 见st_mobile_sticker_transition.h中的st_package_state_type
/// @param[in] displayed_frame 当前package播放的帧数
typedef void(*st_package_event_handle_func)(void* handle, const char* package_name, int packageID, int event, int displayed_frame);
/// @brief 贴纸模块参数
typedef enum {
    ST_STICKER_PARAM_MODULE_TYPE_INT = 100,                     ///< 获取贴纸模块的类型
    ST_STICKER_PARAM_MODULE_PACKAGE_ID_INT = 101,               ///< 设置/获取贴纸模块的package id
    ST_STICKER_PARAM_MODULE_ENABLED_BOOL = 102,                 ///< 设置贴纸模块的enable属性
    ST_STICKER_PARAM_MODULE_NAME_STR = 103,                     ///< 设置/获取贴纸模块的名称：字符串以 '\0' 结尾
    ST_STICKER_PARAM_MUDULE_POSITION_ULL = 104,                 ///< 设置/获取贴纸模块的位置 (unsigned long long)
    ST_STICKER_PARAM_MODULE_RENDER_ORDER_INDEX_INT = 105,       ///< 设置/获取贴纸模块的渲染序号, 序号较小的先渲染
    ST_STICKER_PARAM_MODULE_ANIMATION_EVENT_CALLBACK_PTR = 106, ///< 设置贴纸模块的动画事件回调函数
    ST_STICKER_PARAM_MODULE_SCALE_FLOAT = 107,                  ///< 设置贴纸模块显示的缩放比例
    ST_STICKER_PARAM_MODULE_OFFSET_LEFT_INT = 108,              ///< 设置贴纸模块向左偏移的像素个数
    ST_STICKER_PARAM_MODULE_OFFSET_RIGHT_INT = 109,             ///< 设置贴纸模块向右偏移的像素个数
    ST_STICKER_PARAM_MODULE_OFFSET_TOP_INT = 110,               ///< 设置贴纸模块向上偏移的像素个数
    ST_STICKER_PARAM_MODULE_OFFSET_BOTTOM_INT = 111,            ///< 设置贴纸模块向下偏移的像素个数
    ST_STICKER_PARAM_MODULE_KEY_FRAME_EVENT_CALLBACK_PTR = 112  ///< 设置贴纸模块的关键帧事件回调函数
} st_sticker_module_param_type;

/// @brief 2D贴纸模块参数
typedef enum {
    ST_STICKER_PARAM_2D_STICKER_WEARING_POSITION_INT = 200,
    ST_STICKER_PARAM_2D_STICKER_COUNTER_SIZE_INT = 201,
    ST_STICKER_PARAM_2D_STICKER_ROTATION_CENTER_X_INT = 202,
    ST_STICKER_PARAM_2D_STICKER_ROTATION_CENTER_Y_INT = 203,
    ST_STICKER_PARAM_2D_STICKER_ANCHOR_POINT_X_INT = 204,
    ST_STICKER_PARAM_2D_STICKER_ANCHOR_POINT_Y_INT = 205,
    ST_STICKER_PARAM_2D_STICKER_X_ANCHOR_KEY_INDEX_INT_ARRAY = 206, ///< int数组, X...
    ST_STICKER_PARAM_2D_STICKER_Y_ANCHOR_KEY_INDEX_INT_ARRAY = 207, ///< int数组,
    ST_STICKER_PARAM_2D_STICKER_X_SCALE_STANDARD_FLOAT = 208,
    ST_STICKER_PARAM_2D_STICKER_X_SCALE_KEY_INDEX_INT_ARRAY = 209,  ///< int数组,
    ST_STICKER_PARAM_2D_STICKER_Y_SCALE_STANDARD_FLOAT = 210,
    ST_STICKER_PARAM_2D_STICKER_Y_SCALE_KEY_INDEX_INT_ARRAY = 211,  ///< int数组,
    ST_STICKER_PARAM_2D_STICKER_POSITION_RELATION_TYPE_INT = 212,
    ST_STICKER_PARAM_2D_STICKER_Z_DEPTH_INT = 213
} st_sticker_2d_param_type;

/// @brief 声音贴纸模块参数
typedef enum {
    ST_STICKER_PARAM_SOUND_NAME_STR = 300,      ///< 设置/获取音乐模块的名称
    ST_STICKER_PARAM_SOUND_LOOP_INT = 301       ///< 设置/获取音乐模块的循环次数
} st_sticker_sound_param_type;

/// @brief 美颜贴纸模块参数
typedef enum {
	ST_STICKER_PARAM_BEAUTIFY_REDDEN_STRENGTH_FLOAT = 400,      ///< 设置/获取贴纸美颜模块的红润强度, [0,1.0], 默认值0.36, 0.0不做红润
	ST_STICKER_PARAM_BEAUTIFY_SMOOTH_STRENGTH_FLOAT = 401,      ///< 设置/获取贴纸美颜模块的磨皮强度, [0,1.0], 默认值0.74, 0.0不做磨皮
	ST_STICKER_PARAM_BEAUTIFY_WHITEN_STRENGTH_FLOAT = 402,      ///< 设置/获取贴纸美颜模块的美白强度, [0,1.0], 默认值0.30, 0.0不做美白
	ST_STICKER_PARAM_BEAUTIFY_ENLARGE_EYE_RATIO_FLOAT = 403,    ///< 设置/获取贴纸美颜模块的大眼比例, [0,1.0], 默认值0.13, 0.0不做大眼效果
	ST_STICKER_PARAM_BEAUTIFY_SHRINK_FACE_RATIO_FLOAT = 404,    ///< 设置/获取贴纸美颜模块的瘦脸比例, [0,1.0], 默认值0.11, 0.0不做瘦脸效果
	ST_STICKER_PARAM_BEAUTIFY_SHRINK_JAW_RATIO_FLOAT = 405,     ///< 设置/获取贴纸美颜模块的小脸比例, [0,1.0], 默认值0.10, 0.0不做小脸效果
	ST_STICKER_PARAM_BEAUTIFY_SATURATION_STRENGTH_FLOAT = 406,	///< 设置/获取贴纸美颜模块的饱和度强度, [0,1.0], 默认值为0?， 0.0不做饱和度处理
	ST_STICKER_PARAM_BEAUTIFY_CONTRAST_STRENGTH_FLOAT = 407		///< 设置/获取贴纸美颜模块的对比度强度, [0,1.0], 默认值为0?,
} st_sticker_beautify_param_type;

/// @brief 滤镜贴纸模块参数
typedef enum {
    ST_STICKER_PARAM_FILTER_FLOAT_STRENGTH = 500                ///< 设置/获取滤镜模块的强度
} st_sticker_filter_param_type;

/// @brief 美妆模块参数
typedef enum {
    ST_STICKER_PARAM_MAKEUP_SCALE_FLOAT = 700,
    ST_STICKER_PARAM_MAKEUP_WIDTH_INT = 701,
    ST_STICKER_PARAM_MAKEUP_HEIGHT_INT = 702,
    ST_STICKER_PARAM_MAKEUP_CANVAS_WIDTH_INT = 703,
    ST_STICKER_PARAM_MAKEUP_CANVAS_HEIGHT_INT = 704
} st_sticker_makeup_param_type;

/// @brief 背景描边模块参数
typedef enum {
    ST_STICKER_PARAM_BACKGROUND_EDGE_WIDTH_INT = 800,       ///< 设置/获取背景描边的宽度
    ST_STICKER_PARAM_BACKGROUND_EDGE_COLOR_UINT = 801       ///< 设置/获取背景描边的颜色
} st_sticker_background_edge_param_type;

#endif  // INCLUDE_STMOBILE_ST_MOBILE_STICKER_MODULE_H_
