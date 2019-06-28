# MovieousShortVideo-Android

MovieousShortVideo-Android 是 Movieous 推出的一款适用于 Android 平台的短视频 SDK，提供了视频录制、视频编辑、美颜、滤镜、分段录制、云端存储等多种功能，支持高度定制以及二次开发, 能够让开发者快速构建一款优秀的短视频 app。

*其他语言版本: [English](README.en-us.md), [简体中文](README.md).*

## 功能特性

- [x] 支持视频录制/编辑
- [x] 分段录制
- [x] 回删视频
- [x] 支持自动对焦
- [x] 支持手动对焦
- [x] 支持麦克风静音
- [x] 支持闪光灯操作
- [x] 支持摄像头动态切换
- [x] 支持水印
- [x] 视频拼接
- [x] 视频片段剪辑
- [x] 支持 H.264 和 AAC 硬编
- [x] 第三方美颜接口
- [x] 第三方滤镜接口
- [x] 大眼/瘦脸 [联系商务](mailto:sales@movieous.com)
- [x] 贴纸特效  [联系商务](mailto:sales@movieous.com)
- [x] 抖音滤镜  [联系商务](mailto:sales@movieous.com)
- [x] 背景替换  [联系商务](mailto:sales@movieous.com)
- [x] 表情特效  [联系商务](mailto:sales@movieous.com)
- [x] 手势识别  [联系商务](mailto:sales@movieous.com)
- [x] 上传云端
- [x] 断点续传
- [x] 支持 arm64、armv7、armeabi、x86 等主流芯片体系架构
- [x] 支持 Android API 18（Android 4.3）及其以上版本

## MovieousShortVideo-Android Wiki

请参考 wiki 文档：[MovieousShortVideo-Android 开发指南](https://developer.movieous.cn/#/Android_ShortVideo)

## 设备以及系统要求

- 设备要求：搭载 Android 系统的设备
- 系统要求：Android 4.3(API 18) 及其以上

## 反馈及意见

当你遇到任何问题时，可以通过在 GitHub 的 repo 提交 issues 来反馈问题，请尽可能的描述清楚遇到的问题，如果有错误信息也一同附带，并且在 Labels 中指明类型为 bug 或者其他。

[通过这里查看已有的 issues 和提交 Bug。](https://github.com/movieous-team/MovieousShortVideo-Android-Release/issues)

## 鉴权说明

- SDK 的使用需要进行官方鉴权

- 鉴权方法

在官网创建 app 后，控制台会生成 license.txt 鉴权文件，通过 `UShortVideoEnv` 中的 `init` 接口，把 license.txt 文件中的字符串传递给 SDK 进行鉴权，具体请参考 SDK 文档接口说明。

- [联系商务](mailto:sales@movieous.com)