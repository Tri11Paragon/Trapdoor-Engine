//
// Created by brett on 13/08/22.
//

#include "data/resources.h"
#include <boost/filesystem.hpp>
#include "data/user.h"
#include <sqlite/sqlite3.h>
#include "renderer/ui/debug.h"
#include <nfd.h>
#include <boost/algorithm/string.hpp>
#include "window.h"
#include "world/World.h"
#include <discord.h>
#include <config.h>
#include <iostream>
#include <data/NBT.h>
#include <filesystem>

using namespace boost::filesystem;

namespace TD {

    extern int _display_w, _display_h;
    extern std::unordered_map<std::string, TD::Display *> displays;
    extern std::string activeDisplay;
    extern discord::Core *discore;

    /** ============{ Resources }============ **/
    std::string trapdoor_home;
    std::string user_home;

    void Resources::init() {
        user_home = TD::getUserHome();
#if _WIN32
        trapdoor_home = user_home + "/Documents/Trapdoor";
#endif
#if linux
        trapdoor_home = user_home + "/.local/share/Trapdoor";
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

    std::string Resources::getTrapdoorHome() { return trapdoor_home; }

    std::string Resources::getUserHome() { return user_home; }

    /** ============{ Project }============ **/
    PropertiesFormat properties(trapdoor_home + "/trapdoor.profile");
    sqlite3 *db;
    char *zErrMsg = nullptr;
    int rc;

    sqlite3 *projectDB;
    std::string projectDBLocation{};
    char *projectErrMsg = nullptr;
    int projectRC;

    std::string projectHome{};
    std::string projectName{};
    std::string lastFolderOpen{};

    static int qInsertProjectSettingsTableEmpty(void *NotUsed, int columnCount, char **columnData, char **azColName) {
        return 0;
    }

    static int qLastProject(void *NotUsed, int columnCount, char **columnData, char **azColName) {
        if (columnCount > 1) {
            tlog << "CC :" << columnCount;
            projectHome = columnData[0];
            lastFolderOpen = columnData[1];
            // if the saved home doesn't exist, we don't want to try and load it.
            if (!exists(projectHome))
                projectHome = "";
            for (int i = 0; i < columnCount; i++)
                tlog << azColName[i] << " = " << (columnData[i] ? columnData[i] : "NULL");
            return 0;
        } else {
            char *lErrMsg = nullptr;
            int lrc = sqlite3_exec(db, "INSERT INTO settings VALUES (NULL, NULL);", qInsertProjectSettingsTableEmpty,
                                   nullptr, &lErrMsg);
            if (lrc != SQLITE_OK) {
                elog << "Error executing querry: " << lErrMsg;
                sqlite3_free(lErrMsg);
            }
            return SQLITE_NOTFOUND;
        }
    }

    bool Project::init() {
        TD::registerAllocators();
        auto result = discord::Core::Create(1008769434187476995, DiscordCreateFlags_Default, &discore);
        if (exists(trapdoor_home + "/trapdoor.profile")) {
            properties.open(trapdoor_home + "/trapdoor.profile");
            properties.load();

            if (properties.hasProperty("lastOpenProject")) {
                ilog << "Loading project at " << properties.getProperty("lastOpenProject");
                projectHome = properties.getProperty("lastOpenProject");
            }
            if (properties.hasProperty("lastOpenFolder")) {
                ilog << "Setting last folder opened at " << properties.getProperty("lastOpenFolder");
                lastFolderOpen = properties.getProperty("lastOpenFolder");
            }

            TD::Editor::setToOpen();
            if (!projectHome.empty()) {
                loadProject(projectHome);
                return true;
            }
        }
        newProject();
        return false;
    }

    extern bool newProjectDialogOpen;

    void Project::newProject() {
        TD::Editor::setToOpen();
        newProjectDialogOpen = true;
    }

    void Project::loadProject(const std::string &folderPath) {
        if (exists(projectHome + "/project.profile")) {
            PropertiesFormat project(projectHome + "/project.profile");
            project.load();
            if (project.hasProperty("projectName"))
                projectName = project.getProperty("projectName");
        }
    }

    bool Project::saveDisplays() {
        for (auto d: displays) {
            auto *root = d.second->onSave();
            if (root) {
                TD::NBTWriterThreaded(*root, projectHome + "/displays/" + d.first + ".display");
            }
        }
        return true;
    }

    bool Project::loadDisplays() {
        if (projectHome.empty())
            return false;
        path p(projectHome + "/displays/");
        if (exists(p) && is_directory(p)) {
            // TODO: remove boost filesystem since std::filesystem contains the functions.
            for (const auto &dir: std::filesystem::directory_iterator(projectHome + "/displays/")) {
                if (is_regular_file(dir.path())) {
                    if (dir.path().extension() == ".display") {
                        TAG_COMPOUND root = NBTRecursiveReader::read(dir.path());

                        auto *display = GameRegistry::getDisplayByID(root.getName());

                        if (root.hasTag("name")) {
                            auto n = root.get<TAG_STRING>("name")->getPayload();
                            display->allocate(n)->onLoad(&root);
                            DisplayManager::changeDisplay(n);
                        } else
                            wlog << ".display format invalid!";
                    }
                }
            }
        }
        return true;
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

    bool Project::setProjectLocationDialog(std::string &out) {
        nfdchar_t *outPath = nullptr;
        nfdresult_t result = NFD_PickFolder(lastFolderOpen.empty() ? nullptr : lastFolderOpen.c_str(), &outPath);
        if (result == NFD_OKAY) {
            out = outPath;
            free(outPath);
            lastFolderOpen = out;
            return true;
        } else if (result == NFD_CANCEL)
            elog << "User pressed cancel.";
        else
            elog << NFD_GetError();
        return false;
    }

    extern int offsetY;

    bool Project::showNewProjectDialog() {
        bool stayOpen = true;
        ImGui::SetNextWindowPos(ImVec2((float) _display_w / 2, (float) _display_h / 2 - (float) offsetY / 2),
                                ImGuiCond_Appearing, ImVec2(0.5f, 0.5f));
        ImGui::Begin("New Project", &stayOpen, ImGuiWindowFlags_AlwaysAutoResize | ImGuiWindowFlags_NoCollapse |
                                               ImGuiWindowFlags_NoSavedSettings);

        static char stringBuffer[512]{};

        ImGui::Text("Project Name: ");
        ImGui::InputText("##Name", stringBuffer, 512, ImGuiInputTextFlags_EnterReturnsTrue);
        Strtrim(stringBuffer);
        if (stringBuffer[0]) {
            projectName = std::string(stringBuffer);
            boost::trim(projectName);
        }

        ImGui::Text("Project Directory: ");
        ImGui::SameLine();
        ImGui::Text("%s", projectHome.c_str());
        if (ImGui::Button("Select Directory")) {
            setProjectLocationDialog(projectHome);
        }
        ImGui::NewLine();
        if (ImGui::Button("Create New Project")) {
            if (projectHome.empty() || projectName.empty()) {
                wlog << "Project name and home must not be empty!";
            } else {
                createNewProject();
                stayOpen = false;
            }
        }

        ImGui::End();
        return stayOpen;
    }

    extern parallel_flat_hash_map<std::string, Display *> displayAllocators;

    bool Project::showNewScreenDialog() {
        bool stayOpen = true;
        ImGui::SetNextWindowPos(ImVec2((float) _display_w / 2, (float) _display_h / 2 - (float) offsetY / 2),
                                ImGuiCond_Appearing, ImVec2(0.5f, 0.5f));
        ImGui::Begin("New Project", &stayOpen, ImGuiWindowFlags_AlwaysAutoResize | ImGuiWindowFlags_NoCollapse |
                                               ImGuiWindowFlags_NoSavedSettings);

        static char stringBuffer[512]{};
        std::string displayName;

        ImGui::Text("Display Name: ");
        ImGui::InputText("##DisName", stringBuffer, 512, ImGuiInputTextFlags_EnterReturnsTrue);
        Strtrim(stringBuffer);
        if (stringBuffer[0]) {
            displayName = std::string(stringBuffer);
            boost::trim(displayName);
        }

        static std::string displayType;

        if (ImGui::BeginMenu("Display Type")) {
            for (const auto &d: displayAllocators) {
                if (ImGui::MenuItem(d.first.c_str(), nullptr, displayType == d.first)) {
                    displayType = d.first;
                }
            }
            ImGui::EndMenu();
        }

        if (ImGui::Button("Create Display")) {
            if (!displayName.empty() && !displayType.empty()) {
                try {
                    GameRegistry::getDisplayByID(displayType)->allocate(displayName);
                    DisplayManager::changeDisplay(displayName);
                    stayOpen = false;
                    displayType = {};
                } catch (std::exception &e) {
                    elog << e.what();
                }
            } else
                wlog << "Must specify display name and type!";
        }

        ImGui::End();
        return stayOpen;
    }

    void Project::createNewProject() {
        activeDisplay = "NULL";
        for (auto &d: displays)
            delete (d.second);
        displays.clear();
    }

    static int qCreateProjectSettingsTableNull(void *NotUsed, int columnCount, char **columnData, char **azColName) {
        return 0;
    }

    bool Project::saveProject() {
        int count;
        if (projectHome.empty()) {
            newProjectDialogOpen = true;
            return false;
        }
        properties = PropertiesFormat(trapdoor_home + "/trapdoor.profile");
        if (exists(trapdoor_home + "/trapdoor.profile"))
            properties.load();
        properties.setOverrideProperty("lastOpenProject", projectHome);
        properties.setOverrideProperty("lastOpenFolder", lastFolderOpen);
        properties.save();

        path displaysPath(projectHome + "/displays");
        if (!exists(displaysPath))
            create_directories(displaysPath);
        path resourcesPath(projectHome + "/resources");
        if (!exists(resourcesPath))
            create_directories(resourcesPath);
        path savesPath(projectHome + "/saves");
        if (!exists(savesPath))
            create_directories(savesPath);

        PropertiesFormat project(projectHome + "/project.profile");
        if (exists(projectHome + "/project.profile")) {
            // load it so we don't override other data
            project.load();
        }
        project.setOverrideProperty("projectName", projectName);
        project.save();

        /*if (projectDBLocation != projectHome || !projectDB){
            if (projectDB)
                sqlite3_close(projectDB);
            projectDBLocation = projectHome;
            projectRC = sqlite3_open((projectDBLocation + "/project.profile").c_str(), &projectDB);
            if (rc) {
                flog << "Can't open database! " << db;
                sqlite3_close(projectDB);
                exit(124);
            }
            rc = sqlite3_exec(db, "SELECT * FROM settings;", qLastProject, nullptr, &zErrMsg);
            if (rc != SQLITE_OK) {
                elog << "Error executing querry: " << zErrMsg;
                sqlite3_free(zErrMsg);
            }
        }*/
        discord::Activity activity{};
        activity.SetState(projectName.c_str());
        activity.SetDetails("Making a game");
        activity.GetAssets().SetLargeImage("ben");
        activity.GetAssets().SetSmallImage("character_texture");
        discore->ActivityManager().UpdateActivity(activity, [](discord::Result result) {});
        return saveDisplays();
    }

    void PropertiesFormat::load() {
        if (path.empty()) {
            wlog << "Path is empty!";
            return;
        }
        const unsigned int length = 128 * 1024;
        char buffer[length];
        std::ifstream file(path);
        file.rdbuf()->pubsetbuf(buffer, length);

        int currentLine = 0;
        std::string line;
        while (std::getline(file, line)) {
            if (line.starts_with("#") || line.starts_with("//") || line.starts_with("--")) {
                comments.insert(std::pair(currentLine, line + "\n"));
                currentLine++;
                continue;
            }
            std::vector<std::string> splitStrs;
            boost::split(splitStrs, line, boost::is_any_of("="));

            if (splitStrs.size() < 2) {
                // nulls out lines that aren't valid
                // but keeps the spacing.
                comments.insert(std::pair(currentLine, "\n"));
                currentLine++;
                continue;
            }
            std::string property = splitStrs[0];
            std::string value = splitStrs[1];
            boost::trim(property);
            boost::trim(value);

            properties.insert(std::pair(property, value));
            currentLine++;
        }
        file.close();
    }

    void PropertiesFormat::save() {
        if (path.empty()) {
            wlog << "Path is empty!";
            return;
        }
        const unsigned int length = 128 * 1024;
        char buffer[length];
        std::ofstream file(path);
        file.rdbuf()->pubsetbuf(buffer, length);

        int currentLine = 0;
        auto itr = properties.begin();
        const auto end = properties.end();
        while (itr != end) {
            // we have a comment at this current line.
            if (comments.find(currentLine) != comments.end()) {
                file << comments.at(currentLine);
                comments.erase(currentLine);
                currentLine++;
                continue;
            }
            // otherwise write the next property
            auto property = itr->first;
            auto value = itr->second;
            file << property << " = " << value << "\n";
            currentLine++;
            itr++;
        }
        while (!comments.empty()) {
            if (comments.find(currentLine) != comments.end()) {
                file << comments.at(currentLine);
                comments.erase(currentLine);
            }
            currentLine++;
        }
        file.flush();
        file.close();
    }
}

/**
 *
 * if (!exists(trapdoor_home + "/trapdoor.profile") || !db){
            tlog << "saving " << db;
            if (!db) {
                rc = sqlite3_open((trapdoor_home + "/trapdoor.profile").c_str(), &db);
                if (rc) {
                    flog << "Can't open database! " << db;
                    sqlite3_close(db);
                    exit(124);
                }
            }
        }
        rc = sqlite3_exec(db, "CREATE TABLE IF NOT EXISTS settings (lastProjectPath MEDIUMTEXT, lastOpenFolderPath MEDIUMTEXT);", qCreateProjectSettingsTableNull, nullptr, &zErrMsg);
        if (rc != SQLITE_OK) {
            elog << "Error executing querry: " << zErrMsg;
            sqlite3_free(zErrMsg);
        }
        rc = sqlite3_exec(db, ("INSERT INTO settings(lastProjectPath, lastOpenFolderPath) SELECT '" + projectHome + "', '" + lastFolderOpen + "' WHERE NOT EXISTS (SELECT * FROM settings);").c_str(), qCreateProjectSettingsTableNull, nullptr, &zErrMsg);
        if (rc != SQLITE_OK) {
            elog << "Error executing querry: " << zErrMsg;
            sqlite3_free(zErrMsg);
        }
        rc = sqlite3_open((trapdoor_home + "/trapdoor.profile").c_str(), &db);
            if (rc) {
                flog << "Can't open database! " << db;
                sqlite3_close(db);
                exit(124);
            }
            rc = sqlite3_exec(db, "SELECT * FROM settings;", qLastProject, nullptr, &zErrMsg);
            if (rc != SQLITE_OK) {
                elog << "Error executing querry: " << zErrMsg;
                sqlite3_free(zErrMsg);
            }
 *
 *
 */
