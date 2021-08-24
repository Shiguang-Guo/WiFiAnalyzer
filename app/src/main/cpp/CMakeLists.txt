
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)


set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../distribution)
# Declares and names the project.

project("wifianalyzer")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.


add_library(lib_libnl-3 SHARED IMPORTED)
set_target_properties(lib_libnl-3 PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/lib/${ANDROID_ABI}/libnl-3.so
        )

add_library(lib_libnl-genl-3 SHARED IMPORTED)
set_target_properties(lib_libnl-genl-3 PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/lib/${ANDROID_ABI}/libnl-genl-3.so
        )

add_library(lib_libnl-route-3 SHARED IMPORTED)
set_target_properties(lib_libnl-route-3 PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/lib/${ANDROID_ABI}/libnl-route-3.so
        )

# build application's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

add_library( # Sets the name of the library.
        wifianalyzer

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        wifianalyzer.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_include_directories(wifianalyzer PRIVATE
        ${distribution_DIR}/include)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        wifianalyzer
        lib_libnl-3
        lib_libnl-genl-3
        lib_libnl-route-3
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        )
