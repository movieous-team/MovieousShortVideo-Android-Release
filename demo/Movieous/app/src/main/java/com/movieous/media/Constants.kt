package com.movieous.media

object Constants {
    // 重要：请替换成您的鉴权信息.
    // ufile 云存储参考文档: https://docs.ucloud.cn/storage_cdn/ufile/index
    const val DOMAIN = "" // UCloud 存储空间域名
    const val PUBLIC_KEY = "" // 上传公钥
    // 仅做测试之用，安全性低
    const val PRIVATE_KEY = "" // 上传私钥
    // 通过鉴权服务器进行身份验证，推荐
    const val APPLY_AUTH_URL = "" // 鉴权服务器 eg： "http://your_domain/applyAuth",
    const val APPLY_PRIVATE_AUTH_URL = "" // 鉴权服务器 eg： "http://your_domain/applyPrivateUrlAuth"
    const val BUCKET = "" // bucket 名称
    const val REGION = "cn-sh2"
    const val PROXY_SUFFIX = "ufileos.com" // 存储空间域名后缀
    const val MEDIA_FILE_PREFIX = "shortvideo" // 短视频文件前缀
    const val THUMB_FILE_PREFIX = "thumb"  // 视频封面文件前缀
    const val BASE_URL = "https://api.movieous.cn"
    const val MOVIEOUS_SIGN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcHBpZCI6ImNvbS5tb3ZpZW91cy5tZWRpYSJ9.USaTo0E_1DZoxNMc51HMqP-ixIt9k-rzjPVuoMQLqJM"

    // 最大录制时长
    const val DEFAULT_MAX_RECORD_DURATION = 15 * 1000
    // 编码码率
    const val DEFAULT_ENCODING_BITRATE = 2500 * 1000

    // 存储目录设置
    const val VIDEO_STORAGE_DIR = "/sdcard/movieous/shortvideo/"
    const val RECORD_FILE_PATH = VIDEO_STORAGE_DIR + "record.mp4"
    const val TITLE_FILE_PATH = VIDEO_STORAGE_DIR + "title.mp4"
    const val TAIL_FILE_PATH = VIDEO_STORAGE_DIR + "tail.mp4"
    const val EDIT_FILE_PATH = VIDEO_STORAGE_DIR + "edit.mp4"
    const val COVER_FILE_PATH = VIDEO_STORAGE_DIR + "cover.gif"
    const val MERGE_FILE_PATH = VIDEO_STORAGE_DIR + "merge.mp4"

    // 变速录制/编辑参数
    const val VIDEO_SPEED_SUPER_SLOW = 0.25
    const val VIDEO_SPEED_SLOW = 0.5
    const val VIDEO_SPEED_NORMAL = 1
    const val VIDEO_SPEED_FAST = 2
    const val VIDEO_SPEED_SUPER_FAST = 4

    const val SPAN_COUNT = 6
}