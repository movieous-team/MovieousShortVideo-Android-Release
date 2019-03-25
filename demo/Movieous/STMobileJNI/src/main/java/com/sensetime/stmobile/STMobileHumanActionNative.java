package com.sensetime.stmobile;

import android.content.res.AssetManager;

import com.sensetime.stmobile.model.STHumanAction;

/**
 * 人脸关键点及人脸行为识别
 */
public class STMobileHumanActionNative {
    private final static String TAG = STMobileHumanActionNative.class.getSimpleName();

    //支持的人脸行为配置
    public final static long ST_MOBILE_FACE_DETECT = 0x00000001;    ///<  人脸检测

    //人脸动作
    public final static long ST_MOBILE_EYE_BLINK = 0x00000002;    ///<  眨眼
    public final static long ST_MOBILE_MOUTH_AH = 0x00000004;    ///<  嘴巴大张
    public final static long ST_MOBILE_HEAD_YAW = 0x00000008;    ///<  摇头
    public final static long ST_MOBILE_HEAD_PITCH = 0x00000010;    ///<  点头
    public final static long ST_MOBILE_BROW_JUMP = 0x00000020;    ///<  眉毛挑动

    //手势动作
    public final static long ST_MOBILE_HAND_GOOD = 0x00000800;    ///<  大拇哥 2048
    public final static long ST_MOBILE_HAND_PALM = 0x00001000;    ///<  手掌 4096
    public final static long ST_MOBILE_HAND_LOVE = 0x00004000;    ///<  爱心 16384
    public final static long ST_MOBILE_HAND_HOLDUP = 0x00008000;    ///<  托手 32768
    public final static long ST_MOBILE_HAND_CONGRATULATE = 0x00020000;    ///<  恭贺（抱拳） 131072

    public final static long ST_MOBILE_HAND_FINGER_HEART = 0x00040000;    ///<  单手比爱心 262144
    public final static long ST_MOBILE_HAND_OK = 0x00000200;  ///< OK手势
    public final static long ST_MOBILE_HAND_SCISSOR = 0x00000400;  ///< 剪刀手
    public final static long ST_MOBILE_HAND_PISTOL = 0x00002000;  ///< 手枪手势
    public final static long ST_MOBILE_HAND_FINGER_INDEX = 0x00100000;  ///< 食指指尖

    public final static long ST_MOBILE_HAND_FIST = 0x00200000;  ///< 拳头手势
    public final static long ST_MOBILE_HAND_666 = 0x00400000;  ///< 666
    public final static long ST_MOBILE_HAND_BLESS = 0x00800000;  ///< 双手合十
    public final static long ST_MOBILE_HAND_ILOVEYOU = 0x010000000000L;  ///< 手势ILoveYou

    public final static long ST_MOBILE_BODY_KEYPOINTS = 0x08000000;  ///< 检测人体关键点
    public final static long ST_MOBILE_SEG_BACKGROUND = 0x00010000;    ///<  前景背景分割 65536

    public final static long ST_MOBILE_DETECT_EXTRA_FACE_POINTS = 0x01000000;  ///< 人脸240关键点
    public final static long ST_MOBILE_DETECT_EYEBALL_CENTER = 0x02000000;  ///< 眼球中心点
    public final static long ST_MOBILE_DETECT_EYEBALL_CONTOUR = 0x04000000;  ///< 眼球轮廓点

    // 创建人体行为检测句柄配置选项
    /// 支持的检测类型
    public final static int ST_MOBILE_ENABLE_FACE_DETECT = 0x00000040;            ///< 检测人脸
    public final static int ST_MOBILE_ENABLE_HAND_DETECT = 0x00000080;            ///< 检测手势
    public final static int ST_MOBILE_ENABLE_SEGMENT_DETECT = 0x00000100;         ///< 检测背景分割
    public final static int ST_MOBILE_ENABLE_FACE_EXTRA_DETECT = 0x00000200;      ///< 检测人脸240
    public final static int ST_MOBILE_ENABLE_EYEBALL_CENTER_DETECT = 0x00000400;  ///< 检测眼球中心
    public final static int ST_MOBILE_ENABLE_EYEBALL_CONTOUR_DETECT = 0x00000800; ///< 检测眼球轮廓
    public final static int ST_MOBILE_ENABLE_BODY_KEYPOINTS = 0x00001000;         ///< 检测人体关键点及动作
    public final static int ST_MOBILE_ENABLE_BODY_CONTOUR = 0x00004000;           ///< 检测人体轮廓点
    public final static int ST_MOBILE_ENABLE_HAIR_SEGMENT = 0x00008000;           ///< 检测头发分割

