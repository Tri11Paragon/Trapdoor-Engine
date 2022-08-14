//
// Created by brett on 13/08/22.
//

#include "data/resources.h"
#include <boost/filesystem.hpp>
#include "data/user.h"
#include <sqlite/sqlite3.h>
#include "renderer/ui/debug.h"
#include <nfd.h>
using namespace boost::filesystem;

namespace TD {

    /** ============{ Resources }============ **/
    std::string trapdoor_home;
    std::string user_home;

    void Resources::init() {
        user_home = TD::getUserHome();
#if _WIN32
        trapdoor_home = user_home + "/Documents/Trapdoor";
#endif
#if linux
        trapdoor_home = user_home + "/.local/Trapdoor";
#endif
        path p(trapdoor_home);
        if (!exists(p)) {
            ilog << "Creating Trapdoor home directory!";
            try {
                create_directories(p);
            } catch (std::exception &e) {
                flog << "Unable to create trapdoor home directory!" << trapdoor_home;
                exit(5);
            }
        }
    }

    std::string Resources::getTrapdoorHome() {return trapdoor_home;}
    std::string Resources::getUserHome() {return user_home;}

    /** ============{ Project }============ **/
    sqlite3 *db;
    char *zErrMsg = nullptr;
    int rc;

    std::string projectHome {};

    static int qLastProject(void *NotUsed, int rowCount, char **rows, char **azColName){
        if (rowCount > 0) {
            projectHome = rows[0];
            /*int i;
            for (i = 0; i < rowCount; i++) {
                printf("%s = %s\n", azColName[i], rows[i] ? rows[i] : "NULL");
            }
            printf("\n");*/
            return 0;
        } else
            return SQLITE_NOTFOUND;
    }

    bool Project::init() {
        if (exists(trapdoor_home + "/trapdoor.profile")) {
            rc = sqlite3_open((trapdoor_home + "/trapdoor.profile").c_str(), &db);
            if (rc) {
                flog << "Can't open database! " << db;
                sqlite3_close(db);
                exit(124);
            }
            rc = sqlite3_exec(db, "SELECT * FROM last_project_settings;", qLastProject, nullptr, &zErrMsg);
            if (rc != SQLITE_OK) {
                elog << "Error executing querry: " << zErrMsg;
                sqlite3_free(zErrMsg);
            }
            if (!projectHome.empty()) {
                loadProject(projectHome);
                return true;
            }
        }
        newProject();
        return false;
    }

    void Project::newProject() {
        TD::Editor::setToOpen();
    }

    void Project::loadProject(const std::string& folderPath) {

    }

    bool Project::saveDisplays() {
        if (projectHome.empty())
            if (!setProjectLocationDialog(projectHome))
                return false;

    }

    bool Project::loadDisplays() {
        if (projectHome.empty())
            return false;

    }

    std::string Project::getResourcePath() {
        if (projectHome.empty())
            throw std::runtime_error("Project has no home, and therefore cannot have a resources path!");
        return projectHome + "/resources";
    }

    std::string Project::getDisplaysPath() {
        if (projectHome.empty())
            throw std::runtime_error("Project has no home, and therefore cannot have a displays path!");
        return projectHome + "/displays";
    }

    void Project::close() {
        sqlite3_close(db);
    }

    bool Project::setProjectLocationDialog(std::string& out) {
        nfdchar_t *outPath = nullptr;
        nfdresult_t result = NFD_PickFolder( nullptr, &outPath );
        if ( result == NFD_OKAY ) {
            out = outPath;
            free(outPath);
            return true;
        } else if ( result == NFD_CANCEL )
            elog << "User pressed cancel.";
        else
            elog << NFD_GetError();
        return false;
    }

    bool Project::showNewProjectDialog() {
        return false;
    }

    bool Project::showNewScreenDialog() {
        return false;
    }
}
