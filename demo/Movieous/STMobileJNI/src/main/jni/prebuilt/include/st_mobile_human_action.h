#ifndef INCLUDE_STMOBILE_ST_MOBILE_HUMAN_ACTION_H_
#define INCLUDE_STMOBILE_ST_MOBILE_HUMAN_ACTION_H_

#include "st_mobile_common.h"

/// 该文件中的API不保证线程安全.多线程调用时,需要确保安全调用.例如在 create handle 没有执行完就执行 process 可能造成crash;在 process 执行过程中调用 destroy 函数可能会造成crash.

/// 用于detect_config配置选项, 部分也可用来判断检测结果中的human_action动作类型
#define ST_MOBILE_FACE_DETECT               0x00000001  ///< 人脸检测
#define ST_MOBILE_EYE_BLINK                 0x00000002  ///< 眨眼（该检测耗时较长，请在需要时开启）
#define ST_MOBILE_MOUTH_AH                  0x00000004  ///< 嘴巴大张
#define ST_MOBILE_HEAD_YAW                  0x00000008  ///< 摇头
#define ST_MOBILE_HEAD_PITCH                0x00000010  ///< 点头
#define ST_MOBILE_BROW_JUMP                 0x00000020  ///< 眉毛挑动（该检测耗时较长，请在需要时开启）
#define ST_MOBILE_HAND_DETECT               0x00000100  ///  检测手
#define ST_MOBILE_HAND_OK                   0x00000200  ///< OK手势
#define ST_MOBILE_HAND_SCISSOR              0x00000400  ///< 剪刀手
#define ST_MOBILE_HAND_GOOD                 0x00000800  ///< 大拇哥
#define ST_MOBILE_HAND_PALM                 0x00001000  ///< 手掌
#define ST_MOBILE_HAND_PISTOL               0x00002000  ///< 手枪手势
#define ST_MOBILE_HAND_LOVE                 0x00004000  ///< 爱心手势
#define ST_MOBILE_HAND_HOLDUP               0x00008000  ///< 托手手势
#define ST_MOBILE_HAND_CONGRATULATE         0x00020000  ///< 恭贺（抱拳）
#define ST_MOBILE_HAND_FINGER_HEART         0x00040000  ///< 单手比爱心
#define ST_MOBILE_HAND_FINGER_INDEX         0x00100000  ///< 食指指尖
#define ST_MOBILE_HAND_FIST                 0x00200000  ///< 拳头手势
#define ST_MOBILE_HAND_666                  0x00400000  ///< 666
#define ST_MOBILE_HAND_BLESS                0x00800000  ///< 双手合十
#define ST_MOBILE_HAND_ILOVEYOU         0x010000000000  ///< 手势ILoveYou
#define ST_MOBILE_SEG_BACKGROUND            0x00010000  ///< 检测前景背景分割
#define ST_MOBILE_FACE_240_DETECT           0x01000000  ///< 检测人脸240关键点 (deprecated)
#define ST_MOBILE_DETECT_EXTRA_FACE_POINTS  0x01000000  ///< 检测人脸240关键点
#define ST_MOBILE_DETECT_EYEBALL_CENTER     0x02000000  ///< 检测眼球中心点
#define ST_MOBILE_DETECT_EYEBALL_CONTOUR    0x04000000  ///< 检测眼球轮廓点
#define ST_MOBILE_BODY_KEYPOINTS            0x08000000  ///< 检测肢体关键点
#define ST_MOBILE_BODY_CONTOUR              0x10000000  ///< 检测肢体轮廓点
#define ST_MOBILE_SEG_HAIR                  0x20000000    ///< 检测头发分割
#define ST_MOBILE_DETECT_TONGUE             0x40000000  ///< 检测舌头关键点
#define ST_MOBILE_BODY_ACTION1              0x0100000000    /// 龙拳 暂时不支持
#define ST_MOBILE_BODY_ACTION2              0x0200000000    /// 一休 暂时不支持
#define ST_MOBILE_BODY_ACTION3              0x0400000000    /// 摊手 暂时不支持
#define ST_MOBILE_BODY_ACTION4              0x0800000000    /// 蜘蛛侠 暂时不支持
#define ST_MOBILE_BODY_ACTION5              0x1000000000    /// 动感超人 暂时不支持
#define ST_MOBILE_DETECT_HAND_SKELETON_KEYPOINTS  0x20000000000    /// 检测单手关键点
#define ST_MOBILE_DETECT_HAND_SKELETON_KEYPOINTS_3D  0x40000000000    /// 检测单手3d关键点
#define ST_MOBILE_SEG_MULTI                 0x80000000000  ///< 检测多类分割

