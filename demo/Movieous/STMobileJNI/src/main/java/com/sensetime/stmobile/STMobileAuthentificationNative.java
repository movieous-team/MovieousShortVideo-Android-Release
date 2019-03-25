package com.sensetime.stmobile;

import android.content.Context;

/**
 * Licence验证JNI定义
 */

public class STMobileAuthentificationNative {


    static {
        System.loadLibrary("st_mobile");
        System.loadLibrary("stmobile_jni");
    }

    /**
     * 根据授权文件生成激活码, 在使用新的license文件时使用
     *
     * @param context       上下文环境
     * @param licensePath   license文件路径
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public static native String generateActiveCode(Context context, String licensePath);

    /**
     * 检查激活码, 必须在所有接口之前调用
     *
     * @param context        上下文环境
     * @param licensePath    license文件路径
     * @param activationCode 当前设备的激活码
     * @param codeSize       激活码长度
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public static native int checkActiveCode(Context context, String licensePath, String activationCode, int codeSize);


    /**
     * 根据授权文件缓存生成激活码, 在使用新的license文件时调用
     *
     * @param context       上下文环境
     * @param licenseBuffer license文件缓存
     * @param licenseSize   license文件缓存大小
     * @return 返回当前设备的激活码
     */
    public static native String generateActiveCodeFromBuffer(Context context, String licenseBuffer, int licenseSize);

    /**
     * 检查激活码, 必须在所有接口之前调用
     *
     * @param context        上下文环境
     * @param licenseBuffer  license文件缓存
     * @param licenseSize    license文件缓存大小
     * @param activationCode 当前设备的激活码
     * @param codeSize       当前设备的激活码大小
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public static native int checkActiveCodeFromBuffer(Context context, String licenseBuffer, int licenseSize, String activationCode, int codeSize);
    /**
     * 根据授权文件生成激活码, 在使用新的license文件时使用
     *
     * @param context       上下文环境
     * @param licensePath   license文件路径
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public static native String generateActiveCodeOnline(Context context, String licensePath);

    /**
     * 根据授权文件缓存生成激活码, 在使用新的license文件时调用
     *
     * @param context       上下文环境
     * @param licenseBuffer license文件缓存
     * @param licenseSize   license文件缓存大小
     * @return 返回当前设备的激活码
     */
    public static native String generateActiveCodeFromBufferOnline(Context context, String licenseBuffer, int licenseSize);

}
