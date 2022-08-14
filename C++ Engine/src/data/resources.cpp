//
// Created by brett on 13/08/22.
//

#include "data/resources.h"
#include <boost/filesystem.hpp>
#include "data/user.h"
using namespace boost::filesystem;

namespace TD {

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

    void Project::newProject() {

    }

    void Project::loadProject(const std::string& folderPath) {

    }

    void Project::init() {

    }

    void Project::saveDisplays() {

    }

    void Project::loadDisplays() {

    }

    std::string Project::getResourcePath() {
        return std::string();
    }

    std::string Project::getDisplaysPath() {
        return std::string();
    }
}
