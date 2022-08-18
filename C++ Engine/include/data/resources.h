//
// Created by brett on 13/08/22.
//

#ifndef ENGINE_RESOURCES_H
#define ENGINE_RESOURCES_H

#include <std.h>
#include <logging.h>
#include <hashmaps.h>
#include <boost/algorithm/string.hpp>
#include <data/NBT.h>

#include <utility>

namespace TD {
    class PropertiesFormat {
    private:
        std::string path;
        flat_hash_map<int, std::string> comments;
        flat_hash_map<std::string, std::string> properties;
    public:
        PropertiesFormat() = default;
        explicit PropertiesFormat(std::string&& path): path(std::move(path)) {}
        explicit PropertiesFormat(const std::string& path): path(path) {}
        inline void open(const std::string& p){ this->path = p;}
        inline bool hasPath(){boost::trim(path); return !path.empty();}
        void load();
        void save();
        inline bool hasProperty(const std::string& pFind){return properties.find(pFind) != properties.end();}
        inline std::string getProperty(const std::string& p){return properties.at(p);}
        inline int getPropertyInt(const std::string& p){return std::stoi(properties.at(p));}
        inline long getPropertyLong(const std::string& p){return std::stol(properties.at(p));}
        inline float getPropertyFloat(const std::string& p){return std::stof(properties.at(p));}
        inline double getPropertyDouble(const std::string& p){return std::stod(properties.at(p));}
        inline void setProperty(const std::string& property, const std::string& value){properties.insert(std::pair(property, value));}
        inline void setOverrideProperty(const std::string& property, const std::string& value) {
            if (hasProperty(property)){
                properties.erase(property);
                setProperty(property, value);
            } else
                setProperty(property, value);
        }
    };
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
