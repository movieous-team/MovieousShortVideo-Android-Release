package com.movieous.media.mvp.model.entity

import java.io.Serializable

class VideoListItem : Serializable {

    var avatarRes: Int = 0
    var videoUrl: String? = null
    var userName: String? = null
    var content: String? = null
    var coverUrl: String? = null
    var avatarUrl: String? = null
    var videoWidth: Int = 0
    var videoHeight: Int = 0

    constructor(avatarRes: Int, videoUrl: String, userName: String, content: String, coverUrl: String, videoWidth: Int, videoHeight: Int) {
        this.avatarRes = avatarRes
        this.videoUrl = videoUrl
        this.userName = userName
        this.content = content
        this.coverUrl = coverUrl
        this.videoWidth = videoWidth
        this.videoHeight = videoHeight
        this.avatarUrl = null
    }

    constructor(videoUrl: String, userName: String, content: String, coverUrl: String, avatarUrl: String, videoWidth: Int, videoHeight: Int) {
        this.avatarRes = 0
        this.videoUrl = videoUrl
        this.userName = userName
        this.content = content
        this.coverUrl = coverUrl
        this.avatarUrl = avatarUrl
        this.videoWidth = videoWidth
        this.videoHeight = videoHeight
    }
}
