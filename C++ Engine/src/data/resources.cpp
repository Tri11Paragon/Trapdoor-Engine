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
using namespace boost::filesystem;

namespace TD {

    extern int _display_w, _display_h;
    extern std::unordered_map<std::string, TD::Display*> displays;
    extern std::string activeDisplay;

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

    std::string Resources::getTrapdoorHome() {return trapdoor_home;}
    std::string Resources::getUserHome() {return user_home;}

    /** ============{ Project }============ **/
    sqlite3 *db;
    char *zErrMsg = nullptr;
    int rc;

    sqlite3 *projectDB;
    char *projectErrMsg = nullptr;
    int projectRC;

    std::string projectHome {};
    std::string projectName {};
    std::string lastFolderOpen {};

    static int qInsertProjectSettingsTableEmpty(void *NotUsed, int columnCount, char **columnData, char **azColName){
        return 0;
    }

    static int qLastProject(void *NotUsed, int columnCount, char **columnData, char **azColName){
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
            int lrc = sqlite3_exec(db, "INSERT INTO settings VALUES (NULL, NULL);", qInsertProjectSettingsTableEmpty, nullptr, &lErrMsg);
            if (lrc != SQLITE_OK) {
                elog << "Error executing querry: " << lErrMsg;
                sqlite3_free(lErrMsg);
            }
            return SQLITE_NOTFOUND;
        }
    }

    bool Project::init() {
        TD::registerAllocators();
        if (exists(trapdoor_home + "/trapdoor.profile")) {
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
            TD::Editor::setToOpen();
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
        loadDisplays();
    }

    bool Project::saveDisplays() {

        return true;
    }

    bool Project::loadDisplays() {
        if (projectHome.empty())
            return false;

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

    bool Project::setProjectLocationDialog(std::string& out) {
        nfdchar_t *outPath = nullptr;
        nfdresult_t result = NFD_PickFolder( lastFolderOpen.empty() ? nullptr : lastFolderOpen.c_str(), &outPath );
        if ( result == NFD_OKAY ) {
            out = outPath;
            free(outPath);
            lastFolderOpen = out;
            return true;
        } else if ( result == NFD_CANCEL )
            elog << "User pressed cancel.";
        else
            elog << NFD_GetError();
        return false;
    }

    bool Project::showNewProjectDialog() {
        bool stayOpen = true;
        ImGui::SetNextWindowPos(ImVec2((float)_display_w /2, (float)_display_h / 2), ImGuiCond_Appearing, ImVec2(0.5f, 0.5f));
        ImGui::Begin("New Project", &stayOpen, ImGuiWindowFlags_AlwaysAutoResize | ImGuiWindowFlags_NoCollapse | ImGuiWindowFlags_NoSavedSettings);

        static char stringBuffer[512]{};

        ImGui::Text("Project Name: ");
        ImGui::InputText("##Name", stringBuffer, 512, ImGuiInputTextFlags_EnterReturnsTrue);
        Strtrim(stringBuffer);
        if (stringBuffer[0]){
            projectName = std::string(stringBuffer);
            boost::trim(projectName);
        }

        ImGui::Text("Project Directory: ");
        ImGui::SameLine();
        ImGui::Text("%s", projectHome.c_str());
        if (ImGui::Button("Select Directory")){
            setProjectLocationDialog(projectHome);
        }
        ImGui::NewLine();
        if (ImGui::Button("Create New Project")){
            if (projectHome.empty() || projectName.empty()){
                wlog << "Project name and home must not be empty!";
            } else {
                createNewProject();
                stayOpen = false;
            }
        }

        ImGui::End();
        return stayOpen;
    }

    bool Project::showNewScreenDialog() {
        return false;
    }

    void Project::createNewProject() {
        activeDisplay = "NULL";
        for (auto& d : displays)
            delete(d.second);
        displays.clear();
    }

    static int qCreateProjectSettingsTableNull(void *NotUsed, int columnCount, char **columnData, char **azColName) {
        return 0;
    }

    bool Project::saveProject() {
        int count;
        if (projectHome.empty())
            while (!setProjectLocationDialog(projectHome)) {wlog << "You must set a path to save!"; count++; if (count > 2){elog << "Fine, I'm not going to save then!"; return false;}}
        if (!exists(trapdoor_home + "/trapdoor.profile") || !db){
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
        path displaysPath (projectHome + "/displays");
        if (!exists(displaysPath))
            create_directories(displaysPath);
        path resourcesPath (projectName + "/resources");
        if (!exists(resourcesPath))
            create_directories(resourcesPath);
        path savesPath (projectName + "/saves");
        if (!exists(savesPath))
            create_directories(savesPath);
        return saveDisplays();
    }
}
