file(GLOB_RECURSE discord_source_files src/*.cpp)

add_library(discord ${discord_source_files})

add_library(discord_sdk SHARED IMPORTED)
set_target_properties( discord_sdk PROPERTIES IMPORTED_LOCATION ${CMAKE_BINARY_DIR}/discord_game_sdk.so )

target_include_directories(discord PUBLIC include)

if (WIN32)
    target_link_libraries(discord PUBLIC discord_game_sdk.dll)
elseif (UNIX)
    file(COPY ${CMAKE_SOURCE_DIR}/libs/discord/discord_game_sdk.so DESTINATION ${CMAKE_BINARY_DIR})
    target_link_libraries(discord PUBLIC discord_sdk)
endif ()
