#ifndef INCLUDE_STMOBILE_ST_MOBILE_LICENSE_H_
#define INCLUDE_STMOBILE_ST_MOBILE_LICENSE_H_
#include "st_mobile_common.h"
#include <jni.h>


/// @brief 根据授权文件生成激活码, 在使用新的license文件时使用. Android建议使用st_mobile_generate_activecode_from_buffer
/// @param[in] license_path license文件路径
/// @param[out] activation_code 返回当前设备的激活码,由用户分配内存, 建议分配1024字节
/// @param[in,out] activation_code_len 输入为active_code的内存大小, 返回当前设备的激活码字节长度（包含激活码字符串结束符'\0'）
/// @return 正常返回ST_OK,否则返回错误类型
ST_SDK_API st_result_t
st_mobile_generate_activecode(
    JNIEnv* env,
    jobject context,
    const char* license_path,
    char* activation_code,
    int* activation_code_len
);

/// @brief 检查激活码, 必须在所有接口之前调用. Android建议使用st_mobile_check_activecode_from_buffer
/// @param[in] license_path license文件路径
/// @param[in] activation_code 当前设备的激活码
/// @param[in] activation_code_len 激活码的长度
/// @return 正常返回ST_OK,否则返回错误类型
ST_SDK_API st_result_t
st_mobile_check_activecode(
    JNIEnv* env,
    jobject context,
    const char* license_path,
    const char* activation_code,
    int activation_code_len
);

/// @brief 根据授权文件缓存生成激活码, 在使用新的license文件时调用
/// @param[in] license_buf license文件缓存地址
/// @param[in] license_size license文件缓存大小
/// @param[out] activation_code 返回当前设备的激活码, 由用户分配内存; 建议分配1024字节
/// @param[in, out] activation_code_len 输入为activation_code分配的内存大小, 返回生成的设备激活码的字节长度（包含激活码字符串结束符'\0'）
/// @return 正常返回ST_OK, 否则返回错误类型
ST_SDK_API st_result_t
st_mobile_generate_activecode_from_buffer(
    JNIEnv* env,
    jobject context,
    const char* license_buf,
    int license_size,
    char* activation_code,
    int* activation_code_len
);

/// @brief 检查激活码, 必须在所有接口之前调用
/// @param[in] license_buf license文件缓存
/// @param[in] license_size license文件缓存大小
/// @param[in] activation_code 当前设备的激活码
/// @param[in] activation_code_len 激活码的长度
/// @return 正常返回ST_OK, 否则返回错误类型
ST_SDK_API st_result_t
st_mobile_check_activecode_from_buffer(
    JNIEnv* env,
    jobject context,
    const char* license_buf,
    int license_size,
    const char* activation_code,
    int activation_code_len
);

/// @brief 根据授权文件在线生成激活码, 需要使用在线license和联网
/// @param[in] license_path license文件路径
/// @param[out] activation_code 返回当前设备的激活码,由用户分配内存, 建议分配1024字节
/// @param[in,out] activation_code_len 输入为active_code的内存大小, 返回当前设备的激活码字节长度（包含激活码字符串结束符'\0'）
/// @return 正常返回ST_OK,否则返回错误类型
ST_SDK_API st_result_t
st_mobile_generate_activecode_online(
			      //>> CONFIG_ST_MOBILE_
#if (defined(__ANDROID__))
			      //>> CONFIG_API_END__
			      //>> CONFIG_ST_MOBILE_USE_ANDROID
			      JNIEnv* env,
			      jobject context,
			      //>> CONFIG_API_END__
			      //>> CONFIG_ST_MOBILE_
#endif
			      //>> CONFIG_API_END__
			      const char* license_path,
			      char* activation_code,
			      int* activation_code_len
			      );


/// @brief 根据授权文件buffer在线生成激活码, 需要使用在线license和联网
/// @param[in] license_buf license文件缓存地址
/// @param[in] license_size license文件缓存大小
/// @param[out] activation_code 返回当前设备的激活码, 由用户分配内存; 建议分配1024字节
/// @param[in, out] activation_code_len 输入为activation_code分配的内存大小, 返回生成的设备激活码的字节长度（包含激活码字符串结束符'\0'）
/// @return 正常返回ST_OK, 否则返回错误类型
ST_SDK_API st_result_t
st_mobile_generate_activecode_from_buffer_online(
					  //>> CONFIG_ST_MOBILE_
#if (defined(__ANDROID__))
					  //>> CONFIG_API_END__
					  //>> CONFIG_ST_MOBILE_USE_ANDROID
					  JNIEnv* env,
					  jobject context,
					  //>> CONFIG_API_END__
					  //>> CONFIG_ST_MOBILE_
#endif
					  //>> CONFIG_API_END__
					  const char* license_buf,
					  int license_size,
					  char* activation_code,
					  int* activation_code_len
					  );


#endif // INCLUDE_STMOBILE_ST_MOBILE_LICENSE_H_
