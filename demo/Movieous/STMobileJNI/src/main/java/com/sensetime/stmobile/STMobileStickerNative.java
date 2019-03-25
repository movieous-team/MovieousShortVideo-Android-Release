package com.sensetime.stmobile;

import android.content.Context;
import android.content.res.AssetManager;

import com.sensetime.stmobile.model.STCondition;
import com.sensetime.stmobile.model.STHumanAction;
import com.sensetime.stmobile.model.STStickerInputParams;
import com.sensetime.stmobile.model.STTransParam;
import com.sensetime.stmobile.sticker_module_types.STModuleInfo;

/**
 * 贴纸渲染JNI类定义
 */
public class STMobileStickerNative {

    //定义trigger action
    public final static int ST_MOBILE_EYE_BLINK = 0x00000002;    ///<  眨眼
    public final static int ST_MOBILE_MOUTH_AH = 0x00000004;    ///<  嘴巴大张
    public final static int ST_MOBILE_HEAD_YAW = 0x00000008;    ///<  摇头
    public final static int ST_MOBILE_HEAD_PITCH = 0x00000010;    ///<  点头
    public final static int ST_MOBILE_BROW_JUMP = 0x00000020;    ///<  眉毛挑动

    public final static int ST_INPUT_PARAM_NONE = 0x0;          ///< 无需传感器
    public final static int ST_INPUT_PARAM_CAMERA_QUATERNION = 0x1;      ///< 手机朝向传感器

    enum RenderStatus {
        ST_MATERIAL_BEGIN_RENDER(0), // 开始渲染子素材
        ST_MATERIAL_RENDERING(1),  // 子素材渲染中
        ST_MATERIAL_NO_RENDERING(2); // 子素材不再渲染

        private final int status;

