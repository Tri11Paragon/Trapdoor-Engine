cmake_minimum_required(VERSION 3.22)
project(engine VERSION "0.0.1")

set(CMAKE_CXX_STANDARD 20)
if (UNIX AND (CMAKE_BUILD_TYPE MATCHES Debug))
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fsanitize=address -g")
add_compile_options(-fsanitize=address)
add_link_options(-fsanitize=address)
endif()

#set(CMAKE_FIND_LIBRARY_SUFFIXES ".a")
#set(BUILD_SHARED_LIBS OFF)
#set(CMAKE_EXE_LINKER_FLAGS "-static")
option(EnableDebugMenu "Enable the debug menus (engine editors)" OFF)

if(EnableDebugMenu MATCHES ON)
    message("debug mode")
    set(DEBUG_ENABLED true)
    set(DEBUG_ENABLED_BOOL true)
else()
    message("release mode")
    set(DEBUG_ENABLED false)
    set(DEBUG_ENABLED_BOOL true)
endif(CMAKE_BUILD_TYPE MATCHES Debug)
set(ENGINE_VERSION_MAJOR " ${PROJECT_VERSION_MAJOR}")
set(ENGINE_VERSION_MINOR " ${PROJECT_VERSION_MINOR}")
set(ENGINE_VERSION_PATCH " ${PROJECT_VERSION_PATCH}")
set(BUILD_DIR "${PROJECT_SOURCE_DIR}")
if(WIN32)
    set(RESOURCE_PATH "%appdata%/Trapdoor/") # likely wrong and should be set from c++ but i'd rather it be set this way :(
    set(PATH_SEPARATOR "\\")
elseif(UNIX)
    set(RESOURCE_PATH "~/.local/share/Trapdoor/")
    set(PATH_SEPARATOR "/")
endif()
configure_file(${PROJECT_SOURCE_DIR}/src/config.h.in config.h @ONLY)


#Boost setup
set(Boost_USE_STATIC_LIBS       ON)  # only find static libs
set(Boost_USE_DEBUG_LIBS        OFF)  # ignore debug libs and
set(Boost_USE_RELEASE_LIBS      ON)  # only find release libs
set(Boost_USE_MULTITHREADED     ON)
set(Boost_USE_STATIC_RUNTIME    OFF)
set(Boost_NO_WARN_NEW_VERSIONS  ON)

# Boost Dirs
if (UNIX)
    set( BOOST_ROOT "libs/boost_lib/" CACHE PATH "Boost library path" )
    set( BOOST_INCLUDEDIR "libs/boost_lib/include" CACHE PATH "Boost library path" )
    set( BOOST_LIBRARYDIR "libs/boost_lib/lib" CACHE PATH "Boost library path" )
    set( Boost_NO_SYSTEM_PATHS on CACHE BOOL "Do not search system for Boost" )

    find_package(Boost REQUIRED COMPONENTS filesystem log)
elseif(WIN32)
    #set( BOOST_ROOT "libs/boost_lib_windows/" CACHE PATH "Boost library path" )
    #set( BOOST_INCLUDEDIR "libs/boost_lib_windows/include" CACHE PATH "Boost library path" )
    #set( BOOST_LIBRARYDIR "libs/boost_lib_windows/lib" CACHE PATH "Boost library path" )
    #set(Boost_ARCHITECTURE "-x64")
    set(Boost_INCLUDE_DIRS libs/boost_lib_windows/include/boost-1_79)
    if(CMAKE_BUILD_TYPE MATCHES Debug)
        set(Boost_LIBRARIES 
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_atomic-vc143-mt-gd-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_chrono-vc143-mt-gd-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_filesystem-vc143-mt-gd-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_log_setup-vc143-mt-gd-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_log-vc143-mt-gd-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_regex-vc143-mt-gd-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_thread-vc143-mt-gd-x64-1_79.lib"
        )
        set(bfilesystem "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_filesystem-vc143-mt-gd-x64-1_79.lib")
        set(blog "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_log-vc143-mt-gd-x64-1_79.lib")
    else()
        set(Boost_LIBRARIES 
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_atomic-vc143-mt-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_chrono-vc143-mt-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_filesystem-vc143-mt-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_log_setup-vc143-mt-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_log-vc143-mt-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_regex-vc143-mt-x64-1_79.lib"
            "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_thread-vc143-mt-x64-1_79.lib"
        )
        set(bfilesystem "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_filesystem-vc143-mt-x64-1_79.lib")
        set(blog "${PROJECT_SOURCE_DIR}/libs/boost_lib_windows/lib/libboost_log-vc143-mt-x64-1_79.lib")
    endif()
