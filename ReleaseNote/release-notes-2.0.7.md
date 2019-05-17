# Movieous Shortvideo Release Notes for 2.0.7

## 简介

MovieousShortVideo-Android 是一款适用于 Android 平台的短视频 SDK，支持高度定制以及二次开发。

## 版本

* 发布 ushortvideo-2.0.7.jar

## 功能

* 增加拍照、录制时自动打开闪光灯接口
* 增加获取视频文件指定时间点的缩略图接口
* 图片编辑时适配图像方向
* 增加 ScaleType.CENTER_INSIDE 缩放机制
* 增加设置渲染背景接口
* 规范 log 输出，更便于问题定位
* 升级播放器到 MovieousPlayer v2.0.7

## 缺陷

* 修复 FU 保存视频时前几帧没有美颜滤镜 bug
* 修复没有经过完整生命周期调用导致的预览尺寸获取异常 bug
* 修复进入 pause 状态后，偶现的视频渲染数据回调 bug

## 注意事项
