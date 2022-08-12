cmake_minimum_required(VERSION 3.22)
project(engine VERSION "0.0.1")

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fsanitize=address -g")
add_compile_options(-fsanitize=address)
add_link_options(-fsanitize=address)

#set(CMAKE_FIND_LIBRARY_SUFFIXES ".a")
#set(BUILD_SHARED_LIBS OFF)
#set(CMAKE_EXE_LINKER_FLAGS "-static")

if(CMAKE_BUILD_TYPE MATCHES Debug)
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
    set(RESOURCE_PATH "%userprofile%/Documents/Trapdoor/") # likely wrong and should be set from c++ but i'd rather it be set this way :(
elseif(UNIX)
    set(RESOURCE_PATH "~/.local/share/Trapdoor/")
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
set( BOOST_ROOT "include/boost_lib/" CACHE PATH "Boost library path" )
set( BOOST_INCLUDEDIR "include/boost_lib/include" CACHE PATH "Boost library path" )
set( BOOST_LIBRARYDIR "include/boost_lib/lib" CACHE PATH "Boost library path" )
set( Boost_NO_SYSTEM_PATHS on CACHE BOOL "Do not search system for Boost" )

find_package(Boost REQUIRED COMPONENTS filesystem log)

include_directories(${Boost_INCLUDE_DIRS})

find_package(Threads REQUIRED)

#GL Setup
find_package(OpenGL REQUIRED)
#find_package(glfw3 3.3 REQUIRED)
add_subdirectory(include/glfw)
include_directories(include/glfw/include)

add_subdirectory(include/glad/cmake)
add_subdirectory(include/glm)

glad_add_library(glad_gl_core STATIC API gl:core=4.5)

set(TD_BUILD_SETTING ${CMAKE_BUILD_TYPE})
message("Cached BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")
# Utils Setup
set(CMAKE_BUILD_TYPE "Debug")
add_subdirectory(include/assimp)
message("Assimp BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")
set(CMAKE_BUILD_TYPE ${TD_BUILD_SETTING})
add_subdirectory(include/meshoptimizer)
message("Meshoptimizer BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")
# DTV setup
add_subdirectory(include/direct-to-video)
include_directories(include/direct-to-video/include)

# Bullet Setup
#set( BULLET_ROOT "include/bullet3/build" CACHE PATH "Bullet library path" )

#find_package(Bullet REQUIRED)
#include_directories(${BULLET_INCLUDE_DIRS})
set(CMAKE_BUILD_TYPE "Release")
add_subdirectory(${PROJECT_SOURCE_DIR}/include/bullet3)
include_directories(${PROJECT_SOURCE_DIR}/include/bullet3/src)
message("Bullet BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")
set(CMAKE_BUILD_TYPE ${TD_BUILD_SETTING})
message("Backto BUILD: ${TD_BUILD_SETTING} // ${CMAKE_BUILD_TYPE}")

# NFD Setup
#include_directories(${PROJECT_SOURCE_DIR}/include/nativefiledialog/src/include)
add_subdirectory(include/nativefiledialog)

# Zips

# Engine FS sources setup
set(source_dir "${PROJECT_SOURCE_DIR}/src/")

file(GLOB_RECURSE source_files "${source_dir}/*.cpp")
file(GLOB_RECURSE source_c_files "${source_dir}/*.c")

include_directories(${CMAKE_CURRENT_BINARY_DIR})
include_directories(${PROJECT_SOURCE_DIR}/include/stb)

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
target_link_libraries(${PROJECT_NAME} Boost::filesystem Boost::log)
target_link_libraries(${PROJECT_NAME} direct-to-video)

#target_link_libraries(${PROJECT_NAME} ${PROJECT_SOURCE_DIR}/include/nativefiledialog/build/lib/Debug/x64/libnfd_d.a)
target_link_libraries(${PROJECT_NAME} nativefiledialog)