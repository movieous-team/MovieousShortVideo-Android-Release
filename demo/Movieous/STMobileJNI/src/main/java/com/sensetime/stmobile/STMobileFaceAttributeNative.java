package com.sensetime.stmobile;

import android.content.res.AssetManager;

import com.sensetime.stmobile.model.STFaceAttribute;
import com.sensetime.stmobile.model.STMobile106;


/**
 * 人脸属性JNI定义
 */
public class STMobileFaceAttributeNative {
    static {
        System.loadLibrary("st_mobile");
        System.loadLibrary("stmobile_jni");
    }


    //供底层使用，不需要关注
    private long nativeHandle;

    /**
     * 人脸属性检测
     *
     * @param image      用于检测的图像数据
     * @param format     用于检测的图像数据的像素格式, 支持所有彩色图像格式，推荐STCommon.ST_PIX_FMT_BGR888，
     *                   不建议使用STCommon.ST_PIX_FMT_GRAY8结果不准确
     * @param width      用于检测的图像的宽度(以像素为单位)
     * @param height     用于检测的图像的高度(以像素为单位)
     * @param mobile106  输入待处理的人脸信息，需要包括关键点信息
     * @param attributes [out]输出人脸属性
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int detect(byte[] image, int format, int width, int height, STMobile106[] mobile106, STFaceAttribute[] attributes);

    /**
     * 创建实例
     *
     * @param modelpath 模型路径
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int createInstance(String modelpath);

    /**
     * 从assets资源文件创建实例
     *
     * @param assetModelpath 模型路径
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int createInstanceFromAssetFile(String assetModelpath, AssetManager assetManager);

    /**
     * 释放实例
     */
    public native void destroyInstance();
}
