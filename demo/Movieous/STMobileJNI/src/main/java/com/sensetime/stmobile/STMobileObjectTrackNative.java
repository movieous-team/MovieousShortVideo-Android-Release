package com.sensetime.stmobile;

import com.sensetime.stmobile.model.STRect;

/**
 * Created by sensetime on 17-6-5.
 */

public class STMobileObjectTrackNative {

    //供JNI使用，应用不需要关注
    private long objectTrackNativeHandle;

    /**
     * 创建通用物体跟踪句柄
     *
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int createInstance();

    /**
     * 设置跟踪目标的矩形
     *
     * @param inputImage  输入图像数据数组
     * @param inFormat    输入图片的类型,支持NV21,BGR,BGRA,NV12,RGB等,比如STCommon.ST_PIX_FMT_BGRA8888
     * @param imageWidth  输入图像的宽度(以像素为单位)
     * @param imageHeight 输入图像的高度(以像素为单位)
     * @param rect        设置需要跟踪区域矩形框
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int setTarget(byte[] inputImage, int inFormat, int imageWidth, int imageHeight, STRect rect);

    /**
     * 对连续视频帧中的目标进行实时快速跟踪
     *
     * @param inputImage  输入图像数据数组
     * @param inFormat    输入图片的类型,支持NV21,BGR,BGRA,NV12,RGB等,比如STCommon.ST_PIX_FMT_BGRA8888
     * @param imageWidth  输入图像的宽度(以像素为单位)
     * @param imageHeight 输入图像的高度(以像素为单位)
     * @param score       输出目标区域的置信度
     * @return 输出指定的目标矩形框,输出实际跟踪的矩形框.目前只能跟踪2^n正方形矩形
     */
    public native STRect objectTrack(byte[] inputImage, int inFormat, int imageWidth, int imageHeight, float[] score);

    /**
     * 重置通用物体跟踪句柄. 清空缓存,删除跟踪目标.
     */
    public native void reset();

    /**
     * 销毁实例
     */
    public native void destroyInstance();
}
