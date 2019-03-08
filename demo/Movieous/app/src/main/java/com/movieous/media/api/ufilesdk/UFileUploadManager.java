package com.movieous.media.api.ufilesdk;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.ObjectRemoteAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.auth.UfileObjectRemoteAuthorization;
import cn.ucloud.ufile.http.UfileCallback;

import java.io.File;

/**
 * UCloud 云存储上传参考
 * <p>
 * App 中需要增加如下依赖库：
 * implementation 'cn.ucloud.ufile:ufile-client-java:2.0.6'
 */
public final class UFileUploadManager {
    private static final String TAG = "UFileUploadManager";
    private static final String MIME_TYPE = "video/mp4";

    private ObjectAuthorization authorization;   // 签名
    private ObjectConfig objectConfig;

    /**
     * 通过公钥/私钥方式进行鉴权，仅做测试之用，安全性低
     *
     * @param region      存储区域 eg: "cn-sh2"
     * @param proxySuffix 域名后缀 eg: "ufileos.com"
     * @param publicKey   公钥
     * @param privateKey  私钥
     */
    @Deprecated
    public UFileUploadManager(String region, String proxySuffix, String publicKey, String privateKey) {
        authorization = new UfileObjectLocalAuthorization(publicKey, privateKey);
        objectConfig = new ObjectConfig(region, proxySuffix);
    }

    /**
     * 通过鉴权服务器进行鉴权
     *
     * @param region                   存储区域 eg: "cn-sh2"
     * @param proxySuffix              域名后缀 eg: "ufileos.com"
     * @param publicKey                公钥
     * @param objectOptAuthServer      签名服务 eg："http://your_domain/applyAuth",
     * @param objectDownloadAuthServer 私有下载授权签名 eg: "http://your_domain/applyPrivateUrlAuth"
     */
    public UFileUploadManager(String region, String proxySuffix, String publicKey, String objectOptAuthServer, String objectDownloadAuthServer) {
        authorization = new UfileObjectRemoteAuthorization(publicKey, new ObjectRemoteAuthorization.ApiConfig(objectOptAuthServer, objectDownloadAuthServer));
        objectConfig = new ObjectConfig(region, proxySuffix);
    }

    /**
     * upload file by public token & private token, only for debug
     *
     * @param filePath   文件路径
     * @param bucket     bucket name
     * @param callback   状态回调
     */
    public void startUpload(String filePath, String bucket, UfileCallback callback) {
        final File file = new File(filePath);
        UfileClient.object(authorization, objectConfig)
                .putObject(file, MIME_TYPE)
                .nameAs(file.getName())
                .toBucket(bucket)
                .executeAsync(callback);
    }

}