endif()

include_directories(${Boost_INCLUDE_DIRS})

find_package(Threads REQUIRED)

#GL Setup
find_package(OpenGL REQUIRED)
#find_package(glfw3 3.3 REQUIRED)
add_subdirectory(libs/glfw)
include_directories(libs/glfw/include)

add_subdirectory(libs/glad/cmake)
add_subdirectory(libs/glm)

glad_add_library(glad_gl_core STATIC API gl:core=4.5)

set(TD_BUILD_SETTING ${CMAKE_BUILD_TYPE})
message("Cached BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")
# Utils Setup
if (WIN32)
    set(ASSIMP_BUILD_X3D_IMPORTER OFF)
    set(ASSIMP_BUILD_SMD_IMPORTER OFF)
    set(ASSIMP_BUILD_GLTF_IMPORTER OFF)
    set(ASSIMP_BUILD_GLTF_EXPORTER OFF)
    set(ASSIMP_IMPORTER_GLTF_USE_OPEN3DGC OFF)
elseif(UNIX)
    set(CMAKE_BUILD_TYPE "Debug")
endif()
add_subdirectory(libs/assimp)
message("Assimp BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")
set(CMAKE_BUILD_TYPE ${TD_BUILD_SETTING})
add_subdirectory(libs/meshoptimizer)
message("Meshoptimizer BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")
# DTV setup
add_subdirectory(libs/direct-to-video)
include_directories(libs/direct-to-video/include)

# Bullet Setup
#set( BULLET_ROOT "libs/bullet3/build" CACHE PATH "Bullet library path" )

#find_package(Bullet REQUIRED)
#include_directories(${BULLET_INCLUDE_DIRS})
set(CMAKE_BUILD_TYPE "Release")
add_subdirectory(${PROJECT_SOURCE_DIR}/libs/bullet3)
include_directories(${PROJECT_SOURCE_DIR}/libs/bullet3/src)
message("Bullet BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")
set(CMAKE_BUILD_TYPE ${TD_BUILD_SETTING})
message("Backto BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")

# NFD Setup
#include_directories(${PROJECT_SOURCE_DIR}/libs/nativefiledialog/src/include)
add_subdirectory(libs/nativefiledialog)

# Zips

# Engine FS sources setup
set(source_dir "${PROJECT_SOURCE_DIR}/src/")

file(GLOB_RECURSE source_files "${source_dir}/*.cpp")
file(GLOB_RECURSE source_c_files "${source_dir}/*.c")

include_directories(${CMAKE_CURRENT_BINARY_DIR})
include_directories(${PROJECT_SOURCE_DIR}/libs/stb)
include_directories(${PROJECT_SOURCE_DIR}/include)

add_executable(${PROJECT_NAME} ${source_files} ${source_c_files})

#Linking

target_link_libraries(${PROJECT_NAME} OpenGL::GL)
target_link_libraries(${PROJECT_NAME} glfw)
target_link_libraries(${PROJECT_NAME} glad_gl_core)
target_link_libraries(${PROJECT_NAME} glm)
target_link_libraries(${PROJECT_NAME} assimp)
target_link_libraries(${PROJECT_NAME} ${BULLET_LIBRARIES})
target_link_libraries(${PROJECT_NAME} meshoptimizer)
target_link_libraries(${PROJECT_NAME} ${CMAKE_DL_LIBS})
target_link_libraries(${PROJECT_NAME} ${Boost_LIBRARIES})
target_link_libraries(${PROJECT_NAME} Threads::Threads)
if (UNIX)
    target_link_libraries(${PROJECT_NAME} Boost::filesystem Boost::log)
elseif(WIN32)
    target_link_libraries(${PROJECT_NAME} ${bfilesystem} ${blog})
endif()
target_link_libraries(${PROJECT_NAME} direct-to-video)

#target_link_libraries(${PROJECT_NAME} ${PROJECT_SOURCE_DIR}/libs/nativefiledialog/build/lib/Debug/x64/libnfd_d.a)
target_link_libraries(${PROJECT_NAME} nativefiledialog)