#define ST_MOBILE_FACE_DETECT_FULL        0x0000003F  ///< 检测所有脸部动作
#define ST_MOBILE_HAND_DETECT_FULL      0x010000FEFF00  ///< 检测所有手势
#define ST_MOBILE_BODY_DETECT_FULL        0x018000000 ///< 检测肢体关键点和肢体轮廓点

/// @brief 手势检测结果
typedef struct st_mobile_hand_t {
    int id;                     ///< 手的id
    st_rect_t rect;             ///< 手部矩形框
    int left_right;              ///< 0 unknown ,1 left, 2 right
    st_pointf_t *p_key_points;  ///< 手部关键点
    int key_points_count;       ///< 手部关键点个数
    unsigned long long hand_action; ///< 手部动作
    float score;                ///< 手部动作置信度
    st_pointf_t *p_skeleton_keypoints; ///< 手部骨骼点
    int skeleton_keypoints_count;      ///< 手部骨骼点个数 一般是0/20
    st_point3f_t *p_skeleton_3d_keypoints;   ///< 手部3d骨骼点
    int skeleton_3d_keypoints_count;   ///< 手部3d骨骼点的个数
} st_mobile_hand_t, *p_st_mobile_hand_t;

/// @brief 肢体检测结果
typedef struct st_mobile_body_t {
    int id;                     ///< 肢体 id
    st_pointf_t *p_key_points;  ///< 肢体关键点
    float * p_key_points_score;          ///< 肢体关键点的置信度[0,1] 值越大，置信度越高.建议用户使用0.15作为置信度阈值.
    int key_points_count;       ///< 肢体关键点个数 目前为0/4/14
	st_pointf_t *p_contour_points;  ///< 肢体轮廓点
	float * p_contour_points_score; ///< 肢体轮廓点的置信度[0,1] 值越大，置信度越高.建议用户使用0.15作为置信度阈值.
	int contour_points_count;       ///< 肢体轮廓点个数 目前为0/63
    unsigned long long body_action;   ///< 肢体动作，本版本无效
    float body_action_score;                ///< 肢体动作置信度,本版本无效
} st_mobile_body_t, *p_st_mobile_body_t;

/// @brief human_action检测结果
typedef struct st_mobile_human_action_t {
    st_mobile_face_t *p_faces;  ///< 检测到的人脸信息
    int face_count;             ///< 检测到的人脸数目
    st_mobile_hand_t *p_hands;  ///< 检测到的手的信息
    int hand_count;             ///< 检测到的手的数目
    st_image_t *p_background;   ///< 前后背景分割结果图片信息,前景为0,背景为255,边缘部分有模糊(0-255之间),输出图像大小可以调节
    float background_score;     ///< 前后背景分割置信度
    st_mobile_body_t *p_bodys;  ///< 检测到的肢体信息
    int body_count;             ///< 检测到的肢体的数目
    float camera_motion_score;  ///< 摄像头运动状态置信度
    st_image_t * p_hair;        ///< 头发分割结果图片信息,前景为0,背景为255,边缘部分有模糊(0-255之间),输出图像大小可以调节
    float hair_score;           ///< 头发分割置信度
    st_image_t *p_multi_segment;   ///< 多类分割结果图片信息,背景0，手1，头发2，眼镜3，躯干4，上臂5，下臂6，大腿7，小腿8，脚9，帽子10，随身物品11，脸12，上衣13，下装14，输出图像大小可以调节
    float multi_segment_score;     ///< 多类分割置信度
} st_mobile_human_action_t, *p_st_mobile_human_action_t;

