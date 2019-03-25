package com.sensetime.stmobile;

/**
 * 定义图像旋转角度
 */
public class STRotateType {
    //因为只有正向的人脸能够被识别，所以需要输入图像中人脸需要旋转的角度，
    public final static int ST_CLOCKWISE_ROTATE_0 = 0;  //< 图像不需要转向
    public final static int ST_CLOCKWISE_ROTATE_90 = 1;  //< 图像需要顺时针旋转90度
    public final static int ST_CLOCKWISE_ROTATE_180 = 2; //< 图像需要顺时针旋转180度
    public final static int ST_CLOCKWISE_ROTATE_270 = 3; //< 图像需要顺时针旋转270度
}
