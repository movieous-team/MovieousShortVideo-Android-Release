package com.sensetime.stmobile.model;

/**
 * 定义人脸106点相关的信息
 */
public class STMobile106 {
    //用于对应.h文件中的st_mobile_106_t
    /// @brief 供106点使用
    STRect rect;               ///< 代表面部的矩形区域
    float score;                ///< 置信度
    STPoint[] points_array = new STPoint[106];   ///< 人脸106关键点的数组
    float[] visibility_array = new float[106];    /// 对应点是否被遮挡, 未被遮挡1.0, 遮挡值是0.0
    float yaw;                  ///< 水平转角，真实度量的左负右正
    float pitch;                ///< 俯仰角，真实度量的上负下正
    float roll;                 ///< 旋转角，真实度量的左负右正
    float eye_dist;           ///< 两眼间距
    int ID;                 ///< faceID

    public STMobile106(STRect rect, float score, STPoint[] points_array, float yaw, float pitch, float roll, float eye_dist, int ID) {
        this.rect = rect;
        this.score = score;
        this.points_array = points_array;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.eye_dist = eye_dist;
        this.ID = ID;
    }

    public STRect getRect() {
        return rect;
    }

    public void setRect(STRect rect) {
        this.rect = rect;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public STPoint[] getPoints_array() {
        return points_array;
    }

    public float[] getVisibilityArray(){
        return visibility_array;
    }

    public void setPoints_array(STPoint[] points_array) {
        this.points_array = points_array;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getEye_dist() {
        return eye_dist;
    }

    public void setEye_dist(float eye_dist) {
        this.eye_dist = eye_dist;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