/// @defgroup st_mobile_human_action
/// @brief human action detection interfaces
///
/// This set of interfaces detect human action.
///
/// @{

/// @brief 创建人体行为检测句柄配置选项
/// 支持的检测类型
#define ST_MOBILE_ENABLE_FACE_DETECT            0x00000040  ///< 检测人脸
#define ST_MOBILE_ENABLE_HAND_DETECT            0x00000080  ///< 检测手势
#define ST_MOBILE_ENABLE_SEGMENT_DETECT         0x00000100  ///< 检测背景分割
#define ST_MOBILE_ENABLE_FACE_240_DETECT        0x00000200  ///< 检测人脸240点 (deprecated, 请使用ST_MOBILE_ENABLE_FACE_EXTRA_DETECT)
#define ST_MOBILE_ENABLE_FACE_EXTRA_DETECT      0x00000200  ///< 检测人脸240点
#define ST_MOBILE_ENABLE_EYEBALL_CENTER_DETECT  0x00000400  ///< 检测眼球中心点
#define ST_MOBILE_ENABLE_EYEBALL_CONTOUR_DETECT 0x00000800  ///< 检测眼球轮廓点
#define ST_MOBILE_ENABLE_BODY_KEYPOINTS         0x00001000  ///< 检测肢体关键点4/14
#define ST_MOBILE_ENABLE_BODY_ACTION            0x00002000  ///< 检测肢体动作 本版本无效
#define ST_MOBILE_ENABLE_BODY_CONTOUR           0x00004000  ///< 检测肢体轮廓点
#define ST_MOBILE_ENABLE_HAIR_SEGMENT           0x00008000  ///< 检测头发分割
#define ST_MOBILE_ENABLE_TONGUE_DETECT          0x00010000  ///< 检测舌头关键点
#define ST_MOBILE_ENABLE_HAND_SKELETON_KEYPOINTS   0X01000000  ///< 检测手势关节点
#define ST_MOBILE_ENABLE_HAND_SKELETON_KEYPOINTS_3D    0X02000000  ///< 检测3d手势关节点
#define ST_MOBILE_ENABLE_MULTI_SEGMENT         0x04000000  ///< 检测多类分割

/// 检测模式
#define ST_MOBILE_DETECT_MODE_VIDEO             0x00020000  ///< 视频检测
#define ST_MOBILE_DETECT_MODE_IMAGE             0x00040000  ///< 图片检测 与视频检测互斥，只能同时使用一个

/// 创建人体行为检测句柄的默认配置: 设置检测模式和检测类型
/// 视频检测, 检测人脸、手势和前后背景
#define ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO     (ST_MOBILE_DETECT_MODE_VIDEO | \
                                                        ST_MOBILE_TRACKING_ENABLE_FACE_ACTION | \
                                                        ST_MOBILE_ENABLE_FACE_DETECT | \
                                                        ST_MOBILE_ENABLE_HAND_DETECT | \
                                                        ST_MOBILE_ENABLE_SEGMENT_DETECT)
/// 图片检测, 检测人脸、手势和前后背景
#define ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_IMAGE     (ST_MOBILE_DETECT_MODE_IMAGE | \
                                                        ST_MOBILE_ENABLE_FACE_DETECT | \
                                                        ST_MOBILE_ENABLE_HAND_DETECT | \
                                                        ST_MOBILE_ENABLE_SEGMENT_DETECT)

