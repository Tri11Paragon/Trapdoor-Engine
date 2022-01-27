cmake_minimum_required(VERSION 3.19)
project(Trapdoor)

set(CMAKE_CXX_STANDARD 20)

set(source_dir "${PROJECT_SOURCE_DIR}/src/")

file(GLOB_RECURSE source_files "${source_dir}/*.cpp")
file(GLOB_RECURSE header_files "${source_dir}/*.h")

add_executable(Trapdoor ${source_files} ${header_files})