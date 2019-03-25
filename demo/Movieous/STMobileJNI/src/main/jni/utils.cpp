#include "utils.h"
#include <st_mobile_common.h>
#include <st_mobile_human_action.h>
#include <st_mobile_sticker.h>
#include <st_mobile_sticker_transition.h>

#define  LOG_TAG    "utils"

long getCurrentTime() {
    struct timeval tv;
    gettimeofday(&tv,NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

int getImageStride(const st_pixel_format &pixel_format, const int &outputWidth) {
    int stride = 0;
    switch(pixel_format) {
        case ST_PIX_FMT_YUV420P:
        case ST_PIX_FMT_NV12:
        case ST_PIX_FMT_NV21:
            stride = outputWidth;
            break;
        case ST_PIX_FMT_BGRA8888:
        case ST_PIX_FMT_RGBA8888:
            stride = outputWidth * 4;
            break;
        case ST_PIX_FMT_BGR888:
            stride = outputWidth * 3;
            break;
        default:
            break;
    }

    return stride;
}

jobject convert2STRect(JNIEnv *env, const st_rect_t &object_rect){
    jclass STRectClass = env->FindClass("com/sensetime/stmobile/model/STRect");

    if (STRectClass == NULL) {
        return NULL;
    }

    jobject rectObject = env->AllocObject(STRectClass);

    jfieldID rect_left = env->GetFieldID(STRectClass, "left", "I");
    jfieldID rect_top = env->GetFieldID(STRectClass, "top", "I");
    jfieldID rect_right = env->GetFieldID(STRectClass, "right", "I");
    jfieldID rect_bottom = env->GetFieldID(STRectClass, "bottom", "I");

    env->SetIntField(rectObject, rect_left, object_rect.left);
    env->SetIntField(rectObject, rect_right, object_rect.right);
    env->SetIntField(rectObject, rect_top, object_rect.top);
    env->SetIntField(rectObject, rect_bottom, object_rect.bottom);

    if(STRectClass != NULL){
        env->DeleteLocalRef(STRectClass);
    }

    return rectObject;
}

jobject convert2MobileFace106(JNIEnv *env, const st_mobile_106_t &mobile_106){
    jclass st_mobile_106_class = env->FindClass("com/sensetime/stmobile/model/STMobile106");
    jfieldID frect = env->GetFieldID(st_mobile_106_class, "rect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fscore = env->GetFieldID(st_mobile_106_class, "score", "F");
    jfieldID fpoints_array = env->GetFieldID(st_mobile_106_class, "points_array", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fvisibility_array = env->GetFieldID(st_mobile_106_class, "visibility_array", "[F");
    jfieldID fyaw = env->GetFieldID(st_mobile_106_class, "yaw", "F");
    jfieldID fpitch = env->GetFieldID(st_mobile_106_class, "pitch", "F");
    jfieldID froll = env->GetFieldID(st_mobile_106_class, "roll", "F");
    jfieldID feye_dist = env->GetFieldID(st_mobile_106_class, "eye_dist", "F");
    jfieldID fID = env->GetFieldID(st_mobile_106_class, "ID", "I");

    jclass st_mobile_point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_mobile_point_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_mobile_point_class, "y", "F");

    jclass st_face_rect_class = env->FindClass("com/sensetime/stmobile/model/STRect");
    jfieldID frect_left = env->GetFieldID(st_face_rect_class, "left", "I");
    jfieldID frect_top = env->GetFieldID(st_face_rect_class, "top", "I");
    jfieldID frect_right = env->GetFieldID(st_face_rect_class, "right", "I");
    jfieldID frect_bottom = env->GetFieldID(st_face_rect_class, "bottom", "I");

    jobject st_106_object = env->AllocObject(st_mobile_106_class);

    //继续获取rect
    jobject face_rect = env->AllocObject(st_face_rect_class);

    env->SetIntField(face_rect, frect_left, mobile_106.rect.left);
    env->SetIntField(face_rect, frect_right, mobile_106.rect.right);
    env->SetIntField(face_rect, frect_top, mobile_106.rect.top);
    env->SetIntField(face_rect, frect_bottom, mobile_106.rect.bottom);

    //继续获取points_array
    jobjectArray face_point_array = env->NewObjectArray(106, st_mobile_point_class, 0);
    jfloatArray face_visibility_array = env->NewFloatArray(106);
    jfloat visibility_array[106];

    for(int j=0; j<106; j++) {
        jobject st_point_object = env->AllocObject(st_mobile_point_class);

        env->SetFloatField(st_point_object, fpoint_x, mobile_106.points_array[j].x);
        env->SetFloatField(st_point_object, fpoint_y, mobile_106.points_array[j].y);

        env->SetObjectArrayElement(face_point_array, j, st_point_object);
        env->DeleteLocalRef(st_point_object);

        visibility_array[j] = mobile_106.visibility_array[j];
    }
    env->SetFloatArrayRegion(face_visibility_array, 0, 106, visibility_array);
    env->SetObjectField(st_106_object, frect, face_rect);
    env->SetFloatField(st_106_object, fscore, mobile_106.score);
    env->SetObjectField(st_106_object, fpoints_array, face_point_array);
    env->SetObjectField(st_106_object, fvisibility_array, face_visibility_array);
    env->SetFloatField(st_106_object, fyaw, mobile_106.yaw);
    env->SetFloatField(st_106_object, fpitch, mobile_106.pitch);
    env->SetFloatField(st_106_object, froll, mobile_106.roll);
    env->SetFloatField(st_106_object, feye_dist, mobile_106.eye_dist);
    env->SetIntField(st_106_object, fID, mobile_106.ID);

    env->DeleteLocalRef(face_rect);
    env->DeleteLocalRef(face_point_array);
    env->DeleteLocalRef(face_visibility_array);

    if (st_mobile_point_class != NULL) {
        env->DeleteLocalRef(st_mobile_point_class);
    }

    if (st_face_rect_class != NULL) {
        env->DeleteLocalRef(st_face_rect_class);
    }

    if (st_mobile_106_class != NULL) {
        env->DeleteLocalRef(st_mobile_106_class);
    }

    return st_106_object;
}

jobject convert2FaceAttribute(JNIEnv *env, const st_mobile_attributes_t *faceAttribute){
    jclass face_attribute_cls = env->FindClass("com/sensetime/stmobile/model/STFaceAttribute");

    jfieldID fieldAttribute_count = env->GetFieldID(face_attribute_cls, "attribute_count", "I");
    jfieldID fieldAttribute = env->GetFieldID(face_attribute_cls, "arrayAttribute", "[Lcom/sensetime/stmobile/model/STFaceAttribute$Attribute;");

    jobject faceAttributeObj = env->AllocObject(face_attribute_cls);

    env->SetIntField(faceAttributeObj, fieldAttribute_count, faceAttribute->attribute_count);

    jclass attribute_cls = env->FindClass("com/sensetime/stmobile/model/STFaceAttribute$Attribute");
    jfieldID fieldCategory = env->GetFieldID(attribute_cls, "category", "Ljava/lang/String;");
    jfieldID fieldLabel = env->GetFieldID(attribute_cls, "label", "Ljava/lang/String;");
    jfieldID fieldScore = env->GetFieldID(attribute_cls, "score", "F");

    if (faceAttribute->attribute_count > 0) {
        LOGE("attribute_count: %d", faceAttribute->attribute_count);
        jobjectArray arrayAttrObj = env->NewObjectArray(faceAttribute->attribute_count, attribute_cls, 0);
        for (int i = 0; i < faceAttribute->attribute_count; ++i) {
            st_mobile_attribute_t one = faceAttribute->p_attributes[i];
            jobject attrObj = env->AllocObject(attribute_cls);
            jstring cateObj = env->NewStringUTF(one.category);
            jstring labelObj = env->NewStringUTF(one.label);
            env->SetObjectField(attrObj, fieldCategory, cateObj);
            env->SetObjectField(attrObj, fieldLabel, labelObj);
            env->SetFloatField(attrObj, fieldScore, one.score);

            env->SetObjectArrayElement(arrayAttrObj, i, attrObj);
            env->DeleteLocalRef(cateObj);
            env->DeleteLocalRef(labelObj);
            env->DeleteLocalRef(attrObj);
        }

        env->SetObjectField(faceAttributeObj, fieldAttribute, arrayAttrObj);
        env->DeleteLocalRef(arrayAttrObj);
    }

    env->DeleteLocalRef(attribute_cls);
    env->DeleteLocalRef(face_attribute_cls);
    return faceAttributeObj;
}

jobject convert2Image(JNIEnv *env, const st_image_t *image){
    jclass image_cls = env->FindClass("com/sensetime/stmobile/model/STImage");

    jfieldID fieldImageData = env->GetFieldID(image_cls, "imageData", "[B");
    jfieldID fieldPixelFormat = env->GetFieldID(image_cls, "pixelFormat", "I");
    jfieldID fieldWidth = env->GetFieldID(image_cls, "width", "I");
    jfieldID fieldHeight = env->GetFieldID(image_cls, "height", "I");
    jfieldID fieldStride = env->GetFieldID(image_cls, "stride", "I");
    jfieldID fieldTime = env->GetFieldID(image_cls, "timeStamp", "D");

    jobject imageObj = env->AllocObject(image_cls);

    jbyteArray arrayImageData;
    arrayImageData = (env)->NewByteArray(image->width*image->height);
    jbyte* data = (jbyte*)(image->data);
    if(data == NULL){
        return NULL;
    }
    env->SetByteArrayRegion(arrayImageData, 0, image->width*image->height, data);
    env->SetObjectField(imageObj, fieldImageData, arrayImageData);

    env->SetIntField(imageObj, fieldPixelFormat, (int)image->pixel_format);
    env->SetIntField(imageObj, fieldWidth, image->width);
    env->SetIntField(imageObj, fieldHeight, image->height);
    env->SetIntField(imageObj, fieldStride, image->stride);

    env->SetDoubleField(imageObj, fieldTime, image->time_stamp);

    env->DeleteLocalRef(arrayImageData);
    env->DeleteLocalRef(image_cls);

    return imageObj;
}

jobject convert2HandInfo(JNIEnv *env, const st_mobile_hand_t *hand_info){
    jclass hand_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileHandInfo");

    jfieldID fieldHandId = env->GetFieldID(hand_info_cls, "handId", "I");
    jfieldID fieldHandRect = env->GetFieldID(hand_info_cls, "handRect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fieldKeyPoints = env->GetFieldID(hand_info_cls, "keyPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsCount = env->GetFieldID(hand_info_cls, "keyPointsCount", "I");
    jfieldID fieldHandAction = env->GetFieldID(hand_info_cls, "handAction", "J");
    jfieldID fieldHandActionScore = env->GetFieldID(hand_info_cls, "handActionScore", "F");

    jobject handInfoObj = env->AllocObject(hand_info_cls);

    jclass hand_rect_class = env->FindClass("com/sensetime/stmobile/model/STRect");
    jfieldID hrect_left = env->GetFieldID(hand_rect_class, "left", "I");
    jfieldID hrect_top = env->GetFieldID(hand_rect_class, "top", "I");
    jfieldID hrect_right = env->GetFieldID(hand_rect_class, "right", "I");
    jfieldID hrect_bottom = env->GetFieldID(hand_rect_class, "bottom", "I");

    jobject handRectObj = env->AllocObject(hand_rect_class);
    env->SetIntField(handRectObj, hrect_left, hand_info->rect.left);
    env->SetIntField(handRectObj, hrect_top, hand_info->rect.top);
    env->SetIntField(handRectObj, hrect_right, hand_info->rect.right);
    env->SetIntField(handRectObj, hrect_bottom, hand_info->rect.bottom);

    env->SetObjectField(handInfoObj, fieldHandRect, handRectObj);

    jclass st_points_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_points_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_points_class, "y", "F");

    //extra_face_points
    jobjectArray key_points_array = env->NewObjectArray(hand_info->key_points_count, st_points_class, 0);
    for(int i = 0; i < hand_info->key_points_count; i++){
        jobject keyPointsObj = env->AllocObject(st_points_class);

        env->SetFloatField(keyPointsObj, fpoint_x, (hand_info->p_key_points+i)->x);
        env->SetFloatField(keyPointsObj, fpoint_y, (hand_info->p_key_points+i)->y);

        env->SetObjectArrayElement(key_points_array, i, keyPointsObj);
        env->DeleteLocalRef(keyPointsObj);
    }

    env->SetObjectField(handInfoObj, fieldKeyPoints, key_points_array);
    env->DeleteLocalRef(key_points_array);

    env->SetIntField(handInfoObj, fieldHandId, hand_info->id);
    env->SetIntField(handInfoObj, fieldKeyPointsCount, hand_info->key_points_count);
    env->SetLongField(handInfoObj, fieldHandAction, hand_info->hand_action);
    env->SetFloatField(handInfoObj, fieldHandActionScore, hand_info->score);

    env->DeleteLocalRef(st_points_class);
    env->DeleteLocalRef(hand_rect_class);
    env->DeleteLocalRef(handRectObj);

    return handInfoObj;
}

jobject convert2BodyInfo(JNIEnv *env, const st_mobile_body_t *body_info){
    jclass body_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileBodyInfo");

    jfieldID fieldId = env->GetFieldID(body_info_cls, "id", "I");
    jfieldID fieldKeyPoints = env->GetFieldID(body_info_cls, "keyPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsScore = env->GetFieldID(body_info_cls, "keyPointsScore", "[F");
    jfieldID fieldKeyPointsCount = env->GetFieldID(body_info_cls, "keyPointsCount", "I");
    jfieldID fieldContourPoints = env->GetFieldID(body_info_cls, "contourPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldContourPointsScore = env->GetFieldID(body_info_cls, "contourPointsScore", "[F");
    jfieldID fieldContourPointsCount = env->GetFieldID(body_info_cls, "contourPointsCount", "I");
    jfieldID fieldBodyAction = env->GetFieldID(body_info_cls, "bodyAction", "J");
    jfieldID fieldBodyActionScore = env->GetFieldID(body_info_cls, "bodyActionScore", "F");

    jobject bodyInfoObj = env->AllocObject(body_info_cls);

    jclass st_points_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_points_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_points_class, "y", "F");

    //key_points
    jobjectArray key_points_array = env->NewObjectArray(body_info->key_points_count, st_points_class, 0);
    jfloatArray key_points_score = env->NewFloatArray(body_info->key_points_count);
    jfloat key_points_score_array[body_info->key_points_count];

    for(int i = 0; i < body_info->key_points_count; i++){
        jobject keyPointsObj = env->AllocObject(st_points_class);

        env->SetFloatField(keyPointsObj, fpoint_x, (body_info->p_key_points+i)->x);
        env->SetFloatField(keyPointsObj, fpoint_y, (body_info->p_key_points+i)->y);

        env->SetObjectArrayElement(key_points_array, i, keyPointsObj);
        env->DeleteLocalRef(keyPointsObj);

        key_points_score_array[i] = body_info->p_key_points_score[i];
    }

    env->SetFloatArrayRegion(key_points_score, 0, body_info->key_points_count, key_points_score_array);
    env->SetObjectField(bodyInfoObj, fieldKeyPointsScore, key_points_score);

    env->SetObjectField(bodyInfoObj, fieldKeyPoints, key_points_array);
    env->DeleteLocalRef(key_points_score);
    env->DeleteLocalRef(key_points_array);

    //contour_points
    jobjectArray contour_points_array = env->NewObjectArray(body_info->contour_points_count, st_points_class, 0);
    jfloatArray contour_points_score = env->NewFloatArray(body_info->contour_points_count);
    jfloat contour_points_score_array[body_info->contour_points_count];

    for(int i = 0; i < body_info->contour_points_count; i++){
        jobject contourPointsObj = env->AllocObject(st_points_class);

        env->SetFloatField(contourPointsObj, fpoint_x, (body_info->p_contour_points+i)->x);
        env->SetFloatField(contourPointsObj, fpoint_y, (body_info->p_contour_points+i)->y);

        env->SetObjectArrayElement(contour_points_array, i, contourPointsObj);
        env->DeleteLocalRef(contourPointsObj);

        contour_points_score_array[i] = body_info->p_contour_points_score[i];
    }

    env->SetFloatArrayRegion(contour_points_score, 0, body_info->contour_points_count, contour_points_score_array);
    env->SetObjectField(bodyInfoObj, fieldContourPointsScore, contour_points_score);

    env->SetObjectField(bodyInfoObj, fieldContourPoints, contour_points_array);
    env->DeleteLocalRef(contour_points_score);
    env->DeleteLocalRef(contour_points_array);

    env->SetIntField(bodyInfoObj, fieldId, body_info->id);
    env->SetIntField(bodyInfoObj, fieldKeyPointsCount, body_info->key_points_count);
    env->SetIntField(bodyInfoObj, fieldContourPointsCount, body_info->contour_points_count);
    env->SetLongField(bodyInfoObj, fieldBodyAction, body_info->body_action);
    env->SetFloatField(bodyInfoObj, fieldBodyActionScore, body_info->body_action_score);

    env->DeleteLocalRef(st_points_class);
    env->DeleteLocalRef(body_info_cls);

    return bodyInfoObj;
}

jobject convert2FaceInfo(JNIEnv *env, const st_mobile_face_t *face_info){
    jclass face_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileFaceInfo");

    jfieldID fieldFace = env->GetFieldID(face_info_cls, "face106", "Lcom/sensetime/stmobile/model/STMobile106;");

    jfieldID fieldExtraFacePoints = env->GetFieldID(face_info_cls, "extraFacePoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldExtraFacePointsCount = env->GetFieldID(face_info_cls, "extraFacePointsCount", "I");

    jfieldID fieldEyeballCenter = env->GetFieldID(face_info_cls, "eyeballCenter", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldEyeballCenterPointsCount = env->GetFieldID(face_info_cls, "eyeballCenterPointsCount", "I");

    jfieldID fieldEyeballContour = env->GetFieldID(face_info_cls, "eyeballContour", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldEyeballContourPointsCount = env->GetFieldID(face_info_cls, "eyeballContourPointsCount", "I");

    jfieldID fieldLeftEyeballScore = env->GetFieldID(face_info_cls, "leftEyeballScore", "F");
    jfieldID fieldRightEyeballScore = env->GetFieldID(face_info_cls, "rightEyeballScore", "F");

    jfieldID fieldFaceAction = env->GetFieldID(face_info_cls, "faceAction", "J");

    jobject faceInfoObj = env->AllocObject(face_info_cls);

    //face106
    jclass face106Class = env->FindClass("com/sensetime/stmobile/model/STMobile106");

    jobject face106_object = env->AllocObject(face106Class);
    face106_object = convert2MobileFace106(env, face_info->face106);

    env->SetObjectField(faceInfoObj, fieldFace, face106_object);
    env->DeleteLocalRef(face106_object);

    jclass st_points_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_points_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_points_class, "y", "F");

    //extra_face_points
    jobjectArray extra_face_points_array = env->NewObjectArray(face_info->extra_face_points_count, st_points_class, 0);
    for(int i = 0; i < face_info->extra_face_points_count; i++){
        jobject extraFacePointsObj = env->AllocObject(st_points_class);

        env->SetFloatField(extraFacePointsObj, fpoint_x, face_info->p_extra_face_points[i].x);
        env->SetFloatField(extraFacePointsObj, fpoint_y, face_info->p_extra_face_points[i].y);

        env->SetObjectArrayElement(extra_face_points_array, i, extraFacePointsObj);
        env->DeleteLocalRef(extraFacePointsObj);
    }

    env->SetObjectField(faceInfoObj, fieldExtraFacePoints, extra_face_points_array);
    env->DeleteLocalRef(extra_face_points_array);

    env->SetIntField(faceInfoObj, fieldExtraFacePointsCount, face_info->extra_face_points_count);

    env->SetFloatField(faceInfoObj, fieldLeftEyeballScore, face_info->left_eyeball_score);
    env->SetFloatField(faceInfoObj, fieldRightEyeballScore, face_info->right_eyeball_score);

    //eyeball_center
    jobjectArray eyeball_center_array = env->NewObjectArray(face_info->eyeball_center_points_count, st_points_class, 0);
    for(int i = 0; i < face_info->eyeball_center_points_count; i++){
        jobject eyeballCenterObj = env->AllocObject(st_points_class);

        env->SetFloatField(eyeballCenterObj, fpoint_x, face_info->p_eyeball_center[i].x);
        env->SetFloatField(eyeballCenterObj, fpoint_y, face_info->p_eyeball_center[i].y);

        env->SetObjectArrayElement(eyeball_center_array, i, eyeballCenterObj);
        env->DeleteLocalRef(eyeballCenterObj);
    }

    env->SetObjectField(faceInfoObj, fieldEyeballCenter, eyeball_center_array);
    env->DeleteLocalRef(eyeball_center_array);

    env->SetIntField(faceInfoObj, fieldEyeballCenterPointsCount, face_info->eyeball_center_points_count);

    //eyeball_contour
    jobjectArray eyeball_contour_array = env->NewObjectArray(face_info->eyeball_contour_points_count, st_points_class, 0);
    for(int i = 0; i < face_info->eyeball_contour_points_count; i++){
        jobject eyeballContourObj = env->AllocObject(st_points_class);

        env->SetFloatField(eyeballContourObj, fpoint_x, face_info->p_eyeball_contour[i].x);
        env->SetFloatField(eyeballContourObj, fpoint_y, face_info->p_eyeball_contour[i].y);

        env->SetObjectArrayElement(eyeball_contour_array, i, eyeballContourObj);
        env->DeleteLocalRef(eyeballContourObj);
    }

    env->SetObjectField(faceInfoObj, fieldEyeballContour, eyeball_contour_array);
    env->DeleteLocalRef(eyeball_contour_array);

    env->SetIntField(faceInfoObj, fieldEyeballContourPointsCount, face_info->eyeball_contour_points_count);
    env->SetLongField(faceInfoObj, fieldFaceAction, face_info->face_action);

    env->DeleteLocalRef(st_points_class);
    env->DeleteLocalRef(face_info_cls);

    return faceInfoObj;
}

jobject convert2HumanAction(JNIEnv *env, const st_mobile_human_action_t *human_action){
    jclass human_action_cls = env->FindClass("com/sensetime/stmobile/model/STHumanAction");

    jfieldID fieldFaces = env->GetFieldID(human_action_cls, "faces", "[Lcom/sensetime/stmobile/model/STMobileFaceInfo;");
    jfieldID fieldFaceCount = env->GetFieldID(human_action_cls, "faceCount", "I");

    jfieldID fieldHands = env->GetFieldID(human_action_cls, "hands", "[Lcom/sensetime/stmobile/model/STMobileHandInfo;");
    jfieldID fieldHandCount = env->GetFieldID(human_action_cls, "handCount", "I");

    jfieldID fieldBodys = env->GetFieldID(human_action_cls, "bodys", "[Lcom/sensetime/stmobile/model/STMobileBodyInfo;");
    jfieldID fieldBodyCount = env->GetFieldID(human_action_cls, "bodyCount", "I");

    jfieldID fieldImage = env->GetFieldID(human_action_cls, "image", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldBackGroundScore = env->GetFieldID(human_action_cls, "backGroundScore", "F");

    jfieldID fieldHair = env->GetFieldID(human_action_cls, "hair", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldHairScore = env->GetFieldID(human_action_cls, "hairScore", "F");

    jfieldID fieldMultiSegment = env->GetFieldID(human_action_cls, "multiSegment", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldMultiSegmentScore = env->GetFieldID(human_action_cls, "multiSegmentScore", "F");

    jobject humanActionObj = env->AllocObject(human_action_cls);

    //faces
    env->SetIntField(humanActionObj, fieldFaceCount, human_action->face_count);

    jclass face_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileFaceInfo");
    jobjectArray face_info_array = env->NewObjectArray(human_action->face_count, face_info_cls, 0);
    for(int i = 0; i < human_action->face_count; i++){
        jobject faceInfoObj = env->AllocObject(face_info_cls);

        faceInfoObj = convert2FaceInfo(env, human_action->p_faces+i);

        env->SetObjectArrayElement(face_info_array, i, faceInfoObj);
        env->DeleteLocalRef(faceInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldFaces, face_info_array);
    env->DeleteLocalRef(face_info_array);
    env->DeleteLocalRef(face_info_cls);

    //hands
    env->SetIntField(humanActionObj, fieldHandCount, human_action->hand_count);

    jclass hand_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileHandInfo");
    jobjectArray hand_info_array = env->NewObjectArray(human_action->hand_count, hand_info_cls, 0);
    for(int i = 0; i < human_action->hand_count; i++){
        jobject handInfoObj = env->AllocObject(hand_info_cls);

        handInfoObj = convert2HandInfo(env, human_action->p_hands+i);

        env->SetObjectArrayElement(hand_info_array, i, handInfoObj);
        env->DeleteLocalRef(handInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldHands, hand_info_array);
    env->DeleteLocalRef(hand_info_array);
    env->DeleteLocalRef(hand_info_cls);

    //bodys
    env->SetIntField(humanActionObj, fieldBodyCount, human_action->body_count);

    jclass body_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileBodyInfo");
    jobjectArray body_info_array = env->NewObjectArray(human_action->body_count, body_info_cls, 0);
    for(int i = 0; i < human_action->body_count; i++){
        jobject bodyInfoObj = env->AllocObject(body_info_cls);

        bodyInfoObj = convert2BodyInfo(env, human_action->p_bodys+i);

        env->SetObjectArrayElement(body_info_array, i, bodyInfoObj);
        env->DeleteLocalRef(bodyInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldBodys, body_info_array);
    env->DeleteLocalRef(body_info_array);
    env->DeleteLocalRef(body_info_cls);

    //image
    env->SetFloatField(humanActionObj, fieldBackGroundScore, human_action->background_score);
    if(human_action->p_background != NULL){
        jclass imageClass = env->FindClass("com/sensetime/stmobile/model/STImage");
        jobject image_object = env->AllocObject(imageClass);
        image_object = convert2Image(env, human_action->p_background);

        env->SetObjectField(humanActionObj, fieldImage, image_object);
        env->DeleteLocalRef(imageClass);
    }

    //hair
    env->SetFloatField(humanActionObj, fieldHairScore, human_action->hair_score);
    if(human_action->p_hair != NULL){
        jclass imageClass = env->FindClass("com/sensetime/stmobile/model/STImage");
        jobject image_object = env->AllocObject(imageClass);
        image_object = convert2Image(env, human_action->p_hair);

        env->SetObjectField(humanActionObj, fieldHair, image_object);
        env->DeleteLocalRef(imageClass);
    }

    //MultiSegment
    env->SetFloatField(humanActionObj, fieldMultiSegmentScore, human_action->multi_segment_score);
    if(human_action->p_multi_segment != NULL){
        jclass imageClass = env->FindClass("com/sensetime/stmobile/model/STImage");
        jobject image_object = env->AllocObject(imageClass);
        image_object = convert2Image(env, human_action->p_multi_segment);

        env->SetObjectField(humanActionObj, fieldMultiSegment, image_object);
        env->DeleteLocalRef(imageClass);
    }

    return humanActionObj;
}

void convert2HumanAction(JNIEnv *env, const st_mobile_human_action_t *human_action, jobject humanActionObj){
    jclass human_action_cls = env->FindClass("com/sensetime/stmobile/model/STHumanAction");

    jfieldID fieldFaces = env->GetFieldID(human_action_cls, "faces", "[Lcom/sensetime/stmobile/model/STMobileFaceInfo;");
    jfieldID fieldFaceCount = env->GetFieldID(human_action_cls, "faceCount", "I");

    jfieldID fieldHands = env->GetFieldID(human_action_cls, "hands", "[Lcom/sensetime/stmobile/model/STMobileHandInfo;");
    jfieldID fieldHandCount = env->GetFieldID(human_action_cls, "handCount", "I");

    jfieldID fieldBodys = env->GetFieldID(human_action_cls, "bodys", "[Lcom/sensetime/stmobile/model/STMobileBodyInfo;");
    jfieldID fieldBodyCount = env->GetFieldID(human_action_cls, "bodyCount", "I");

    jfieldID fieldImage = env->GetFieldID(human_action_cls, "image", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldBackGroundScore = env->GetFieldID(human_action_cls, "backGroundScore", "F");

    jfieldID fieldHair = env->GetFieldID(human_action_cls, "hair", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldHairScore = env->GetFieldID(human_action_cls, "hairScore", "F");

    jfieldID fieldMultiSegment = env->GetFieldID(human_action_cls, "multiSegment", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldMultiSegmentScore = env->GetFieldID(human_action_cls, "multiSegmentScore", "F");

    //faces
    env->SetIntField(humanActionObj, fieldFaceCount, human_action->face_count);

    jclass face_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileFaceInfo");
    jobjectArray face_info_array = env->NewObjectArray(human_action->face_count, face_info_cls, 0);
    for(int i = 0; i < human_action->face_count; i++){
        jobject faceInfoObj = env->AllocObject(face_info_cls);

        faceInfoObj = convert2FaceInfo(env, human_action->p_faces+i);

        env->SetObjectArrayElement(face_info_array, i, faceInfoObj);
        env->DeleteLocalRef(faceInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldFaces, face_info_array);
    env->DeleteLocalRef(face_info_array);
    env->DeleteLocalRef(face_info_cls);

    //hands
    env->SetIntField(humanActionObj, fieldHandCount, human_action->hand_count);

    jclass hand_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileHandInfo");
    jobjectArray hand_info_array = env->NewObjectArray(human_action->hand_count, hand_info_cls, 0);
    for(int i = 0; i < human_action->hand_count; i++){
        jobject handInfoObj = env->AllocObject(hand_info_cls);

        handInfoObj = convert2HandInfo(env, human_action->p_hands+i);

        env->SetObjectArrayElement(hand_info_array, i, handInfoObj);
        env->DeleteLocalRef(handInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldHands, hand_info_array);
    env->DeleteLocalRef(hand_info_array);
    env->DeleteLocalRef(hand_info_cls);

    //bodys
    env->SetIntField(humanActionObj, fieldBodyCount, human_action->body_count);

    jclass body_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileBodyInfo");
    jobjectArray body_info_array = env->NewObjectArray(human_action->body_count, body_info_cls, 0);
    for(int i = 0; i < human_action->body_count; i++){
        jobject bodyInfoObj = env->AllocObject(body_info_cls);

        bodyInfoObj = convert2BodyInfo(env, human_action->p_bodys+i);

        env->SetObjectArrayElement(body_info_array, i, bodyInfoObj);
        env->DeleteLocalRef(bodyInfoObj);
    }

    env->SetObjectField(humanActionObj, fieldBodys, body_info_array);
    env->DeleteLocalRef(body_info_array);
    env->DeleteLocalRef(body_info_cls);

    //image
    env->SetFloatField(humanActionObj, fieldBackGroundScore, human_action->background_score);
    if(human_action->p_background != NULL){
        jclass imageClass = env->FindClass("com/sensetime/stmobile/model/STImage");
        jobject image_object = env->AllocObject(imageClass);
        image_object = convert2Image(env, human_action->p_background);

        env->SetObjectField(humanActionObj, fieldImage, image_object);
        env->DeleteLocalRef(imageClass);
    }

    //hair
    env->SetFloatField(humanActionObj, fieldHairScore, human_action->hair_score);
    if(human_action->p_hair != NULL){
        jclass imageClass = env->FindClass("com/sensetime/stmobile/model/STImage");
        jobject image_object = env->AllocObject(imageClass);
        image_object = convert2Image(env, human_action->p_hair);

        env->SetObjectField(humanActionObj, fieldHair, image_object);
        env->DeleteLocalRef(imageClass);
    }


    //MultiSegment
    env->SetFloatField(humanActionObj, fieldMultiSegmentScore, human_action->multi_segment_score);
    if(human_action->p_multi_segment != NULL){
        jclass imageClass = env->FindClass("com/sensetime/stmobile/model/STImage");
        jobject image_object = env->AllocObject(imageClass);
        image_object = convert2Image(env, human_action->p_multi_segment);

        env->SetObjectField(humanActionObj, fieldMultiSegment, image_object);
        env->DeleteLocalRef(imageClass);
    }
}

jobject convert2ModuleInfo(JNIEnv *env, const st_module_info *module_info){
    jclass module_info_cls = env->FindClass("com/sensetime/stmobile/sticker_module_types/STModuleInfo");

    jfieldID fieldId = env->GetFieldID(module_info_cls, "id", "I");
    jfieldID fieldPackageId = env->GetFieldID(module_info_cls, "packageId", "I");

    jfieldID fieldModuleType = env->GetFieldID(module_info_cls, "moduleType", "I");
    jfieldID fieldEnabled = env->GetFieldID(module_info_cls, "enabled", "Z");

    jfieldID fieldName = env->GetFieldID(module_info_cls, "name", "[B");

    jobject moduleInfoObj = env->AllocObject(module_info_cls);

    env->SetIntField(moduleInfoObj, fieldId, module_info->id);
    env->SetIntField(moduleInfoObj, fieldPackageId, module_info->package_id);
    env->SetIntField(moduleInfoObj, fieldModuleType, (int)module_info->type);
    env->SetBooleanField(moduleInfoObj, fieldEnabled, module_info->enabled);

    jbyteArray arrayName;
    jbyte* name = (jbyte*)(module_info->name);
    int len = strlen(module_info->name);
    arrayName = (env)->NewByteArray(len + 1);

    if(name == NULL){
        return NULL;
    }
    env->SetByteArrayRegion(arrayName, 0, len + 1, name);
    env->SetObjectField(moduleInfoObj, fieldName, arrayName);

    env->DeleteLocalRef(arrayName);
    env->DeleteLocalRef(module_info_cls);

    return moduleInfoObj;
}

bool convert2st_rect_t(JNIEnv *env, jobject rectObject, st_rect_t &rect){
    if(rectObject == NULL){
        return false;
    }

    jclass STRectClass = env->GetObjectClass(rectObject);

    if (STRectClass == NULL) {
        return false;
    }

    jfieldID rect_left = env->GetFieldID(STRectClass, "left", "I");
    jfieldID rect_top = env->GetFieldID(STRectClass, "top", "I");
    jfieldID rect_right = env->GetFieldID(STRectClass, "right", "I");
    jfieldID rect_bottom = env->GetFieldID(STRectClass, "bottom", "I");

    rect.left = env->GetIntField(rectObject, rect_left);
    rect.top = env->GetIntField(rectObject, rect_top);
    rect.right = env->GetIntField(rectObject, rect_right);
    rect.bottom = env->GetIntField(rectObject, rect_bottom);

    if(STRectClass != NULL){
        env->DeleteLocalRef(STRectClass);
    }

    return true;
}

bool convert2mobile_106(JNIEnv *env, jobject face106, st_mobile_106_t &mobile_106)
{
    if (face106 == NULL) {
        return false;
    }

    jclass st_mobile_106_class = env->FindClass("com/sensetime/stmobile/model/STMobile106");
    jfieldID frect = env->GetFieldID(st_mobile_106_class, "rect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fscore = env->GetFieldID(st_mobile_106_class, "score", "F");
    jfieldID fpoints_array = env->GetFieldID(st_mobile_106_class, "points_array", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fvisibility_array = env->GetFieldID(st_mobile_106_class, "visibility_array", "[F");
    jfieldID fyaw = env->GetFieldID(st_mobile_106_class, "yaw", "F");
    jfieldID fpitch = env->GetFieldID(st_mobile_106_class, "pitch", "F");
    jfieldID froll = env->GetFieldID(st_mobile_106_class, "roll", "F");
    jfieldID feye_dist = env->GetFieldID(st_mobile_106_class, "eye_dist", "F");
    jfieldID fID = env->GetFieldID(st_mobile_106_class, "ID", "I");

    jclass st_mobile_point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(st_mobile_point_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(st_mobile_point_class, "y", "F");

    jclass st_face_rect_class = env->FindClass("com/sensetime/stmobile/model/STRect");
    jfieldID frect_left = env->GetFieldID(st_face_rect_class, "left", "I");
    jfieldID frect_top = env->GetFieldID(st_face_rect_class, "top", "I");
    jfieldID frect_right = env->GetFieldID(st_face_rect_class, "right", "I");
    jfieldID frect_bottom = env->GetFieldID(st_face_rect_class, "bottom", "I");

    mobile_106.score = env->GetFloatField(face106, fscore);
    mobile_106.yaw = env->GetFloatField(face106, fyaw);
    mobile_106.pitch = env->GetFloatField(face106, fpitch);
    mobile_106.roll = env->GetFloatField(face106, froll);
    mobile_106.eye_dist = env->GetFloatField(face106, feye_dist);
    mobile_106.ID = env->GetIntField(face106, fID);

    jobject faceRect = env->GetObjectField(face106, frect);
    mobile_106.rect.left = env->GetIntField(faceRect, frect_left);
    mobile_106.rect.right = env->GetIntField(faceRect, frect_right);
    mobile_106.rect.top = env->GetIntField(faceRect, frect_top);
    mobile_106.rect.bottom = env->GetIntField(faceRect, frect_bottom);

    jobjectArray points_array = (jobjectArray)env->GetObjectField(face106, fpoints_array);
    jfloatArray face_visibility_array = (jfloatArray)env->GetObjectField(face106, fvisibility_array);
    jfloat* visibility_array = env->GetFloatArrayElements(face_visibility_array, 0);

    for (int j = 0; j < 106; ++j)
    {
        jobject point = env->GetObjectArrayElement(points_array, j);

        mobile_106.points_array[j].x = env->GetFloatField(point, fpoint_x);
        mobile_106.points_array[j].y = env->GetFloatField(point, fpoint_y);
        env->DeleteLocalRef(point);

        mobile_106.visibility_array[j] = visibility_array[j];
    }

    env->ReleaseFloatArrayElements(face_visibility_array, visibility_array, JNI_FALSE);
    env->DeleteLocalRef(face_visibility_array);
    env->DeleteLocalRef(points_array);
    env->DeleteLocalRef(faceRect);
    env->DeleteLocalRef(st_mobile_106_class);
    env->DeleteLocalRef(st_face_rect_class);
    env->DeleteLocalRef(st_mobile_point_class);

    return true;
}

bool convert2Image(JNIEnv *env, jobject image, st_image_t *background){
    if (image == NULL) {
        return false;
    }

    jclass image_cls = env->FindClass("com/sensetime/stmobile/model/STImage");

    jfieldID fieldImageData = env->GetFieldID(image_cls, "imageData", "[B");
    jfieldID fieldPixelFormat = env->GetFieldID(image_cls, "pixelFormat", "I");
    jfieldID fieldWidth = env->GetFieldID(image_cls, "width", "I");
    jfieldID fieldHeight = env->GetFieldID(image_cls, "height", "I");
    jfieldID fieldStride = env->GetFieldID(image_cls, "stride", "I");
    jfieldID fieldTime = env->GetFieldID(image_cls, "timeStamp", "D");

    jobject imageData = env->GetObjectField(image, fieldImageData);
    jbyteArray *arr = reinterpret_cast<jbyteArray*>(&imageData);
    jbyte* data = env->GetByteArrayElements(*arr, NULL);
    background->data = (unsigned char*)data;

    background->pixel_format = (st_pixel_format)env->GetIntField(image, fieldPixelFormat);
    background->width = env->GetIntField(image, fieldWidth);
    background->height = env->GetIntField(image, fieldHeight);
    background->stride = env->GetIntField(image, fieldStride);
    background->time_stamp = env->GetDoubleField(image, fieldTime);

    env->ReleaseByteArrayElements(*arr, data, JNI_FALSE);
    env->DeleteLocalRef(imageData);
    env->DeleteLocalRef(image_cls);

    //test for jni memory leak
    //jclass vm_class = env->FindClass("dalvik/system/VMDebug");
    //jmethodID dump_mid = env->GetStaticMethodID( vm_class, "dumpReferenceTables", "()V" );
    //env->CallStaticVoidMethod( vm_class, dump_mid );

    return true;
}

bool convert2HandInfo(JNIEnv *env, jobject handInfoObject, st_mobile_hand_t *hand_info){
    if (handInfoObject == NULL) {
        return false;
    }

    jclass hand_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileHandInfo");

    jfieldID fieldHandId = env->GetFieldID(hand_info_cls, "handId", "I");
    jfieldID fieldHandRect = env->GetFieldID(hand_info_cls, "handRect", "Lcom/sensetime/stmobile/model/STRect;");
    jfieldID fieldKeyPoints = env->GetFieldID(hand_info_cls, "keyPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsCount = env->GetFieldID(hand_info_cls, "keyPointsCount", "I");
    jfieldID fieldHandAction = env->GetFieldID(hand_info_cls, "handAction", "J");
    jfieldID fieldHandActionScore = env->GetFieldID(hand_info_cls, "handActionScore", "F");

    jclass hand_rect_class = env->FindClass("com/sensetime/stmobile/model/STRect");
    jfieldID hrect_left = env->GetFieldID(hand_rect_class, "left", "I");
    jfieldID hrect_top = env->GetFieldID(hand_rect_class, "top", "I");
    jfieldID hrect_right = env->GetFieldID(hand_rect_class, "right", "I");
    jfieldID hrect_bottom = env->GetFieldID(hand_rect_class, "bottom", "I");

    jobject handRectObj = env->GetObjectField(handInfoObject, fieldHandRect);
    hand_info->rect.left = env->GetIntField(handRectObj, hrect_left);
    hand_info->rect.top = env->GetIntField(handRectObj, hrect_top);
    hand_info->rect.right = env->GetIntField(handRectObj, hrect_right);
    hand_info->rect.bottom = env->GetIntField(handRectObj, hrect_bottom);

    hand_info->key_points_count = env->GetIntField(handInfoObject, fieldKeyPointsCount);

    //key_points
    hand_info->key_points_count = env->GetIntField(handInfoObject, fieldKeyPointsCount);

    if(hand_info->key_points_count > 0){
        jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
        jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
        jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

        jobjectArray key_points_array = (jobjectArray)env->GetObjectField(handInfoObject, fieldKeyPoints);
        hand_info->p_key_points = new st_pointf_t[hand_info->key_points_count];
        memset(hand_info->p_key_points, 0, sizeof(st_pointf_t)*hand_info->key_points_count);
        for (int i = 0; i < hand_info->key_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(key_points_array, i);

            (hand_info->p_key_points+i)->x = env->GetFloatField(point, fpoint_x);
            (hand_info->p_key_points+i)->y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(key_points_array);
        env->DeleteLocalRef(point_class);
    } else{
        hand_info->p_key_points = NULL;
    }

    hand_info->id = env->GetIntField(handInfoObject, fieldHandId);
    hand_info->hand_action = env->GetLongField(handInfoObject, fieldHandAction);
    hand_info->score = env->GetFloatField(handInfoObject, fieldHandActionScore);

    env->DeleteLocalRef(hand_info_cls);
    env->DeleteLocalRef(hand_rect_class);
    env->DeleteLocalRef(handRectObj);

    return true;
}

bool convert2FaceInfo(JNIEnv *env, jobject faceInfoObject, st_mobile_face_t *face_info){
    if (faceInfoObject == NULL) {
        return false;
    }

    jclass face_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileFaceInfo");

    jfieldID fieldFace = env->GetFieldID(face_info_cls, "face106", "Lcom/sensetime/stmobile/model/STMobile106;");

    jfieldID fieldExtraFacePoints = env->GetFieldID(face_info_cls, "extraFacePoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldExtraFacePointsCount = env->GetFieldID(face_info_cls, "extraFacePointsCount", "I");

    jfieldID fieldEyeballCenter = env->GetFieldID(face_info_cls, "eyeballCenter", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldEyeballCenterPointsCount = env->GetFieldID(face_info_cls, "eyeballCenterPointsCount", "I");

    jfieldID fieldEyeballContour = env->GetFieldID(face_info_cls, "eyeballContour", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldEyeballContourPointsCount = env->GetFieldID(face_info_cls, "eyeballContourPointsCount", "I");

    jfieldID fieldLeftEyeballScore = env->GetFieldID(face_info_cls, "leftEyeballScore", "F");
    jfieldID fieldRightEyeballScore = env->GetFieldID(face_info_cls, "rightEyeballScore", "F");

    jfieldID fieldFaceAction = env->GetFieldID(face_info_cls, "faceAction", "J");

    //face106
    jobject face106Obj = env->GetObjectField(faceInfoObject, fieldFace);
    convert2mobile_106(env, face106Obj, face_info->face106);
    env->DeleteLocalRef(face106Obj);

    jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
    jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
    jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

    //extra_face_points
    face_info->extra_face_points_count = env->GetIntField(faceInfoObject, fieldExtraFacePointsCount);

    if(face_info->extra_face_points_count > 0){
        jobjectArray extra_face_points_array = (jobjectArray)env->GetObjectField(faceInfoObject, fieldExtraFacePoints);
        face_info->p_extra_face_points = new st_pointf_t[face_info->extra_face_points_count];
        memset(face_info->p_extra_face_points, 0, sizeof(st_pointf_t)*face_info->extra_face_points_count);
        for (int i = 0; i < face_info->extra_face_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(extra_face_points_array, i);

            face_info->p_extra_face_points[i].x = env->GetFloatField(point, fpoint_x);
            face_info->p_extra_face_points[i].y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(extra_face_points_array);
    }else{
        face_info->p_extra_face_points = NULL;
    }

    face_info->left_eyeball_score = env->GetFloatField(faceInfoObject, fieldLeftEyeballScore);
    face_info->right_eyeball_score = env->GetFloatField(faceInfoObject, fieldRightEyeballScore);

    //eyeball_center
    face_info->eyeball_center_points_count = env->GetIntField(faceInfoObject, fieldEyeballCenterPointsCount);

    if(face_info->eyeball_center_points_count > 0){
        jobjectArray eyeball_center_array = (jobjectArray)env->GetObjectField(faceInfoObject, fieldEyeballCenter);

        face_info->p_eyeball_center = new st_pointf_t[face_info->eyeball_center_points_count];
        memset(face_info->p_eyeball_center, 0, sizeof(st_pointf_t)*face_info->eyeball_center_points_count);
        for (int i = 0; i < face_info->eyeball_center_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(eyeball_center_array, i);

            face_info->p_eyeball_center[i].x = env->GetFloatField(point, fpoint_x);
            face_info->p_eyeball_center[i].y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(eyeball_center_array);
    }else{
        face_info->p_eyeball_center = NULL;
    }

    //eyeball_contour
    face_info->eyeball_contour_points_count = env->GetIntField(faceInfoObject, fieldEyeballContourPointsCount);

    if(face_info->eyeball_contour_points_count > 0){
        jobjectArray eyeball_contour_array = (jobjectArray)env->GetObjectField(faceInfoObject, fieldEyeballContour);

        face_info->p_eyeball_contour = new st_pointf_t[face_info->eyeball_contour_points_count];
        memset(face_info->p_eyeball_contour, 0, sizeof(st_pointf_t)*face_info->eyeball_contour_points_count);
        for (int i = 0; i < face_info->eyeball_contour_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(eyeball_contour_array, i);

            face_info->p_eyeball_contour[i].x = env->GetFloatField(point, fpoint_x);
            face_info->p_eyeball_contour[i].y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(eyeball_contour_array);
    } else{
        face_info->p_eyeball_contour = NULL;
    }

    face_info->face_action = env->GetLongField(faceInfoObject, fieldFaceAction);


    env->DeleteLocalRef(point_class);
    env->DeleteLocalRef(face_info_cls);

    return true;
}

bool convert2BodyInfo(JNIEnv *env, jobject bodyInfoObject, st_mobile_body_t *body_info){
    if (bodyInfoObject == NULL) {
        return false;
    }

    jclass body_info_cls = env->FindClass("com/sensetime/stmobile/model/STMobileBodyInfo");

    jfieldID fieldBodyId = env->GetFieldID(body_info_cls, "id", "I");
    jfieldID fieldKeyPoints = env->GetFieldID(body_info_cls, "keyPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldKeyPointsScore = env->GetFieldID(body_info_cls, "keyPointsScore", "[F");
    jfieldID fieldKeyPointsCount = env->GetFieldID(body_info_cls, "keyPointsCount", "I");
    jfieldID fieldContourPoints = env->GetFieldID(body_info_cls, "contourPoints", "[Lcom/sensetime/stmobile/model/STPoint;");
    jfieldID fieldContourPointsScore = env->GetFieldID(body_info_cls, "contourPointsScore", "[F");
    jfieldID fieldContourPointsCount = env->GetFieldID(body_info_cls, "contourPointsCount", "I");
    jfieldID fieldBodyAction = env->GetFieldID(body_info_cls, "bodyAction", "J");
    jfieldID fieldBodyActionScore = env->GetFieldID(body_info_cls, "bodyActionScore", "F");

    //key_points
    body_info->key_points_count = env->GetIntField(bodyInfoObject, fieldKeyPointsCount);

    if(body_info->key_points_count > 0){
        jfloatArray score_array= (jfloatArray)env->GetObjectField(bodyInfoObject, fieldKeyPointsScore);
        float* scores = env->GetFloatArrayElements(score_array, 0);

        body_info->p_key_points_score = new float[body_info->key_points_count];
        memset(body_info->p_key_points_score, 0, sizeof(float)*body_info->key_points_count);
        memcpy(body_info->p_key_points_score, scores, sizeof(float)*body_info->key_points_count);

        env->ReleaseFloatArrayElements(score_array, scores, JNI_FALSE);
        env->DeleteLocalRef(score_array);

        jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
        jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
        jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

        jobjectArray key_points_array = (jobjectArray)env->GetObjectField(bodyInfoObject, fieldKeyPoints);
        body_info->p_key_points = new st_pointf_t[body_info->key_points_count];
        memset(body_info->p_key_points, 0, sizeof(st_pointf_t)*body_info->key_points_count);
        for (int i = 0; i < body_info->key_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(key_points_array, i);
            (body_info->p_key_points+i)->x = env->GetFloatField(point, fpoint_x);
            (body_info->p_key_points+i)->y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(key_points_array);
        env->DeleteLocalRef(point_class);
    } else{
        body_info->p_key_points = NULL;
        body_info->p_key_points_score = NULL;
    }

    //contour_points
    body_info->contour_points_count = env->GetIntField(bodyInfoObject, fieldContourPointsCount);

    if(body_info->contour_points_count > 0){
        jfloatArray score_array= (jfloatArray)env->GetObjectField(bodyInfoObject, fieldContourPointsScore);
        float* scores = env->GetFloatArrayElements(score_array, 0);

        body_info->p_contour_points_score = new float[body_info->contour_points_count];
        memset(body_info->p_contour_points_score, 0, sizeof(float)*body_info->contour_points_count);
        memcpy(body_info->p_contour_points_score, scores, sizeof(float)*body_info->contour_points_count);

        env->ReleaseFloatArrayElements(score_array, scores, JNI_FALSE);
        env->DeleteLocalRef(score_array);

        jclass point_class = env->FindClass("com/sensetime/stmobile/model/STPoint");
        jfieldID fpoint_x = env->GetFieldID(point_class, "x", "F");
        jfieldID fpoint_y = env->GetFieldID(point_class, "y", "F");

        jobjectArray contour_points_array = (jobjectArray)env->GetObjectField(bodyInfoObject, fieldContourPoints);
        body_info->p_contour_points = new st_pointf_t[body_info->contour_points_count];
        memset(body_info->p_contour_points, 0, sizeof(st_pointf_t)*body_info->contour_points_count);
        for (int i = 0; i < body_info->contour_points_count; ++i) {
            jobject point = env->GetObjectArrayElement(contour_points_array, i);
            (body_info->p_contour_points+i)->x = env->GetFloatField(point, fpoint_x);
            (body_info->p_contour_points+i)->y = env->GetFloatField(point, fpoint_y);
            env->DeleteLocalRef(point);
        }

        env->DeleteLocalRef(contour_points_array);
        env->DeleteLocalRef(point_class);
    } else{
        body_info->p_contour_points = NULL;
        body_info->p_contour_points_score = NULL;
    }

    body_info->id = env->GetIntField(bodyInfoObject, fieldBodyId);
    body_info->body_action = env->GetLongField(bodyInfoObject, fieldBodyAction);
    body_info->body_action_score = env->GetFloatField(bodyInfoObject, fieldBodyActionScore);

    env->DeleteLocalRef(body_info_cls);

    return true;
}

bool convert2HumanAction(JNIEnv *env, jobject humanActionObject, st_mobile_human_action_t *human_action){
    if (humanActionObject == NULL) {
        return false;
    }

    jclass human_action_cls = env->FindClass("com/sensetime/stmobile/model/STHumanAction");

    jfieldID fieldFaces = env->GetFieldID(human_action_cls, "faces", "[Lcom/sensetime/stmobile/model/STMobileFaceInfo;");
    jfieldID fieldFaceCount = env->GetFieldID(human_action_cls, "faceCount", "I");

    jfieldID fieldHands = env->GetFieldID(human_action_cls, "hands", "[Lcom/sensetime/stmobile/model/STMobileHandInfo;");
    jfieldID fieldHandCount = env->GetFieldID(human_action_cls, "handCount", "I");

    jfieldID fieldBodys = env->GetFieldID(human_action_cls, "bodys", "[Lcom/sensetime/stmobile/model/STMobileBodyInfo;");
    jfieldID fieldBodyCount = env->GetFieldID(human_action_cls, "bodyCount", "I");

    jfieldID fieldImage = env->GetFieldID(human_action_cls, "image", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldBackGroundScore = env->GetFieldID(human_action_cls, "backGroundScore", "F");

    jfieldID fieldHair = env->GetFieldID(human_action_cls, "hair", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldHairScore = env->GetFieldID(human_action_cls, "hairScore", "F");

    jfieldID fieldMultiSegment = env->GetFieldID(human_action_cls, "multiSegment", "Lcom/sensetime/stmobile/model/STImage;");
    jfieldID fieldMultiSegmentScore = env->GetFieldID(human_action_cls, "multiSegmentScore", "F");

    //faces
    human_action->face_count = env->GetIntField(humanActionObject, fieldFaceCount);

    if(human_action->face_count > 0){
        jobjectArray faces_obj_array = (jobjectArray)env->GetObjectField(humanActionObject, fieldFaces);

        human_action->p_faces = new st_mobile_face_t[human_action->face_count];
        memset(human_action->p_faces, 0, sizeof(st_mobile_face_t)*human_action->face_count);
        for(int i = 0; i < human_action->face_count; i++){
            jobject facesObj = env->GetObjectArrayElement(faces_obj_array, i);
            convert2FaceInfo(env, facesObj, human_action->p_faces+i);

            env->DeleteLocalRef(facesObj);
        }
        env->DeleteLocalRef(faces_obj_array);
    } else {
        human_action->p_faces = NULL;
    }

    //hands
    human_action->hand_count = env->GetIntField(humanActionObject, fieldHandCount);

    if(human_action->hand_count > 0){
        jobjectArray hands_obj_array = (jobjectArray)env->GetObjectField(humanActionObject, fieldHands);

        human_action->p_hands = new st_mobile_hand_t[human_action->hand_count];
        memset(human_action->p_hands, 0, sizeof(st_mobile_hand_t)*human_action->hand_count);
        for(int i = 0; i < human_action->hand_count; i++){
            jobject handsObj = env->GetObjectArrayElement(hands_obj_array, i);
            convert2HandInfo(env, handsObj, human_action->p_hands+i);

            env->DeleteLocalRef(handsObj);
        }

        env->DeleteLocalRef(hands_obj_array);
    } else{
        human_action->p_hands = NULL;
    }

    //bodys
    human_action->body_count = env->GetIntField(humanActionObject, fieldBodyCount);

    if(human_action->body_count > 0){
        jobjectArray bodys_obj_array = (jobjectArray)env->GetObjectField(humanActionObject, fieldBodys);

        human_action->p_bodys = new st_mobile_body_t[human_action->body_count];
        memset(human_action->p_bodys, 0, sizeof(st_mobile_body_t)*human_action->body_count);
        for(int i = 0; i < human_action->body_count; i++){
            jobject bodysObj = env->GetObjectArrayElement(bodys_obj_array, i);
            convert2BodyInfo(env, bodysObj, human_action->p_bodys+i);

            env->DeleteLocalRef(bodysObj);
        }

        env->DeleteLocalRef(bodys_obj_array);
    } else{
        human_action->p_bodys = NULL;
    }

    //image
    human_action->background_score = env->GetFloatField(humanActionObject, fieldBackGroundScore);
    jobject imageObj = env->GetObjectField(humanActionObject, fieldImage);

    if(imageObj != NULL){
        human_action->p_background = new st_image_t;
        memset(human_action->p_background, 0, sizeof(st_image_t));

        convert2Image(env, imageObj, human_action->p_background);
    } else{
        human_action->p_background = NULL;
    }

    env->DeleteLocalRef(imageObj);

    //hair
    human_action->hair_score = env->GetFloatField(humanActionObject, fieldHairScore);
    jobject hairObj = env->GetObjectField(humanActionObject, fieldHair);

    if(hairObj != NULL){
        human_action->p_hair = new st_image_t;
        memset(human_action->p_hair, 0, sizeof(st_image_t));

        convert2Image(env, hairObj, human_action->p_hair);
    } else{
        human_action->p_hair = NULL;
    }

    env->DeleteLocalRef(hairObj);

    //MultiSegment
    human_action->multi_segment_score = env->GetFloatField(humanActionObject, fieldMultiSegmentScore);
    jobject multiSegmentObj = env->GetObjectField(humanActionObject, fieldMultiSegment);

    if(multiSegmentObj != NULL){
        human_action->p_multi_segment = new st_image_t;
        memset(human_action->p_multi_segment, 0, sizeof(st_image_t));

        convert2Image(env, multiSegmentObj, human_action->p_multi_segment);
    } else{
        human_action->p_multi_segment = NULL;
    }

    env->DeleteLocalRef(hairObj);

    env->DeleteLocalRef(human_action_cls);

    return true;
}

void releaseHumanAction(st_mobile_human_action_t *human_action){
    if(human_action == NULL){
        return;
    }

    for(int i = 0; i < human_action->face_count; i++){
        safe_delete_array((human_action->p_faces+i)->p_extra_face_points);
        safe_delete_array((human_action->p_faces+i)->p_eyeball_center);
        safe_delete_array((human_action->p_faces+i)->p_eyeball_contour);
    }
    for(int i = 0; i < human_action->hand_count; i++){
        safe_delete_array((human_action->p_hands+i)->p_key_points);
    }
    for(int i = 0; i < human_action->body_count; i++){
        safe_delete_array((human_action->p_bodys+i)->p_key_points);
        safe_delete_array((human_action->p_bodys+i)->p_key_points_score);

        safe_delete_array((human_action->p_bodys+i)->p_contour_points);
        safe_delete_array((human_action->p_bodys+i)->p_contour_points_score);
    }

    safe_delete(human_action->p_faces);
    safe_delete(human_action->p_hands);
    safe_delete(human_action->p_bodys);
    safe_delete(human_action->p_background);
    safe_delete(human_action->p_hair);
    safe_delete(human_action->p_multi_segment);
}

bool convert2Condition(JNIEnv *env, jobject conditionObject, st_condition &condition){
    if(conditionObject == NULL){
        return false;
    }
    jclass st_condition_class = env->FindClass("com/sensetime/stmobile/model/STCondition");
    jfieldID fpreStateModuleId = env->GetFieldID(st_condition_class, "preStateModuleId", "I");
    jfieldID fpreState = env->GetFieldID(st_condition_class, "preState", "I");
    jfieldID ftriggers = env->GetFieldID(st_condition_class, "triggers", "[Lcom/sensetime/stmobile/model/STTriggerEvent;");
    jfieldID ftriggerCount = env->GetFieldID(st_condition_class, "triggerCount", "I");

    condition.pre_state_module_id = env->GetIntField(conditionObject, fpreStateModuleId);
    condition.pre_state = (st_animation_state_type)env->GetIntField(conditionObject, fpreState);
    condition.trigger_count = env->GetIntField(conditionObject, ftriggerCount);

    if(condition.trigger_count > 0){
        jobjectArray trigger_obj_array = (jobjectArray)env->GetObjectField(conditionObject, ftriggers);

        condition.triggers = new st_trigger_event[condition.trigger_count];
        memset(condition.triggers, 0, sizeof(st_trigger_event)*condition.trigger_count);
        for(int i = 0; i < condition.trigger_count; i++){
            jobject triggersObj = env->GetObjectArrayElement(trigger_obj_array, i);
            convert2TriggerEvent(env, triggersObj, condition.triggers[i]);

            env->DeleteLocalRef(triggersObj);
        }

        env->DeleteLocalRef(trigger_obj_array);
    } else{
        condition.triggers = NULL;
    }

    env->DeleteLocalRef(st_condition_class);

    return true;
}

bool convert2TransParam(JNIEnv *env, jobject paramObject, st_trans_param &param){
    if(paramObject == NULL){
        return false;
    }
    jclass st_param_class = env->FindClass("com/sensetime/stmobile/model/STTransParam");
    jfieldID fadeFrame = env->GetFieldID(st_param_class, "fadeFrame", "I");
    jfieldID fdelayFrame = env->GetFieldID(st_param_class, "delayFrame", "I");
    jfieldID flastingFrame = env->GetFieldID(st_param_class, "lastingFrame", "I");
    jfieldID fplayloop = env->GetFieldID(st_param_class, "playloop", "I");

    param.fade_frame = env->GetIntField(paramObject, fadeFrame);
    param.delay_frame = env->GetIntField(paramObject, fdelayFrame);
    param.lasting_frame = env->GetIntField(paramObject, flastingFrame);
    param.play_loop = env->GetIntField(paramObject, fplayloop);

    env->DeleteLocalRef(st_param_class);

    return true;
}

bool convert2TriggerEvent(JNIEnv *env, jobject triggerEventObject, st_trigger_event &trigger_event){
    if(triggerEventObject == NULL){
        return false;
    }

    jclass st_trigger_event_class = env->FindClass("com/sensetime/stmobile/model/STTriggerEvent");
    jfieldID ftriggerType = env->GetFieldID(st_trigger_event_class, "triggerType", "I");
    jfieldID ftrigger = env->GetFieldID(st_trigger_event_class, "trigger", "J");
    jfieldID fmoduleId = env->GetFieldID(st_trigger_event_class, "moduleId", "I");
    jfieldID fisAppear = env->GetFieldID(st_trigger_event_class, "isAppear", "Z");

    trigger_event.triggerType = (st_trigger_event_type) env->GetIntField(triggerEventObject, ftriggerType);
    trigger_event.trigger = env->GetLongField(triggerEventObject, ftrigger);
    trigger_event.module_id = env->GetIntField(triggerEventObject, fmoduleId);
    trigger_event.is_appear = env->GetBooleanField(triggerEventObject, fisAppear);

    env->DeleteLocalRef(st_trigger_event_class);
}

bool convert2StickerInputParams(JNIEnv *env, jobject eventObject, st_mobile_input_params &input_params){
    if(eventObject == NULL){
        return false;
    }
    jclass st_input_event_class = env->FindClass("com/sensetime/stmobile/model/STStickerInputParams");
    jfieldID fieldQuaternion = env->GetFieldID(st_input_event_class, "cameraQuaternion", "[F");
    jfieldID fieldLength = env->GetFieldID(st_input_event_class, "quaternionLength", "I");
    jfieldID fieldCameraId = env->GetFieldID(st_input_event_class, "isFrontCamera", "Z");

    jfieldID fieldEvent = env->GetFieldID(st_input_event_class, "customEvent", "I");

    int length = env->GetIntField(eventObject, fieldLength);
    if(length >= 4){
        jfloatArray quaternion_array= (jfloatArray)env->GetObjectField(eventObject, fieldQuaternion);
        float* values = env->GetFloatArrayElements(quaternion_array, 0);

        for(int i = 0; i< 4; i++){
            input_params.camera_quaternion[i] = values[i];
        }
        env->ReleaseFloatArrayElements(quaternion_array, values, 0);
        env->DeleteLocalRef(quaternion_array);
    }

    bool isFrontCamera = env->GetBooleanField(eventObject, fieldCameraId);
    input_params.is_front_camera = isFrontCamera;

    input_params.custom_event = env->GetIntField(eventObject, fieldEvent);

    env->DeleteLocalRef(st_input_event_class);

    return true;
}