/// @brief 创建人体行为检测句柄. Android建议使用st_mobile_human_action_create_from_buffer
/// @param[in] model_path 模型文件的路径,例如models/action.model. 为NULL时需要调用st_mobile_human_action_add_sub_model添加需要的模型
/// @param[in] config 设置创建人体行为句柄的方式,检测视频时设置为ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO,检测图片时设置为ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_IMAGE
/// @parma[out] handle 人体行为检测句柄,失败返回NULL
/// @return 成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_human_action_create(
    const char *model_path,
    unsigned int config,
    st_handle_t *handle
);

/// @brief 创建人体行为检测句柄
/// @param[in] buffer 模型缓存起始地址,为NULL时需要调用st_mobile_human_action_add_sub_model添加需要的模型
/// @param[in] buffer_size 模型缓存大小
/// @param[in] config 设置创建人体行为句柄的方式,检测视频时设置为ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO,检测图片时设置为ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_IMAGE
/// @parma[out] handle 人体行为检测句柄,失败返回NULL
/// @return 成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_human_action_create_from_buffer(
    const unsigned char* buffer,
    unsigned int buffer_size,
    unsigned int config,
    st_handle_t *handle
);

/// @brief 通过子模型创建人体行为检测句柄, st_mobile_human_action_create和st_mobile_human_action_create_with_sub_models只能调一个
/// @param[in] model_path_arr 模型文件路径指针数组. 根据加载的子模型确定支持检测的类型. 如果包含相同的子模型, 后面的会覆盖前面的.
/// @param[in] model_count 模型文件数目
/// @param[in] detect_mode 设置检测模式. 检测视频时设置为ST_MOBILE_DETECT_MODE_VIDEO, 检测图片时设置为ST_MOBILE_DETECT_MODE_IMAGE
/// @parma[out] handle 人体行为检测句柄,失败返回NULL
/// @return 成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_human_action_create_with_sub_models(
    const char **model_path_arr,
    int model_count,
    unsigned int detect_mode,
    st_handle_t *handle
);

/// @brief 添加子模型. Android建议使用st_mobile_human_action_add_sub_model_from_buffer
/// @parma[in] handle 人体行为检测句柄
/// @param[in] model_path 模型文件的路径. 后添加的会覆盖之前添加的同类子模型。加载模型耗时较长, 建议在初始化创建句柄时就加载模型
ST_SDK_API st_result_t
st_mobile_human_action_add_sub_model(
    st_handle_t handle,
    const char *model_path
);
/// @brief 添加子模型.
/// @parma[in] handle 人体行为检测句柄
/// @param[in] buffer 模型缓存起始地址
/// @param[in] buffer_size 模型缓存大小
ST_SDK_API st_result_t
st_mobile_human_action_add_sub_model_from_buffer(
    st_handle_t handle,
    const unsigned char* buffer,
    unsigned int buffer_size
);

/// @brief 删除子模型.
/// @parma[in] handle 人体行为检测句柄
/// @param[in] config 与create_handle中的参数config意义相同, 例如ST_MOBILE_ENABLE_HAND_DETECT对应删除手势模型
/// @return 成功返回ST_OK， 失败返回其他错误码
ST_SDK_API
st_result_t st_mobile_human_action_remove_model_by_config(
	st_handle_t handle,
	unsigned int config
	);

/// @brief 释放人体行为检测句柄
/// @param[in] handle 已初始化的人体行为句柄
ST_SDK_API
void st_mobile_human_action_destroy(
    st_handle_t handle
);

