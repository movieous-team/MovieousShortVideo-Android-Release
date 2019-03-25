# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)


add_library(stmobile_jni
            SHARED
                src/main/jni/stmobile_common_jni.cpp
                src/main/jni/stmobile_beautify_jni.cpp
                src/main/jni/stmobile_sticker_jni.cpp
                src/main/jni/stmobile_humanaction_jni.cpp
                src/main/jni/utils.cpp
                src/main/jni/stmobile_authentification_jni.cpp
                src/main/jni/stmobile_face_attribute_jni.cpp
                src/main/jni/stmobile_filter_jni.cpp
                src/main/jni/stmobile_streamfilter_jni.cpp
                src/main/jni/stmobile_object_jni.cpp
                src/main/jni/stmobile_sound_play_jni.cpp
                src/main/jni/jvmutil.cpp
            )

include_directories(./src/main/jni/prebuilt/include)

find_library(log-lib log)
find_library(dl-lib dl)
find_library(android-lib android)
find_library(stmobile-lib st_mobile PATHS ./src/main/jni/prebuilt/lib/${ANDROID_ABI}/ NO_CMAKE_FIND_ROOT_PATH)

target_link_libraries( stmobile_jni ${log-lib} ${dl-lib} ${stmobile-lib} ${android-lib})