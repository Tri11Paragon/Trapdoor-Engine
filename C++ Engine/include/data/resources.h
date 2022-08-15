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
        // called when the engine loads, used to create the blank screen.
        static void newProject();
    public:
        // init the project system, call before most things, including window creation.
        // if it finds a project it will queue resources to be loaded.
        static bool init();
        static void close();

        static bool saveDisplays();
        static bool loadDisplays();

        static void createNewProject();
        static bool saveProject();
        // loads a project from disk
        static void loadProject(const std::string& folderPath);

        static std::string getResourcePath();
        static std::string getDisplaysPath();

        static bool setProjectLocationDialog(std::string& out);

        static bool showNewProjectDialog();
        static bool showNewScreenDialog();
    };

}

#endif //ENGINE_RESOURCES_H
