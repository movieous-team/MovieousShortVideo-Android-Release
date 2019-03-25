#ifndef INCLUDE_STMOBILE_ST_MOBILE_STICKER_TRANSITION_H_
#define INCLUDE_STMOBILE_ST_MOBILE_STICKER_TRANSITION_H_

#include "st_mobile_common.h"

/// @brief 贴纸part的播放状态类型
typedef enum {
    ST_AS_PAUSED_FIRST_FRAME = 1,
    ST_AS_PLAYING = 2,
    ST_AS_PAUSED = 3,
    ST_AS_PAUSED_LAST_FRAME = 4,
    ST_AS_INVISIBLE = 5
} st_animation_state_type;

/// @brief 贴纸package的播放状态类型
typedef enum {
	ST_AS_BEGIN = 1,
	ST_AS_END = 2
} st_package_state_type;

/// @brief 事件类型
typedef enum {
    ST_EVENT_ANIMATION = 1,
    ST_EVENT_HUMAN_ACTION = 2,
    ST_EVENT_CUSTOM = 3
} st_trigger_event_type;

#define ST_CUSTOM_EVENT_1 (1)
#define ST_CUSTOM_EVENT_2 (2)
#define ST_CUSTOM_EVENT_3 (4)
#define ST_CUSTOM_EVENT_4 (8)
#define ST_CUSTOM_EVENT_5 (16)


/// @brief 贴纸播放事件类型
typedef enum {
    ST_ANIMATION_EVENT_BEGIN = 1,       ///< enter first frame
    ST_ANIMATION_EVENT_PLAY = 2,        ///< enter playing
    ST_ANIMATION_EVENT_PAUSE = 3,       ///< enter paused
    ST_ANIMATION_EVENT_END = 4,         ///< enter laster frame
    ST_ANIMATION_EVENT_HIDE = 5         ///< enter invisible
} st_animation_event_type;

inline st_animation_event_type AnimState2Event(st_animation_state_type state)
{
    switch (state)
    {
    case ST_AS_PAUSED_FIRST_FRAME:
        return ST_ANIMATION_EVENT_BEGIN;
        break;

    case ST_AS_PLAYING:
        return ST_ANIMATION_EVENT_PLAY;
        break;

    case ST_AS_PAUSED:
        return ST_ANIMATION_EVENT_PAUSE;
        break;

    case ST_AS_PAUSED_LAST_FRAME:
        return ST_ANIMATION_EVENT_END;
        break;

    case ST_AS_INVISIBLE:
        return ST_ANIMATION_EVENT_HIDE;
        break;
    }

    // default is hide state.
    return ST_ANIMATION_EVENT_HIDE;
}

/// @brief 贴纸播放状态
//typedef struct {
//    int module_id;                      ///< 处于state的module id
//    st_animation_state_type state;      ///< 处于的state
//} st_animation_state;

typedef struct {
    st_trigger_event_type triggerType;  ///< trigger event的类型，有human action，animetion和custom三种
    unsigned long long trigger;         ///< trigger_event的值；
    int module_id;                      ///< animation中的module id，仅对animation event有效。如果是custom，或human action，此字段无意义
    bool is_appear;                     ///< trigger 是出现时出发还是消失时触发。仅对human_action有用。
} st_trigger_event;

typedef struct {
//    st_animation_state* pre_state;      ///< 前置状态
    int pre_state_module_id;
    st_animation_state_type pre_state;
    st_trigger_event* triggers;         ///< 触发事件数组
    int trigger_count;                  ///< 触发事件数组的长度
} st_condition;

/// @brief A struct contains all possible params used in transation,
///  pick which ones to use according to target_status
typedef struct {
    int fade_frame;                     ///< 需要多少帧渐变，当target是playing，first frame和invisible时有用；
    int delay_frame;                    ///< 延迟多少帧进行转换，都有用
    int lasting_frame;                  ///< 在当前状态下持续多少帧自动切换到下一状态，对first frame，last frame和pause有用
    int play_loop;                      ///< 播放多少个循环，对play有用
} st_trans_param;

/// @brief 为指定贴纸模块添加transition
/// @param[in] handle 已初始化的贴纸句柄
/// @param[in] module_id 贴纸模块id
/// @param[in] target_state 目标状态
/// @param[in] param 状态转换参数
/// @param[out] trans_id transition id
// st_mobile_sticker_add_trans_target_module
ST_SDK_API st_result_t
st_mobile_sticker_add_module_transition(
    st_handle_t handle,
    int module_id,
    st_animation_state_type target_state,
    st_condition* condition,
    st_trans_param* param,
    int* trans_id
);

/// @brief 移除指定贴纸模块的某一transition
/// @param[in] handle 已初始化的贴纸句柄
/// @param[in] trans_id transition id
// st_mobile_sticker_remove_trans_by_id
ST_SDK_API st_result_t
st_mobile_sticker_remove_transition(
    st_handle_t handle,
    int trans_id
);

/// @brief 清除指定贴纸模块的所有transition
/// @param[in] handle 已初始化的贴纸句柄
/// @param[in] module_id 贴纸模块id
// st_mobile_sticker_clear_trans_traget_module
ST_SDK_API st_result_t
st_mobile_sticker_clear_module_transition(
    st_handle_t handle,
    int module_id
);

#endif  ///< INCLUDE_STMOBILE_ST_MOBILE_STICKER_TRANSITION_H_
