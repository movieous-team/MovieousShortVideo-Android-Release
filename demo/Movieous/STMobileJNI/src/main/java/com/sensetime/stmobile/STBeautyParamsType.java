package com.sensetime.stmobile;

/**
 * 定义可以美颜的类型
 */
public class STBeautyParamsType {
    public final static int ST_BEAUTIFY_REDDEN_STRENGTH = 1; // 红润强度, [0,1.0], 0.0不做红润
    public final static int ST_BEAUTIFY_SMOOTH_MODE = 2;     /// 磨皮模式, 默认值1.0, 1.0表示对全图磨皮, 0.0表示只对人脸磨皮
    public final static int ST_BEAUTIFY_SMOOTH_STRENGTH = 3; // 磨皮强度, [0,1.0], 0.0不做磨皮
    public final static int ST_BEAUTIFY_WHITEN_STRENGTH = 4;    /// 美白强度, [0,1.0], 0.0不做美白
    public final static int ST_BEAUTIFY_ENLARGE_EYE_RATIO = 5;    /// 大眼比例, [0,1.0], 0.0不做大眼效果
    public final static int ST_BEAUTIFY_SHRINK_FACE_RATIO = 6;    /// 瘦脸比例, [0,1.0], 0.0不做瘦脸效果
    public final static int ST_BEAUTIFY_SHRINK_JAW_RATIO = 7;     /// 小脸比例, [0,1.0], 0.0不做小脸效果
    public final static int ST_BEAUTIFY_CONSTRACT_STRENGTH = 8; // 对比度
    public final static int ST_BEAUTIFY_SATURATION_STRENGTH = 9; // 饱和度
    public final static int ST_BEAUTIFY_DEHIGHLIGHT_STRENGTH = 10; // 去高光强度, [0,1.0], 默认值1, 0.0不做高光
    public final static int ST_BEAUTIFY_NARROW_FACE_STRENGTH = 11; // 窄脸强度, [0,1.0], 默认值0, 0.0不做窄脸

    public final static int ST_BEAUTIFY_3D_NARROW_NOSE_RATIO = 20;    // 瘦鼻比例，[0, 1.0], 默认值为0.0，0.0不做瘦鼻
    public final static int ST_BEAUTIFY_3D_NOSE_LENGTH_RATIO = 21;     // 鼻子长短比例，[-1, 1], 默认值为0.0, [-1, 0]为短鼻，[0, 1]为长鼻
    public final static int ST_BEAUTIFY_3D_CHIN_LENGTH_RATIO = 22;     // 下巴长短比例，[-1, 1], 默认值为0.0，[-1, 0]为短下巴，[0, 1]为长下巴
    public final static int ST_BEAUTIFY_3D_MOUTH_SIZE_RATIO = 23;      // 嘴型比例，[-1, 1]，默认值为0.0，[-1, 0]为放大嘴巴，[0, 1]为缩小嘴巴
    public final static int ST_BEAUTIFY_3D_PHILTRUM_LENGTH_RATIO = 24; // 人中长短比例，[-1, 1], 默认值为0.0，[-1, 0]为短人中，[0, 1]为长人中
    public final static int ST_BEAUTIFY_3D_HAIRLINE_HEIGHT_RATIO = 25;   // 发际线高低比例，[-1, 1], 默认值为0.0，[-1, 0]为高发际线，[0, 1]为低发际线
    public final static int ST_BEAUTIFY_3D_THIN_FACE_SHAPE_RATIO = 26;   ///  瘦脸型比例， [0,1.0], 默认值0.0, 0.0不做瘦脸型效果
}