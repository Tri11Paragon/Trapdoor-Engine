cmake_minimum_required(VERSION 3.19)
project(Trapdoor VERSION "0.1.0")

set(CMAKE_CXX_STANDARD 20)

configure_file("src/config/config.h.in" "src/config/config.h")

find_package(OpenGL REQUIRED)
find_package(glfw3 3.3 REQUIRED)

add_subdirectory(depends/glad)
add_subdirectory(depends/glm)

set(source_dir "${PROJECT_SOURCE_DIR}/src/")

file(GLOB_RECURSE source_files "${source_dir}/*.cpp")

add_executable(${PROJECT_NAME} ${source_files})

include_directories(${CMAKE_CURRENT_BINARY_DIR})

target_link_libraries(${PROJECT_NAME} glfw)
target_link_libraries(${PROJECT_NAME} OpenGL::GL)
target_link_libraries(${PROJECT_NAME} glad)
target_link_libraries(${PROJECT_NAME} glm)
target_link_libraries(${PROJECT_NAME} ${CMAKE_DL_LIBS})