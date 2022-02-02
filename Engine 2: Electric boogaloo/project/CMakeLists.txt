cmake_minimum_required(VERSION 3.19)
project(Trapdoor VERSION "0.1.0")

set(CMAKE_CXX_STANDARD 20)

configure_file("include/config/config.h.in" "include/config/config.h")

set(source_dir "${PROJECT_SOURCE_DIR}/src/")
set(include_dir "${PROJECT_SOURCE_DIR}/include/")
set(glew_dir "${PROJECT_SOURCE_DIR}/depends/glew/")
set(imgui_dir "${PROJECT_SOURCE_DIR}/depends/imgui/")

file(GLOB_RECURSE source_files "${source_dir}/*.cpp")
file(GLOB_RECURSE header_files "${include_dir}/*.h")
file(GLOB_RECURSE glew_files "${glew_dir}/src/*.cpp")
file(GLOB imgui_files "${imgui_dir}/*.cpp")

add_executable(${PROJECT_NAME} ${source_files} ${header_files})