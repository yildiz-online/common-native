cmake_minimum_required (VERSION 2.8.6)
project (yildiz_common_native)

set(CMAKE_CXX_FLAGS "-O2 -std=gnu++11")

include_directories("java/include")
include_directories("java/include/win32")

set(CMAKE_SHARED_LINKER_FLAGS "-Xlinker --add-stdcall-alias")

file(
        GLOB_RECURSE
        source_files
        ${CMAKE_SOURCE_DIR}/../../c++/*.cpp
        ${CMAKE_SOURCE_DIR}/../../c++/*.c
        ${CMAKE_SOURCE_DIR}/../../includes/*.hpp
        ${CMAKE_SOURCE_DIR}/../../includes/*.h
)

add_library(yildizcommon SHARED ${source_files})