/// @brief 人体行为检测
/// @param[in] handle 已初始化的人体行为句柄
/// @param[in] image 用于检测的图像数据
/// @param[in] pixel_format 用于检测的图像数据的像素格式. 检测人脸建议使用NV12、NV21、YUV420P(转灰度图较快),检测手势和前后背景建议使用BGR、BGRA、RGB、RGBA
/// @param[in] image_width 用于检测的图像的宽度(以像素为单位)
/// @param[in] image_height 用于检测的图像的高度(以像素为单位)
/// @param[in] image_stride 用于检测的图像的跨度(以像素为单位),即每行的字节数；目前仅支持字节对齐的padding,不支持roi
/// @param[in] orientation 图像中人脸的方向
/// @param[in] detect_config 需要检测的人体行为,例如ST_MOBILE_EYE_BLINK | ST_MOBILE_MOUTH_AH | ST_MOBILE_HAND_LOVE | ST_MOBILE_SEG_BACKGROUND
/// @param[out] p_human_action 检测到的人体行为,由用户分配内存. 会覆盖上一次的检测结果.
/// @return 成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_human_action_detect(
    st_handle_t handle,
    const unsigned char *image,
    st_pixel_format pixel_format,
    int image_width,
    int image_height,
    int image_stride,
    st_rotate_type orientation,
    unsigned long long detect_config,
    st_mobile_human_action_t *p_human_action
);