    public final static long ST_MOBILE_BODY_ACTION1 = (long)1 << 32;    /// 龙拳
    public final static long ST_MOBILE_BODY_ACTION2 = (long)1 << 33;    /// 一休
    public final static long ST_MOBILE_BODY_ACTION3 = (long)1 << 34;    /// 摊手
    public final static long ST_MOBILE_BODY_ACTION4 = (long)1 << 35;    /// 蜘蛛侠 暂时不支持
    public final static long ST_MOBILE_BODY_ACTION5 = (long)1 << 36;    /// 动感超人 暂时不支持

    public final static int ST_MOBILE_FACE_DETECT_FULL = 0x0000003F;  ///< 检测所有脸部动作
    public final static int ST_MOBILE_HAND_DETECT_FULL = 0x001EFF00;  ///< 检测所有手势
    public final static int ST_MOBILE_BODY_DETECT_FULL = 0x018000000; ///< 检测肢体关键点和肢体轮廓点
    /// 检测模式
    public final static int ST_MOBILE_DETECT_MODE_VIDEO = 0x00020000;  ///< 视频检测
    public final static int ST_MOBILE_DETECT_MODE_IMAGE = 0x00040000;  ///< 图片检测


    //human action默认配置,
    //全部检测,不建议使用,耗时、cpu占用率会变高,建议根据需求检测相关动作
    public final static long ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_DETECT = ST_MOBILE_FACE_DETECT | ST_MOBILE_EYE_BLINK |
            ST_MOBILE_MOUTH_AH | ST_MOBILE_HEAD_YAW |
            ST_MOBILE_HEAD_PITCH | ST_MOBILE_BROW_JUMP |
            ST_MOBILE_HAND_GOOD | ST_MOBILE_HAND_PALM |
            ST_MOBILE_HAND_LOVE | ST_MOBILE_HAND_HOLDUP |
            ST_MOBILE_HAND_CONGRATULATE | ST_MOBILE_HAND_FINGER_HEART |
            ST_MOBILE_SEG_BACKGROUND| ST_MOBILE_DETECT_EYEBALL_CENTER|ST_MOBILE_DETECT_EYEBALL_CONTOUR;  ///< 全部检测

    //创建时默认的配置参数
    // 使用多线程，可最大限度的提高速度，并减少卡顿,根据可根据具体需求修改默认配置
    // 对视频进行检测推荐使用多线程检
    public final static int ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO = STCommon.ST_MOBILE_TRACKING_MULTI_THREAD
            | STCommon.ST_MOBILE_TRACKING_ENABLE_DEBOUNCE | STCommon.ST_MOBILE_TRACKING_ENABLE_FACE_ACTION
            | ST_MOBILE_ENABLE_FACE_DETECT | ST_MOBILE_ENABLE_HAND_DETECT
            | ST_MOBILE_ENABLE_SEGMENT_DETECT | ST_MOBILE_DETECT_MODE_VIDEO;

    //对图片进行检测默认配置
    //对图片进行检测只能使用单线程
    public final static int ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_IMAGE = STCommon.ST_MOBILE_TRACKING_SINGLE_THREAD| ST_MOBILE_ENABLE_FACE_DETECT
            | ST_MOBILE_ENABLE_SEGMENT_DETECT| ST_MOBILE_ENABLE_HAND_DETECT | ST_MOBILE_DETECT_MODE_IMAGE;

