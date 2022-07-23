cmake_minimum_required(VERSION 3.22)
project(engine VERSION "0.0.1")

set(CMAKE_CXX_STANDARD 20)

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
find_package(glfw3 3.3 REQUIRED)

add_subdirectory(include/glad/cmake)
add_subdirectory(include/glm)

glad_add_library(glad_gl_core SHARED API gl:core=4.5)

# Utils Setup
add_subdirectory(include/assimp)
add_subdirectory(include/meshoptimizer)

# Bullet Setup
#set( BULLET_ROOT "include/bullet3/build" CACHE PATH "Bullet library path" )

#find_package(Bullet REQUIRED)
#include_directories(${BULLET_INCLUDE_DIRS})
add_subdirectory(${PROJECT_SOURCE_DIR}/include/bullet3)
include_directories(${PROJECT_SOURCE_DIR}/include/bullet3/src)

# NFD Setup
#include_directories(${PROJECT_SOURCE_DIR}/include/nativefiledialog/src/include)
add_subdirectory(include/nativefiledialog)

# Engine FS sources setup
set(source_dir "${PROJECT_SOURCE_DIR}/src/")

file(GLOB_RECURSE source_files "${source_dir}/*.cpp")

include_directories(${CMAKE_CURRENT_BINARY_DIR})
include_directories(${PROJECT_SOURCE_DIR}/include/stb)

add_executable(${PROJECT_NAME} ${source_files})

#Linking

target_link_libraries(${PROJECT_NAME} glfw)
target_link_libraries(${PROJECT_NAME} OpenGL::GL)
target_link_libraries(${PROJECT_NAME} glad_gl_core)
target_link_libraries(${PROJECT_NAME} glm)
target_link_libraries(${PROJECT_NAME} assimp)
target_link_libraries(${PROJECT_NAME} ${BULLET_LIBRARIES})
target_link_libraries(${PROJECT_NAME} meshoptimizer)
target_link_libraries(${PROJECT_NAME} ${CMAKE_DL_LIBS})
target_link_libraries(${PROJECT_NAME} ${Boost_LIBRARIES})
target_link_libraries(${PROJECT_NAME} Threads::Threads)
target_link_libraries(${PROJECT_NAME} Boost::filesystem Boost::log)

#target_link_libraries(${PROJECT_NAME} ${PROJECT_SOURCE_DIR}/include/nativefiledialog/build/lib/Debug/x64/libnfd_d.a)
target_link_libraries(${PROJECT_NAME} nativefiledialog)