/// @brief 重置, 清除所有缓存信息. 视频模式下会在handle中缓存一些状态，当切换分辨率、切换前后摄像头、切换后台、两帧图像差别较大时建议调用reset
ST_SDK_API st_result_t
st_mobile_human_action_reset(
    st_handle_t handle
);
/// @brief human_action参数类型
typedef enum {
    /// 输出的background结果中长边的长度[10,长边长度](默认长边240,短边=长边/原始图像长边*原始图像短边).值越大,背景分割的耗时越长,边缘部分效果越好.
    ST_HUMAN_ACTION_PARAM_BACKGROUND_MAX_SIZE = 1,
    /// 背景分割羽化程度[0,1](默认值0.35),0 完全不羽化,1羽化程度最高,在strenth较小时,羽化程度基本不变.值越大,前景与背景之间的过度边缘部分越宽.
    ST_HUMAN_ACTION_PARAM_BACKGROUND_BLUR_STRENGTH = 2,
    /// 设置检测到的最大人脸数目N(默认值32, 最大值32),持续track已检测到的N个人脸直到人脸数小于N再继续做detect.值越大,检测到的人脸数目越多,但相应耗时越长.
    ST_HUMAN_ACTION_PARAM_FACELIMIT = 3,
    /// 设置tracker每多少帧进行一次detect(默认值有人脸时30,无人脸时30/3=10). 值越大,cpu占用率越低, 但检测出新人脸的时间越长.
    ST_HUMAN_ACTION_PARAM_FACE_DETECT_INTERVAL = 4,
    /// 设置106点平滑的阈值[0.0,1.0](默认值0.5), 值越大, 点越稳定,但相应点会有滞后.
    ST_HUMAN_ACTION_PARAM_SMOOTH_THRESHOLD = 5,
    /// 设置head_pose去抖动的阈值[0.0,1.0](默认值0.5),值越大, pose信息的值越稳定,但相应值会有滞后.
    ST_HUMAN_ACTION_PARAM_HEADPOSE_THRESHOLD = 6,
    /// 设置手势检测每多少帧进行一次 detect (默认有手时30帧detect一次, 无手时10(30/3)帧detect一次). 值越大,cpu占用率越低, 但检测出新人脸的时间越长.
    ST_HUMAN_ACTION_PARAM_HAND_DETECT_INTERVAL = 7,
    /// 设置前后背景检测结果灰度图的方向是否需要旋转（0: 不旋转, 保持竖直; 1: 旋转, 方向和输入图片一致. 默认不旋转)
    ST_HUMAN_ACTION_PARAM_BACKGROUND_RESULT_ROTATE = 8,
    /// 设置检测到的最大肢体数目N(默认值1),持续track已检测到的N个肢体直到肢体数小于N再继续做detect.值越大,检测到的body数目越多,但相应耗时越长.
    ST_HUMAN_ACTION_PARAM_BODY_LIMIT = 9,
    /// 设置肢体关键点检测每多少帧进行一次 detect (默认有肢体时30帧detect一次, 无body时10(30/3)帧detect一次). 值越大,cpu占用率越低, 但检测出新body的时间越长.
    ST_HUMAN_ACTION_PARAM_BODY_DETECT_INTERVAL = 10,
    /// 设置脸部隔帧检测（对上一帧结果做拷贝），目的是减少耗时。默认每帧检测一次. 最多每10帧检测一次. 开启隔帧检测后, 只能对拷贝出来的检测结果做后处理.
    ST_HUMAN_ACTION_PARAM_FACE_PROCESS_INTERVAL = 11,
	/// 设置手势隔帧检测（对上一帧结果做拷贝），目的是减少耗时。默认每帧检测一次. 最多每10帧检测一次. 开启隔帧检测后, 只能对拷贝出来的检测结果做后处理.
    ST_HUMAN_ACTION_PARAM_HAND_PROCESS_INTERVAL = 12,
	/// 设置背景隔帧检测（对上一帧结果做拷贝），目的是减少耗时。默认每帧检测一次. 最多每10帧检测一次. 开启隔帧检测后, 只能对拷贝出来的检测结果做后处理.
    ST_HUMAN_ACTION_PARAM_BACKGROUND_PROCESS_INTERVAL = 13,
	/// 设置肢体隔帧检测（对上一帧结果做拷贝），目的是减少耗时。默认每帧检测一次. 最多每10帧检测一次. 开启隔帧检测后, 只能对拷贝出来的检测结果做后处理.
    ST_HUMAN_ACTION_PARAM_BODY_PROCESS_INTERVAL = 14,
	/// 设置检测到的最大手数目N(默认值2, 最大值32),持续track已检测到的N个hand直到人脸数小于N再继续做detect.值越大,检测到的hand数目越多,但相应耗时越长.
	ST_HUMAN_ACTION_PARAM_HAND_LIMIT = 15,
	/// 头发结果中长边的长度[10,长边长度](默认长边240,短边=长边/原始图像长边*原始图像短边).值越大,头发分割的耗时越长,边缘部分效果越好.
	ST_HUMAN_ACTION_PARAM_HAIR_MAX_SIZE = 16,
	/// 头发分割羽化程度[0,1](默认值0.35),0 完全不羽化,1羽化程度最高,在strenth较小时,羽化程度基本不变.值越大,过度边缘部分越宽.
	ST_HUMAN_ACTION_PARAM_HAIR_BLUR_STRENGTH = 17,
	/// 设置头发灰度图的方向是否需要旋转（0: 不旋转, 保持竖直; 1: 旋转, 方向和输入图片一致. 默认0不旋转)
	ST_HUMAN_ACTION_PARAM_HAIR_RESULT_ROTATE = 18,
	/// 设置头发分割隔帧检测（对上一帧结果做拷贝），目的是减少耗时。默认每帧检测一次. 最多每10帧检测一次. 开启隔帧检测后, 只能对拷贝出来的检测结果做后处理.
	ST_HUMAN_ACTION_PARAM_HAIR_PROCESS_INTERVAL = 19,
	ST_HUMAN_ACTION_PARAM_CAM_FOVX = 20,  // 摄像头x方向上的视场角，单位为度，3d手势点需要
	/// 设置是否根据肢体信息检测摄像头运动状态 (0: 不检测; 1: 检测. 默认检测肢体轮廓点时检测摄像头运动状态)
	ST_HUMAN_ACTION_PARAM_DETECT_CAMERA_MOTION_WITH_BODY = 21,
	/// 输出的multisegment结果中长边的长度.
	ST_HUMAN_ACTION_PARAM_MULTI_SEGMENT_MAX_SIZE = 22,
	/// 设置多类分割检测结果灰度图的方向是否需要旋转（0: 不旋转, 保持竖直; 1: 旋转, 方向和输入图片一致. 默认不旋转)
	ST_HUMAN_ACTION_PARAM_MULTI_SEGMENT_RESULT_ROTATE = 23
} st_human_action_type;

