# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

#프로젝트, opencv라이브러리 경로를 각각 변수에 저장
set(pathOPENCV C:/Users/yoon/opencv-3.2.0-android-sdk/OpenCV-android-sdk)
set(pathPROJECT C:/Users/yoon/AndroidStudioProjects/test/OpencvFilterTest)

set(pathLIBOPENCV_JAVA ${pathPROJECT}/app/src/main/JniLibs/${ANDROID_ABI}/libopencv_java3.so)
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")


#헤더파일 경로
include_directories(${pathOPENCV}/sdk/native/jni/include)

#add_library( lib_opencv SHARED IMPORTED )
#set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathProject}/app/src/main/JniLibs/${ANDROID_ABI}/libopencv_java3.so)


#와일드 문자를 이용하여 여러개의 파일을 불러올 수 도 있음
# *.cpp 또는 *.h
file(GLOB Library_SOURCES  src/main/cpp/module.cpp)
file(GLOB Library_HEADER   src/main/cpp/module.h)

#공유 라이브러리 생성
add_library( lib_opencv SHARED IMPORTED )
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathLIBOPENCV_JAVA})



# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#공유 라이브러리 생성
add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )





# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.
#미리 빌드된 라이브러리를 찾는다
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log
              android-lib android
              )


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
#타켓 라이브러리를 생성하기 위해 필요한 라이브러리를 명시
target_link_libraries( # Specifies the target library.
                       native-lib   #타켓 라이브러리


                        #필요한 라이브러리
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                        ${android-lib}

                        lib_opencv
                       )


