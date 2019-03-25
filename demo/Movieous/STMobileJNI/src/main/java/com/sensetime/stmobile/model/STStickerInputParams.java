package com.sensetime.stmobile.model;

public class STStickerInputParams {
    float[] cameraQuaternion;
    int quaternionLength;
    boolean isFrontCamera;

    int customEvent;

    public STStickerInputParams(float[] quaternion, boolean isFront, int event){
        if(quaternion != null){
            cameraQuaternion = quaternion;
            quaternionLength = quaternion.length;
        }else {
            cameraQuaternion = null;
            quaternionLength = 0;
        }

        isFrontCamera = isFront;
        customEvent = event;
    }
}