/// @brief 设置human_action参数
/// @param[in] handle 已初始化的human_action句柄
/// @param[in] type human_action参数关键字,例如ST_HUMAN_ACTION_PARAM_BACKGROUND_MAX_SIZE
/// @param[in] value 参数取值
/// @return 成功返回ST_OK,错误则返回错误码,错误码定义在st_mobile_common.h 中,如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_human_action_setparam(
    st_handle_t handle,
    st_human_action_type type,
    float value
);

/// @brief 镜像human_action检测结果. 隔帧检测时, 需要将检测结果拷贝出来再镜像
/// @param[in] image_width 检测human_action的图像的宽度(以像素为单位)
/// @param[in,out] p_human_action 需要镜像的human_action检测结果
ST_SDK_API void
st_mobile_human_action_mirror(
    int image_width,
    st_mobile_human_action_t *p_human_action
);
/// @brief 旋转human_action检测结果.
/// @param[in] image_width 检测human_action的图像的宽度(以像素为单位)
/// @param[in] image_height 检测human_action的图像的宽度(以像素为单位)
/// @param[in] orientation 顺时针旋转的角度
/// @param[in] b_rotate_image 是否旋转图片
/// @param[in,out] p_human_action 需要旋转的human_action检测结果
ST_SDK_API void
st_mobile_human_action_rotate(
	int image_width,
	int image_height,
	st_rotate_type orientation,
	bool b_rotate_image,
	st_mobile_human_action_t* p_human_action
);
/// @brief 对分割后的图像进行描边
/// @param[in] segment 分割结果二值图，前景像素为0，背景像素为255
/// @param[out] edge 描边后的灰度图,边缘值处于0-255之间,其余为0, 用户分配图像内存，大小与segment图像内存相同
/// @param[in] edge_width 需要描边的边缘宽度
/// @param[in] edge_blur 对edge进行滤波与否
ST_SDK_API
st_result_t
st_mobile_retrieve_human_edge(
const st_image_t* segment, st_image_t* edge, int edge_width, bool edge_blur
);
/// @brief 放大/缩小human_action检测结果.背景图像不缩放
/// @param[in] scale 缩放的尺度
/// @param[in,out] p_human_action 需要缩放的human_action检测结果
ST_SDK_API
void st_mobile_human_action_resize(
	float scale,
	st_mobile_human_action_t* p_human_action
);

