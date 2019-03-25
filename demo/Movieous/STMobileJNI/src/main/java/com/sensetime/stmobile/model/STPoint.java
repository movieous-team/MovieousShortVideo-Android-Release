package com.sensetime.stmobile.model;

public class STPoint {
    private float x;    ///< 点的水平方向坐标，为浮点数
    private float y;    ///< 点的竖直方向坐标，为浮点数

    public STPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
