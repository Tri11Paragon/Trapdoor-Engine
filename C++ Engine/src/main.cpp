#include <iostream>

// https://github.com/ocornut/imgui/tree/master/docs

#include "logging.h"
#include "window.h"
#include <nfd.h>
#include <stdio.h>
#include "input.h"
#include "renderer/ui/debug.h"
#include "renderer/shader.h"
#include "renderer/camera.h"
#include "util.h"
#include "profiler.h"
#include "world/World.h"
#include "game/TestDisplay.h"
#include "world/GameRegistry.h"
#include <config.h>
#include <encoder.h>
#include <data/resources.h>

#include "data/NBT.h"

#include "sqlite/sqlite3.h"
static int callback(void *NotUsed, int argc, char **argv, char **azColName){
    int i;
    for(i=0; i<argc; i++){
        printf("%s = %s\n", azColName[i], argv[i] ? argv[i] : "NULL");
    }
    printf("\n");
    return 0;
}

int main(int, char**){
    TD::profiler loadTimer("Load Time");
    loadTimer.start("Load Time");
    init_logging("output");
    TD::Resources::init();
    tlog << DEBUG_ENABLED_BOOL;

//    sqlite3 *db;
//    char *zErrMsg = nullptr;
//    int rc;
//    rc = sqlite3_open("trapdoor.profile", &db);
//    if (rc){
//        tlog << "Can't open database! " << db;
//        sqlite3_close(db);
//        return 1;
//    }
//    rc = sqlite3_exec(db, "CREATE TABLE ducks(id int, name varchar(255)); INSERT INTO ducks VALUES (5, 'Duckers'); SELECT * FROM ducks;", callback, 0, &zErrMsg);
//    if (rc != SQLITE_OK){
//        tlog << "Error executing querry: " << zErrMsg;
//        sqlite3_free(zErrMsg);
//    }
//    sqlite3_close(db);

    TD::GameRegistry::registerRegistrationCallback([]() -> void* {
        // Register Models
        TD::GameRegistry::registerModel("taylor_plane", "../assets/models/32x32plane_sided.dae");
        TD::GameRegistry::registerModel("kent", "../assets/models/kent.dae");
        TD::GameRegistry::registerModel("plane", "../assets/models/32x32plane.dae");
        TD::GameRegistry::registerModel("sponza", "../assets/models/sponzame/sponza.dae");
        // Register Fonts
        TD::GameRegistry::registerFont("quicksand", "../assets/fonts/quicksand/Quicksand-Regular.ttf", 16.0f);
        TD::GameRegistry::registerFont("roboto", "../assets/fonts/roboto/Roboto-Regular.ttf", 16.0f);
        return nullptr;
    });

    TD::DisplayManager::init("Trapdoor " + std::to_string(ENGINE_VERSION_MAJOR) + "." + std::to_string(ENGINE_VERSION_MINOR) + "."
            + std::to_string(ENGINE_VERSION_PATCH) + " // C++ Test");

    // Standard Defered about 120fps @ 1024 lights (8.5ms)

    // will automatically be cleaned up when the display manager exits
    new TD::TestDisplay("TestDisplay");
    TD::DisplayManager::changeDisplay("TestDisplay");
    loadTimer.end("Load Time");
    loadTimer.print();
    TD::DisplayManager::update();
    TD::DisplayManager::close();
    return 0;
}