        private RenderStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public static RenderStatus fromStatus(int status) {
            for (RenderStatus type : RenderStatus.values()) {
                if (type.getStatus() == status) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * 定义素材处理callback
     */
    public interface ItemCallback {
        /**
         * @param materialName 子素材名
         * @param status       当前子素材渲染的状态 RenderStatus
         */
        void processTextureCallback(String materialName, RenderStatus status);
    }

    private final static String TAG = STMobileStickerNative.class.getSimpleName();
    private static ItemCallback mCallback;

    /**
     * 设置要监听的素材处理callback
     *
     * @param callback 素材处理callback
     */
    public static void setCallback(ItemCallback callback) {
        mCallback = callback;
    }

    /**
     * JNI处理素材时，会回调该函数。
     */
    public static void item_callback(String materialName, int status) {
        if (mCallback != null) {
            mCallback.processTextureCallback(materialName, RenderStatus.fromStatus(status));
        }
    }

    static {
        System.loadLibrary("st_mobile");
        System.loadLibrary("stmobile_jni");
    }

    private long nativeStickerHandle;

    private STSoundPlay mSoundPlay;

    /**
     * 创建贴纸实例
     *
     * @param context 上下文环境，可为null，null时无法使用贴纸声音功能
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public int createInstance(Context context) {
        if(context != null){
            mSoundPlay = new STSoundPlay(context);
        }

        int ret = createInstanceNative();

        if(0 == ret && mSoundPlay != null) {
            mSoundPlay.setStickHandle(this);
        }
        return ret;
    }

    /**
     * 销毁实例，必须在opengl环境中运行
     */
    public void destroyInstance() {
        destroyInstanceNative();
        if(mSoundPlay != null){
            mSoundPlay.release();
            mSoundPlay = null;
        }
    }

    /**
     * 创建贴纸实例
     *
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    private native int createInstanceNative();


    /**
     * 对OpenGL ES 中的纹理进行贴纸处理，必须在opengl环境中运行，仅支持RGBA图像格式
     *
     * @param textureIn      输入textureid
     * @param humanAction    输入检测到的人脸信息，由STMobileHumanActionNative相关的API获得
     * @param rotate         为使人脸正向，pInputImage需要旋转的角度。比如STRotateType.ST_CLOCKWISE_ROTATE_90
     * @param frontRotate    贴纸中前景方向
     * @param imageWidth     图像宽度（以像素为单位）
     * @param imageHeight    图像高度（以像素为单位）
     * @param needsMirroring 传入图像与显示图像是否是镜像关系
     * @param textureOut     处理后的纹理ID，用来做渲染
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int processTexture(int textureIn, STHumanAction humanAction, int rotate, int frontRotate, int imageWidth, int imageHeight, boolean needsMirroring, STStickerInputParams inputParams, int textureOut);

    /**
     * 对OpenGL ES 中的纹理进行贴纸处理，必须在opengl环境中运行，仅支持RGBA图像格式.支持buffer输出
     *
     * @param textureIn      输入textureid
     * @param humanAction    输入检测到的人脸信息，由STMobileHumanActionNative相关的API获得
     * @param rotate         为使人脸正向，pInputImage需要旋转的角度。比如STRotateType.ST_CLOCKWISE_ROTATE_90
     * @param frontRotate    贴纸中前景方向
     * @param imageWidth     图像宽度（以像素为单位）
     * @param imageHeight    图像高度（以像素为单位）
     * @param needsMirroring 传入图像与显示图像是否是镜像关系
     * @param textureOut     处理后的纹理ID，用来做渲染
     * @param outFmt         输出图像的格式，支持NV21,BGR,BGRA,NV12,RGBA等,比如STCommon.ST_PIX_FMT_NV12。
     * @param imageOut       输出图像的buffer，需要从外部创建
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int processTextureAndOutputBuffer(int textureIn, STHumanAction humanAction, int rotate, int frontRotate, int imageWidth, int imageHeight, boolean needsMirroring, STStickerInputParams inputParams, int textureOut, int outFmt, byte[] imageOut);

    /**
     * 切换贴纸路径
     *
     * @param path 贴纸路径。如果输入null，为无贴纸模式
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int changeSticker(String path);

    /**
     * 从asset文件切换贴纸
     *
     * @param assetFilePath 贴纸路径。如果输入null，为无贴纸模式
     * @param assetManager 资源文件管理器
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int changeStickerFromAssetsFile(String assetFilePath, AssetManager assetManager);

    /**
     * 添加贴纸
     *
     * @param path 贴纸路径。如果输入null，为无贴纸模式
     * @return 成功返回贴纸id，错误返回其他，参考STCommon.ResultCode
     */
    public native int addSticker(String path);

    /**
     * 从asset文件添加贴纸
     *
     * @param assetFilePath 贴纸路径。如果输入null，为无贴纸模式
     * @return 成功返回贴纸id，错误返回其他，参考STCommon.ResultCode
     */
    public native int addStickerFromAssetsFile(String assetFilePath, AssetManager assetManager);

    /**
     * 创建贴纸
     *
     * @param packageId 贴纸id
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int createSticker(int packageId);

    /**
     * 删除贴纸
     *
     * @param packageId 贴纸id。
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int removeSticker(int packageId);

    /**
     * 删除所有贴纸
     *
     */
    public native void removeAllStickers();

    /**
     * 创建贴纸模块
     * @param moduleType 模块类型
     * @param packageId 素材包id
     * @param moduleId 贴纸模块id
     * @return   成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
     */
    public native int createModule(int moduleType, int packageId, int moduleId);

    /**
     * 将贴纸模块移动到指定的素材包
     * @param packageId 素材包id
     * @param moduleId 贴纸模块id
     * @return   成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
     */
    public native int moveModuleToPackage(int packageId, int moduleId);

    /**
     * 删除贴纸模块. 可以在非 OpenGL 线程中调用, OpenGL资源在处理下一帧时释放
     * @param moduleId 贴纸模块id
     * @return   成功返回ST_OK, 失败返回其他错误码, 错误码定义在st_mobile_common.h中, 如ST_E_FAIL等
     */
    public native int removeModule(int moduleId);

    /**
     * 获取贴纸模块信息
     * @return   成功返回贴纸模块信息, 失败返回null
     */
    public native STModuleInfo[] getModules();

    /**
     * 获取素材包id数组
     * @return   素材包id数组, 失败返回null
     */
    public native int[] getPackageIds();

    /**
     * 获取当前贴纸的触发动作
     *
     * @return 触发动作，比如STMobileHumanActionNative.ST_MOBILE_EYE_BLINK,
     * 比如STMobileHumanActionNative.ST_MOBILE_BROW_JUMP等
     */
    public native long getTriggerAction();

    /**
     * 等待素材加载完毕后再渲染，因为会导致切换素材包时画面卡顿，仅建议用于希望等待模型加载完毕再渲染的场景，比如单帧或较短视频的3D绘制等
     * @param needWait 是否等待素材加载完毕后再渲染
     * @return   成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
     */
    public native int setWaitingMaterialLoaded(boolean needWait);

    /**
     * 设置贴纸素材图像所占用的最大内存
     * @param value   贴纸素材图像所占用的最大内存（MB）,默认150MB,素材过大时,循环加载,降低内存； 贴纸较小时,全部加载,降低cpu
     * @return        成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
     */
    public native int setMaxMemory(int value);

    /**
     * 通知声音停止函数
     * @param name   结束播放的声音文件名（MB）,默认150MB,素材过大时,循环加载,降低内存； 贴纸较小时,全部加载,降低cpu
     * @return        成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
     */
    public native int setSoundPlayDone(String name);


    /**
     * 销毁实例，必须在opengl环境中运行
     */
    private native void destroyInstanceNative();

    /**
     * 加载Avatar功能对应的模型
     * @param modelpath   Avatar模型文件对应的绝对路径
     * @return        成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
     */
    public native int loadAvatarModel(String modelpath);

    /**
     * 从assets文件夹加载Avatar功能对应的模型
     * @param assetModelpath 模型文件路径
     * @param assetManager 资源文件管理器
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int loadAvatarModelFromAssetFile(String assetModelpath, AssetManager assetManager);

    /**
     * 卸载Avatar功能对应的模型及清理相关数据
     */
    public native int removeAvatarModel();

    /**
     * 为指定贴纸模块添加transition
     * @param moduleId 贴纸模块id
     * @param targetState 目标状态
     * @param conditionArray condition
     * @param paramArray 状态转换参数
     * @param transId transition id
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int addModuleTransition(int moduleId, int targetState, STCondition[] conditionArray, STTransParam[] paramArray, int[] transId);

    /**
     * 移除指定贴纸模块的某一transition
     * @param transId   transition id
     * @return        成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
     */
    public native int removeTransition(int transId);

    /**
     * 清除指定贴纸模块的所有transition
     * @param moduleId   贴纸模块id
     * @return        成功返回ST_OK,失败返回其他错误码,错误码定义在st_mobile_common.h中,如ST_E_FAIL等
     */
    public native int clearModuleTransition(int moduleId);


    /**
     * 设置int类型参数
     * @param moduleId  贴纸模块id
     * @param paramType 参数类型
     * @param value     需要设置的参数数值
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int setParamInt(int moduleId, int paramType, int value);

    /**
     * 设置long类型参数
     * @param moduleId  贴纸模块id
     * @param paramType 参数类型
     * @param value     需要设置的参数数值
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int setParamLong(int moduleId, int paramType, long value);

    /**
     * 设置bool类型参数
     * @param moduleId  贴纸模块id
     * @param paramType 参数类型
     * @param value     需要设置的参数数值
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int setParamBool(int moduleId, int paramType, boolean value);

    /**
     * 设置float类型参数
     * @param moduleId  贴纸模块id
     * @param paramType 参数类型
     * @param value     需要设置的参数数值
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int setParamFloat(int moduleId, int paramType, boolean value);

    /**
     * 设置string类型参数
     * @param moduleId  贴纸模块id
     * @param paramType 参数类型
     * @param value     需要设置的参数数值
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int setParamStr(int moduleId, int paramType, String value);

    /**
     * 获取int类型参数
     * @param moduleId  贴纸模块id
     * @param paramType 参数类型
     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
     */
    public native int getParamInt(int moduleId, int paramType);

    /**
     * 获取当前需要的自定义输入参数列表，应该在每次切换/添加素材包之后调用
     * @return 返回需要自定义的参数列表类型
     */
    public native int getNeededInputParams();

//    /**
//     * 获取long类型参数
//     * @param moduleId  贴纸模块id
//     * @param paramType 参数类型
//     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
//     */
//    public native int getParamLong(int moduleId, int paramType);
//
//    /**
//     * 获取bool类型参数
//     * @param moduleId  贴纸模块id
//     * @param paramType 参数类型
//     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
//     */
//    public native int getParamBool(int moduleId, int paramType);
//
//    /**
//     * 获取float类型参数
//     * @param moduleId  贴纸模块id
//     * @param paramType 参数类型
//     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
//     */
//    public native int getParamFloat(int moduleId, int paramType);
//
//    /**
//     * 获取string类型参数
//     * @param moduleId  贴纸模块id
//     * @param paramType 参数类型
//     * @return 成功返回0，错误返回其他，参考STCommon.ResultCode
//     */
//    public native int getParamStr(int moduleId, int paramType);
}
