package com.sensetime.stmobile;

import com.sensetime.stmobile.model.STHumanAction;

/**
 * 美颜图片接口
 */
public class STBeautifyNative {
    static {
        System.loadLibrary("st_mobile");
        System.loadLibrary("stmobile_jni");
    }

    //供JNI使用，应用不需要关注
    private long nativeHandle;

    /**
     * 创建美颜句柄
     *
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public native int createInstance();

    /**
     * @param type  要设置美颜参数的类型 STBeautyParamsType.ST_BEAUTIFY_CONTRAST_STRENGTH
     * @param value 设置的值
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public native int setParam(int type, float value);

    /**
     * 对图像buffer做美颜处理，需要在OpengGL环境中调用（运行在OpenGL线程中）
     *
     * @param pInputImage  ［in]输入图片的数据数组
     * @param inFormat     输入图片的类型,支持NV21,BGR,BGRA,NV12,RGBA等。比如STCommon.ST_PIX_FMT_BGRA8888
     * @param outputWidth  输出图片的宽度(以像素为单位)
     * @param outputHeight 输出图片的高度(以像素为单位)
     * @param rotate   将图像中的人转正，图像需要顺时针旋转的角度。当前，只会影响美体
     * @param pOutImage    [out]输出图像数据buffer，需要用户自己创建
     * @param humanActionInput 输入需要美颜的人脸106点数组；如果为NULL，不执行大眼瘦脸
     * @param outFormat    输出图片的类型,支持NV21,BGR,BGRA,NV12,RGBA等。比如STCommon.ST_PIX_FMT_NV21
     * @param humanActionOutput 输出美颜后的人脸106点数组，需要由用户分配内存，必须与输入的人脸106点数组大小相同；如果为NULL，不输出美颜后的人脸106点数组
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public native int processBufferInGLContext(byte[] pInputImage, int inFormat, int outputWidth, int outputHeight, int rotate, STHumanAction humanActionInput, byte[] pOutImage, int outFormat, STHumanAction humanActionOutput);

    /**
     * 对图像做美颜处理，此接口针对不在OpenGL环境中(不在opengl线程中)执行函数的用户
     *
     * @param pInputImage  [in]输入图片的数据数组
     * @param inFormat     输入图片的类型,支持NV21,BGR,BGRA,NV12,RGB等,比如STCommon.ST_PIX_FMT_BGRA8888
     * @param outputWidth  输出图片的宽度(以像素为单位)
     * @param outputHeight 输出图片的高度(以像素为单位)
     * @param rotate   将图像中的人转正，图像需要顺时针旋转的角度。当前，只会影响美体
     * @param humanActionInput 输入需要美颜的人脸106点数组；如果为NULL，不执行大眼瘦脸
     * @param pOutImage    [out]输出图像数据buffer，用户需要自己创建
     * @param outFormat    输出图片的类型,支持NV21,BGR,BGRA,NV12,RGBA等。比如STCommon.ST_PIX_FMT_NV21
     * @param humanActionOutput 输出美颜后的人脸106点数组，需要由用户分配内存，必须与输入的人脸106点数组大小相同；如果为NULL，不输出美颜后的人脸106点数组
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public native int processBufferNotInGLContext(byte[] pInputImage, int inFormat, int outputWidth, int outputHeight, int rotate, STHumanAction humanActionInput, byte[] pOutImage, int outFormat, STHumanAction humanActionOutput);

    /**
     * 对OpenGL ES中的纹理进行美颜处理，需要运行在Opengl环境中
     *
     * @param textureIn    待处理的纹理id, 仅支持RGBA纹理
     * @param outputWidth  输出图片的宽度(以像素为单位)
     * @param outputHeight 输出图片的高度(以像素为单位)
     * @param rotate   将图像中的人转正，图像需要顺时针旋转的角度。当前，只会影响美体
     * @param textureOut   处理后的纹理id，仅支持RGBA处理。用户需要自己创建该纹理ID，并输入
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public native int processTexture(int textureIn, int outputWidth, int outputHeight, int rotate, STHumanAction humanActionInput, int textureOut, STHumanAction humanActionOutput);


    /**
     * 对OpenGL ES中的纹理进行美颜处理，需要运行在Opengl环境中.可以输出buffer及人脸信息
     *
     * @param textureIn     待处理的纹理id, 仅支持RGBA纹理
     * @param outputWidth   输入纹理的宽度(以像素为单位)
     * @param outputHeight  输入纹理的高度(以像素为单位)
     * @param rotate   将图像中的人转正，图像需要顺时针旋转的角度。当前，只会影响美体
     * @param humanActionInput  输入需要美颜的人脸106点数组；如果为NULL，不执行大眼瘦脸
     * @param textureOut    处理后输出的纹理id，仅支持RGBA处理
     * @param outputBuf     输出图像数据数组,需要用户分配内存,如果是null, 不输出buffer
     * @param format        输出图片的类型,支持NV21,BGR,BGRA,NV12,RGBA格式。比如STCommon.ST_PIX_FMT_NV21
     * @param humanActionOutput 输出美颜后的人脸106点数组，需要由用户分配内存，必须与输入的人脸106点数组大小相同；如果为NULL，不输出美颜后的人脸106点数组
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public native int processTextureAndOutputBuffer(int textureIn, int outputWidth, int outputHeight, int rotate, STHumanAction humanActionInput, int textureOut, byte[] outputBuf, int format, STHumanAction humanActionOutput);

    /**
     * 释放instance，必须在opengl环境中运行
     */
    public native void destroyBeautify();
}
