package com.sensetime.stmobile.model;

/**
 * 肢体检测结果
*/
public class STMobileBodyInfo {
    public int id;                 ///< body id

    public  STPoint[] keyPoints;    ///< body关键点
    public float[] keyPointsScore; ///< 关键点的置信度[0,1] 值越大，置信度越高
    public int keyPointsCount;     ///< body关键点个数 目前为4或14

    public  STPoint[] contourPoints;    ///< 肢体轮廓点
    public float[] contourPointsScore; ///< 肢体轮廓点键点的置信度[0,1] 值越大，置信度越高
    public int contourPointsCount;     ///< 肢体轮廓点个数

    public long bodyAction;         ///< body动作，本版本无效
    public float bodyActionScore;  ///< body动作置信度,本版本无效

    public STPoint[] getKeyPoints(){
        return keyPoints;
    }

    public float[] getKeyPointsScore(){
        return keyPointsScore;
    }

    public STPoint[] getContourPoints(){
        return contourPoints;
    }

    public float[] getContourPointsScore(){
        return contourPointsScore;
    }
}
