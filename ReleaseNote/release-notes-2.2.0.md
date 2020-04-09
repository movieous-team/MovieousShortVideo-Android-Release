# Movieous Shortvideo Release Notes for 2.2.0

## 简介

`MovieousShortVideo-Android` 是一款适用于 `Android` 平台的短视频 `SDK`，支持高度定制以及二次开发。

## 版本

* 发布 `movieous-shortvideo-2.2.0.jar`

## 功能

* 优化鉴权机制

## 缺陷

## 其他

* `Demo` 中播放器版本升级到 `movieous-player-2.1.1.aar`

## 注意事项

* 从 `v2.1.9` 版本开始，以下类的位置或名称发生了变化：
  * video.movieous.engine.base.utils.ULog -> video.movieous.media.ULog
  * video.movieous.engine.UVideoFrameListener -> video.movieous.media.listener.UVideoFrameListener
  * video.movieous.engine.UVideoSaveListener -> video.movieous.media.listener.UVideoSaveListener
  * video.movieous.engine.URecordListener -> video.movieous.media.listener.URecordListener
  * video.movieous.engine.UMediaTrimTime -> video.movieous.media.model.UMediaTime
  * video.movieous.shortvideo.UVideoPlayListener -> video.movieous.media.listener.UMediaPlayListener

* 删除 `setMediaPlayer` 接口
