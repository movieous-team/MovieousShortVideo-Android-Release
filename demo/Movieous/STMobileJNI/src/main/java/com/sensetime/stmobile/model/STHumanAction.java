package com.sensetime.stmobile.model;

import android.hardware.Camera;

import com.sensetime.stmobile.STRotateType;
import com.sensetime.stmobile.model.STImage;
import com.sensetime.stmobile.model.STMobile106;
import com.sensetime.stmobile.model.STMobileFaceInfo;
import com.sensetime.stmobile.model.STMobileHandInfo;

/**
 * 人脸信息定义（包括人脸信息及人脸行为），
 * 作为STMobileHumanActionNative.humanActionDetect的返回值
 */
public class STHumanAction {
    public int bufIndex;             ///< 对应的图像数据缓冲区索引，用于双缓冲渲染时使用

    public STMobileFaceInfo[] faces; ///< 检测到的人脸信息
    public int faceCount;            ///< 检测到的人脸数目

    public STMobileHandInfo[] hands; ///< 检测到的手的信息
    public int handCount;            ///< 检测到的手的数目 (目前仅支持检测一只手)

    public STImage image;            ///< 前后背景分割图片信息,前景为0,背景为255,边缘部分有模糊(0-255之间),输出图像大小可以调节
    public float backGroundScore;    ///< 置信度

    public STImage hair;             ///< 头发分割图片信息,前景为0,背景为255,边缘部分有模糊(0-255之间),输出图像大小可以调节
    public float hairScore;          ///< 置信度

    public STImage multiSegment; ///多类分割结果图片信息,背景0，手1，头发2，眼镜3，躯干4，上臂5，下臂6，大腿7，小腿8，脚9，帽子10，随身物品11，脸12，上衣13，下装14，输出图像大小可以调节
    public float multiSegmentScore;   ///多类分割置信度

    public STMobileBodyInfo[] bodys; ///< 检测到的人体信息
    public int bodyCount;            ///< 检测到的人体的数目

    public STMobile106[] getMobileFaces() {
        if (faceCount == 0) {
            return null;
        }

        STMobile106[] arrayFaces = new STMobile106[faceCount];
        for(int i = 0; i < faceCount; ++i) {
            arrayFaces[i] = faces[i].face106;
        }

        return arrayFaces;
    }

    public boolean replaceMobile106(STMobile106[] arrayFaces) {
        if (arrayFaces == null || arrayFaces.length == 0
                || faces == null || faceCount != arrayFaces.length) {
            return false;
        }

        for (int i = 0; i < arrayFaces.length; ++i) {
            faces[i].face106 = arrayFaces[i];
        }
        return true;
    }

    public STMobileFaceInfo[] getFaceInfos() {
        if (faceCount == 0) {
            return null;
        }

        return faces;
    }

    public STMobileHandInfo[] getHandInfos() {
        if (handCount == 0) {
            return null;
        }

        return hands;
    }

    public STImage getImage(){
        return image;
    }

    public STImage getHair(){
        return hair;
    }

    /**
     * 镜像human_action检测结果
     *
     * @param width        用于转换的图像的宽度(以像素为单位)
     * @param humanAction  需要镜像的STHumanAction对象
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public static native STHumanAction humanActionMirror(int width, STHumanAction humanAction);

    /**
     * 旋转human_action检测结果
     *
     * @param width        用于转换的图像的宽度(以像素为单位)
     * @param width        用于转换的图像的宽度(以像素为单位)
     * @param orientation  顺时针旋转的角度,例如STRotateType.ST_CLOCKWISE_ROTATE_90（旋转90度）
     * @param rotateBackGround 是否旋转前后背景分割结果
     * @param humanAction  需要镜像的STHumanAction对象
     * @return 成功返回0，错误返回其他，参考STUtils.ResultCode
     */
    public static native STHumanAction humanActionRotate(int width, int height, int orientation, boolean rotateBackGround, STHumanAction humanAction);

    /**
     * resize human_action检测结果
     *
     * @param scale        缩放的尺度
     * @param humanAction  需要resize的STHumanAction对象
     * @return 成功返回新humanActon，错误返回null
     */
    public static native STHumanAction humanActionResize(float scale, STHumanAction humanAction);

    /**
     * 根据摄像头ID和摄像头方向，重新计算HumanAction（双输入场景使用）
     * @param humanAction  输入HumanAction
     * @param width        图像宽度
     * @param height       图像高度
     * @param cameraId     摄像头ID
     * @param cameraOrientation  摄像头方向
     * @return  旋转或镜像后的HumanAction
     */
    public static STHumanAction humanActionRotateAndMirror(STHumanAction humanAction, int width, int height, int cameraId, int cameraOrientation){
        if(humanAction == null){
            return null;
        }
        if(cameraId != Camera.CameraInfo.CAMERA_FACING_FRONT && cameraId != Camera.CameraInfo.CAMERA_FACING_BACK){
            return humanAction;
        }
        if(cameraOrientation != 90 && cameraOrientation != 270){
            return humanAction;
        }
        //humanAction rotate && mirror
        if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && cameraOrientation == 90){
            humanAction = humanActionRotate(height, width, STRotateType.ST_CLOCKWISE_ROTATE_90, false, humanAction);
            humanAction = humanActionMirror(width, humanAction);
        }else if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && cameraOrientation == 270){
            humanAction = humanActionRotate(height, width, STRotateType.ST_CLOCKWISE_ROTATE_270, false, humanAction);
            humanAction = humanActionMirror(width, humanAction);
        }else if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK && cameraOrientation == 270){
            humanAction = humanActionRotate(height, width, STRotateType.ST_CLOCKWISE_ROTATE_270, false, humanAction);
        }else if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK && cameraOrientation == 90){
            humanAction = humanActionRotate(height, width, STRotateType.ST_CLOCKWISE_ROTATE_90, false, humanAction);
        }

        return humanAction;
    }
}