    public enum STMobileExpression {
        // 脸部动作
        ST_MOBILE_EXPRESSION_EYE_BLINK (1),  ///< 眨眼
        ST_MOBILE_EXPRESSION_MOUTH_AH (2), ///< 嘴巴大张
        ST_MOBILE_EXPRESSION_HEAD_YAW (3), ///< 摇头
        ST_MOBILE_EXPRESSION_HEAD_PITCH (4),  ///< 点头
        ST_MOBILE_EXPRESSION_BROW_JUMP (5),  ///< 挑眉
        // 手
        ST_MOBILE_EXPRESSION_HAND_OK (9),  ///< OK手势
        ST_MOBILE_EXPRESSION_HAND_SCISSOR (10),  ///< 剪刀手
        ST_MOBILE_EXPRESSION_HAND_GOOD (11),  ///< 大拇哥
        ST_MOBILE_EXPRESSION_HAND_PALM (12),  ///< 手掌
        ST_MOBILE_EXPRESSION_HAND_PISTOL (13),  ///< 手枪手势
        ST_MOBILE_EXPRESSION_HAND_LOVE (14),  ///< 爱心手势
        ST_MOBILE_EXPRESSION_HAND_HOLDUP (15),  ///< 托手手势
        ST_MOBILE_EXPRESSION_HAND_CONGRATULATE (17),  ///< 恭贺（抱拳）
        ST_MOBILE_EXPRESSION_HAND_FINGER_HEART (18),  ///< 单手比爱心
        ST_MOBILE_EXPRESSION_HAND_FINGER_INDEX (20),  ///< 食指指尖
        // 头状态
        ST_MOBILE_EXPRESSION_HEAD_NORMAL (65), ///< 头正向
        ST_MOBILE_EXPRESSION_SIDE_FACE_LEFT (66), ///< 头向左侧偏
        ST_MOBILE_EXPRESSION_SIDE_FACE_RIGHT (67), ///< 头向右侧偏
        ST_MOBILE_EXPRESSION_TILTED_FACE_LEFT (68), ///< 头向左侧倾斜
        ST_MOBILE_EXPRESSION_TILTED_FACE_RIGHT (69), ///< 头向右侧倾斜
        ST_MOBILE_EXPRESSION_HEAD_RISE (70), ///< 抬头
        ST_MOBILE_EXPRESSION_HEAD_LOWER (71), ///< 低头
        // 眼状态
        ST_MOBILE_EXPRESSION_TWO_EYE_CLOSE (85), ///< 两眼都闭
        ST_MOBILE_EXPRESSION_TWO_EYE_OPEN (86), ///< 两眼都睁
        ST_MOBILE_EXPRESSION_LEFTEYE_OPEN_RIGHTEYE_CLOSE (87), ///< 左眼睁右眼闭
        ST_MOBILE_EXPRESSION_LEFTEYE_CLOSE_RIGHTEYE_OPEN (88), ///< 左眼闭右眼睁
        // 嘴状态
        ST_MOBILE_EXPRESSION_MOUTH_OPEN (105), ///< 闭嘴
        ST_MOBILE_EXPRESSION_MOUTH_CLOSE (106), ///< 张嘴
        ST_MOBILE_EXPRESSION_FACE_LIPS_UPWARD (107), ///< 嘴角上扬
        ST_MOBILE_EXPRESSION_FACE_LIPS_POUTED (108), ///< 嘟嘴
        ST_MOBILE_EXPRESSION_FACE_LIPS_CURL_LEFT (109),   ///< 左撇嘴
        ST_MOBILE_EXPRESSION_FACE_LIPS_CURL_RIGHT (110),   ///< 右撇嘴

        ST_MOBILE_EXPRESSION_COUNT (128),

        // 以下只能用于set_expression接口
        ST_MOBILE_EXPRESSION_FACE_ALL (257),   ///< 所有脸部动作
        ST_MOBILE_EXPRESSION_HAND_ALL (258);   ///< 所有手部动作

        private final int expressionCode;

        STMobileExpression(int expressionCode) {
            this.expressionCode = expressionCode;
        }

        public int getExpressionCode() {
            return expressionCode;
        }
    };

    static {
        System.loadLibrary("st_mobile");
        System.loadLibrary("stmobile_jni");
    }

    //供JNI使用，应用不需要关注
    private long nativeHumanActionHandle;

