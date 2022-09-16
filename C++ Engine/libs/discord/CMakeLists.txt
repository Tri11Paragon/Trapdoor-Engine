file(GLOB_RECURSE discord_source_files src/*.cpp)

add_library(discord ${discord_source_files})

add_library(discord_sdk SHARED IMPORTED)
set_target_properties( discord_sdk PROPERTIES IMPORTED_LOCATION ${CMAKE_BINARY_DIR}/discord_game_sdk.so )

target_include_directories(discord PUBLIC include)

if (WIN32)
    #add_library(discord_sdk_win SHARED IMPORTED)
    #add_library(discord_sdk_win_lib SHARED IMPORTED)
    #add_library(discord_sdk_win_dylib SHARED IMPORTED)
    #set_target_properties( discord_sdk_win PROPERTIES IMPORTED_LOCATION ${CMAKE_BINARY_DIR}/discord_game_sdk.dll )
    #set_target_properties( discord_sdk_win_lib PROPERTIES IMPORTED_LOCATION ${CMAKE_BINARY_DIR}/discord_game_sdk.dll.lib )
    #set_target_properties( discord_sdk_win_dylib PROPERTIES IMPORTED_LOCATION ${CMAKE_BINARY_DIR}/discord_game_sdk.dylib )

    file(COPY ${CMAKE_SOURCE_DIR}/libs/discord/discord_game_sdk.dll DESTINATION ${CMAKE_BINARY_DIR})
    file(COPY ${CMAKE_SOURCE_DIR}/libs/discord/discord_game_sdk.dll.lib DESTINATION ${CMAKE_BINARY_DIR})
    file(COPY ${CMAKE_SOURCE_DIR}/libs/discord/discord_game_sdk.dylib DESTINATION ${CMAKE_BINARY_DIR})
    #target_link_libraries(discord PUBLIC discord_sdk_win discord_sdk_win_lib discord_sdk_win_dylib)
    target_link_libraries(discord PUBLIC discord_game_sdk.dll.lib)
elseif (UNIX)
    file(COPY ${CMAKE_SOURCE_DIR}/libs/discord/discord_game_sdk.so DESTINATION ${CMAKE_BINARY_DIR})
    target_link_libraries(discord PUBLIC discord_sdk)
endif ()
