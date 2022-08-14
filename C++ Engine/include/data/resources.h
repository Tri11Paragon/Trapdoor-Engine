//
// Created by brett on 13/08/22.
//

#ifndef ENGINE_RESOURCES_H
#define ENGINE_RESOURCES_H

#include <std.h>
#include <logging.h>
#include <hashmaps.h>

namespace TD {
    /**
     * Class which handles file nonsense like creating / finding
     */
    class Resources {
    public:
        static void init();
        static std::string getTrapdoorHome();
        static std::string getUserHome();
    };

    /**
     * handles all of the global project settings
     */
    class Project {
    private:
        static void newProject();
        static void loadProject(const std::string& folderPath);
    public:
        static void init();
        static void saveDisplays();
        static void loadDisplays();
        static std::string getResourcePath();
        static std::string getDisplaysPath();
    };

}

#endif //ENGINE_RESOURCES_H