    /**
     * 创建实例
     *
     * @param modelpath 模型文件的,例如models/face_track_2.x.x.model
     * @param config    配置选项，比如STCommon.ST_MOBILE_TRACKING_RESIZE_IMG_320W。建议输入ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int createInstance(String modelpath, int config);

    /**
     * 从资源文件夹创建实例
     *
     * @param assetModelpath 模型文件的路径
     * @param config    配置选项，比如STCommon.ST_MOBILE_TRACKING_RESIZE_IMG_320W。建议输入ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO
     * @param assetManager 资源文件管理器
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int createInstanceFromAssetFile(String assetModelpath, int config, AssetManager assetManager);

    /**
     * 通过子模型创建人体行为检测句柄, st_mobile_human_action_create和st_mobile_human_action_create_with_sub_models只能调一个
     *
     * @param modelPaths 模型文件路径指针数组. 根据加载的子模型确定支持检测的类型. 如果包含相同的子模型, 后面的会覆盖前面的.
     * @param modelCount 模型文件数目
     * @param config     detect_mode 设置检测模式. 检测视频时设置为ST_MOBILE_DETECT_MODE_VIDEO, 检测图片时设置为ST_MOBILE_DETECT_MODE_IMAGE
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int createInstanceWithSubModels(String[] modelPaths, int modelCount, int config);

    /**
     * 添加子模型.
     *
     * @param modelPath 模型文件的路径. 后添加的会覆盖之前添加的同类子模型。加载模型耗时较长, 建议在初始化创建句柄时就加载模型
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public native int addSubModel(String modelPath);

    /**
     * 从资源文件夹添加子模型.
     *
     * @param assetModelpath 模型文件的路径. 后添加的会覆盖之前添加的同类子模型。加载模型耗时较长, 建议在初始化创建句柄时就加载模型
     * @param assetManager 资源文件管理器
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int addSubModelFromAssetFile(String assetModelpath, AssetManager assetManager);

    /**
     * 删除已添加的子模型
     *
     * @param config 要删除的子模型对应的config，config为创建人体行为检测句柄配置选项（例如：ST_MOBILE_ENABLE_BODY_KEYPOINTS）
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int removeSubModelByConfig(int config);

    /**
     * @param type  要设置Human Action参数的类型
     * @param value 设置的值
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public native int setParam(int type, float value);

    /**
     * 检测人脸106点及人脸行为
     *
     * @param imgData       用于检测的图像数据
     * @param imageFormat   用于检测的图像数据的像素格式,比如STCommon.ST_PIX_FMT_NV12。能够独立提取灰度通道的图像格式处理速度较快，
     *                      比如ST_PIX_FMT_GRAY8，ST_PIX_FMT_YUV420P，ST_PIX_FMT_NV12，ST_PIX_FMT_NV21
     * @param detect_config 检测选项，代表当前需要检测哪些动作，例如ST_MOBILE_EYE_BLINK|ST_MOBILE_MOUTH_AH表示当前帧只检测眨眼和张嘴
     * @param orientation   图像中人脸的方向,例如STRotateType.ST_CLOCKWISE_ROTATE_0
     * @param imageWidth    用于检测的图像的宽度(以像素为单位)
     * @param imageHeight   用于检测的图像的高度(以像素为单位)
     * @return 人脸信息
     */
    public native STHumanAction humanActionDetect(byte[] imgData, int imageFormat, long detect_config,
                                                  int orientation, int imageWidth, int imageHeight);

    /**
     * 重置，清除所有缓存信息. 视频模式下会在handle中保存一些状态，当切换分辨率、切换前后摄像头、切换后台、两帧图像差别较大时需要调用reset
     */
    public native void reset();

    /**
     * 释放instance
     */
    public native void destroyInstance();

    /**
     * 根据human_action结果获取expression动作信息. 在st_mobile_human_action_detect之后调用. 默认各动作阈值为0.5
     *
     * @param humanAction  计算表情的STHumanAction对象
     * @param orientation  图像中人脸的方向,例如STRotateType.ST_CLOCKWISE_ROTATE_90（人脸方向为90度）
     * @param needMirror   是否需要镜像expression结果
     * @return 返回expressions_result检测动作结果数组,动作有效true，无效false
     */
    public static native boolean[] getExpression(STHumanAction humanAction, int orientation, boolean needMirror);

    /**
     * 设置expression动作阈值
     *
     * @param detectExpression 需要设置阈值的检测动作. 目前仅支持face相关的阈值，可以配置为ST_MOBILE_EXPRESSION_FACE_LIPS_POUTE等
     * @param threshold 阈值数值[0,1]，默认阈值为0.5, 阈值越大，误检越少，漏检越多
     */
    public static native void setExpressionThreshold(int detectExpression, float threshold);
}
