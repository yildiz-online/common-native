cmake_minimum_required (VERSION 3.0.0)
project (yildiz_common_native)

set(CMAKE_CXX_FLAGS "-O2 -fPIC -std=gnu++11")

find_package(Java REQUIRED)
find_package(JNI REQUIRED)

include_directories(${JNI_INCLUDE_DIRS})
include_directories(${JNI_INCLUDE_DIRS}/linux)

file(
        GLOB_RECURSE
        source_files
        ${CMAKE_SOURCE_DIR}/../../c++/*.cpp
        ${CMAKE_SOURCE_DIR}/../../c++/*.c
        ${CMAKE_SOURCE_DIR}/../../includes/*.hpp
        ${CMAKE_SOURCE_DIR}/../../includes/*.h
)

add_library(yildizcommon SHARED ${source_files})