typedef enum{
	// 脸部动作
	ST_MOBILE_EXPRESSION_EYE_BLINK = 1,  ///< 眨眼
	ST_MOBILE_EXPRESSION_MOUTH_AH = 2, ///< 嘴巴大张
	ST_MOBILE_EXPRESSION_HEAD_YAW = 3, ///< 摇头
	ST_MOBILE_EXPRESSION_HEAD_PITCH = 4,  ///< 点头
	ST_MOBILE_EXPRESSION_BROW_JUMP = 5,  ///< 挑眉
	// 手
	ST_MOBILE_EXPRESSION_HAND_OK = 9,  ///< OK手势
	ST_MOBILE_EXPRESSION_HAND_SCISSOR = 10,  ///< 剪刀手
	ST_MOBILE_EXPRESSION_HAND_GOOD = 11,  ///< 大拇哥
	ST_MOBILE_EXPRESSION_HAND_PALM = 12,  ///< 手掌
	ST_MOBILE_EXPRESSION_HAND_PISTOL = 13,  ///< 手枪手势
	ST_MOBILE_EXPRESSION_HAND_LOVE = 14,  ///< 爱心手势
	ST_MOBILE_EXPRESSION_HAND_HOLDUP = 15,  ///< 托手手势
	ST_MOBILE_EXPRESSION_HAND_CONGRATULATE = 17,  ///< 恭贺（抱拳）
	ST_MOBILE_EXPRESSION_HAND_FINGER_HEART = 18,  ///< 单手比爱心
	ST_MOBILE_EXPRESSION_HAND_FINGER_INDEX = 20,  ///< 食指指尖
	// 头状态
	ST_MOBILE_EXPRESSION_HEAD_NORMAL = 65, ///< 头正向
	ST_MOBILE_EXPRESSION_SIDE_FACE_LEFT = 66, ///< 头向左侧偏
	ST_MOBILE_EXPRESSION_SIDE_FACE_RIGHT = 67, ///< 头向右侧偏
	ST_MOBILE_EXPRESSION_TILTED_FACE_LEFT = 68, ///< 头向左侧倾斜
	ST_MOBILE_EXPRESSION_TILTED_FACE_RIGHT = 69, ///< 头向右侧倾斜
	ST_MOBILE_EXPRESSION_HEAD_RISE = 70, ///< 抬头
	ST_MOBILE_EXPRESSION_HEAD_LOWER = 71, ///< 低头
	// 眼状态
	ST_MOBILE_EXPRESSION_TWO_EYE_CLOSE = 85, ///< 两眼都闭
	ST_MOBILE_EXPRESSION_TWO_EYE_OPEN = 86, ///< 两眼都睁
	ST_MOBILE_EXPRESSION_LEFTEYE_OPEN_RIGHTEYE_CLOSE = 87, ///< 左眼睁右眼闭
	ST_MOBILE_EXPRESSION_LEFTEYE_CLOSE_RIGHTEYE_OPEN = 88, ///< 左眼闭右眼睁
	// 嘴状态
	ST_MOBILE_EXPRESSION_MOUTH_OPEN = 105, ///< 闭嘴
	ST_MOBILE_EXPRESSION_MOUTH_CLOSE = 106, ///< 张嘴
	ST_MOBILE_EXPRESSION_FACE_LIPS_UPWARD = 107, ///< 嘴角上扬
	ST_MOBILE_EXPRESSION_FACE_LIPS_POUTED = 108, ///< 嘟嘴
	ST_MOBILE_EXPRESSION_FACE_LIPS_CURL_LEFT = 109,   ///< 左撇嘴
	ST_MOBILE_EXPRESSION_FACE_LIPS_CURL_RIGHT = 110,   ///< 右撇嘴

	ST_MOBILE_EXPRESSION_COUNT = 128,

	// 以下只能用于set_expression接口
	ST_MOBILE_EXPRESSION_FACE_ALL = 257,   ///< 所有脸部动作
	ST_MOBILE_EXPRESSION_HAND_ALL = 258   ///< 所有手部动作
}ST_MOBILE_EXPRESSION;

/// @brief 根据human_action结果获取expression动作信息. 在st_mobile_human_action_detect之后调用
/// @param[in] human_action 输入human_action_detect结果
/// @param[in] orientation 人脸方向
/// @param[in] b_mirror 是否需要镜像expression结果
/// @param[out] expressions_result 用户分配内存，返回检测动作结果数组,动作有效true，无效false
/// @return 成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
ST_SDK_API st_result_t
st_mobile_get_expression(
st_mobile_human_action_t* human_action,
st_rotate_type orientation, bool b_mirror,
bool expressions_result[ST_MOBILE_EXPRESSION_COUNT]
);

/// @brief 设置expression动作阈值
/// @param[in] detect_expression 需要设置阈值的检测动作. 目前仅支持face相关的阈值，可以配置为ST_MOBILE_EXPRESSION_FACE_LIPS_POUTE等
/// @param[in] threshold 阈值数值[0,1]，阈值越大，误检越少，漏检越多
ST_SDK_API st_result_t
st_mobile_set_expression_threshold(
ST_MOBILE_EXPRESSION detect_expression,
float threshold
);
#endif  // INCLUDE_STMOBILE_ST_MOBILE_HUMAN_ACTION_H_
