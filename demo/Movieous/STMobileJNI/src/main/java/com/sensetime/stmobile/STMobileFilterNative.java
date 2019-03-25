package com.sensetime.stmobile;

/**
 * 图像滤镜
 */

public class STMobileFilterNative {

    //供JNI使用，应用不需要关注
    private long nativeHandle;

    /**
     * 创建滤镜句柄
     *
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int createInstance();


    /**
     * 设置滤镜风格
     *
     * @param styleModelPath 滤镜风格模型路径
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int setStyle(String styleModelPath);


    /**
     * 设置滤镜参数
     *
     * @param type  参考STFilterParamsType
     * @param value 参数值
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int setParam(int type, float value);


    /**
     * 对图像做滤镜处理
     *
     * @param inputImage  输入图像数据数组
     * @param inFormat    输入图片的类型,支持NV21,BGR,BGRA,NV12,RGB等,比如STCommon.ST_PIX_FMT_BGRA8888
     * @param imageWidth  输入图像的宽度(以像素为单位)
     * @param imageHeight 输入图像的高度(以像素为单位)
     * @param outImage    输出图像数据数组
     * @param outFormat   输出图片的类型,支持NV21,BGR,BGRA,NV12,RGBA等。比如STCommon.ST_PIX_FMT_NV21
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int process(byte[] inputImage, int inFormat, int imageWidth, int imageHeight,
                              byte[] outImage, int outFormat);


    /**
     * 销毁实例
     */
    public native void destroyInstance();
}
