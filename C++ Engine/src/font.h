//
// Created by brett on 21/07/22.
//

#ifndef ENGINE_FONT_H
#define ENGINE_FONT_H

#include <string>
#include <vector>
#include <map>
#include "logging.h"
#include "imgui/imgui.h"

using namespace std;
namespace TD {
    class font {
    private:
        string _path;
        string _name;
        float _size;
    public:
        font(string name, string path, float size) {
            this->_name = name;
            this->_path = path;
            this->_size = size;
        }

        string getPath() {
            return _path;
        }

        float getSize() {
            return _size;
        }

        string getName() {
            return _name;
        }
    };

    extern unordered_map<string, ImFont *> loadedFonts;
    extern vector <font> _fonts;

    class fontContext {
    private:
        fontContext();

    public:
        static void loadContexts(vector <font> fonts) {
            _fonts = fonts;
        }
        static void load(ImGuiIO &io) {
            if (_fonts.size() > 0) {
                for (int i = 0; i < _fonts.size(); i++) {
                    ImFont *loadedFont = io.Fonts->AddFontFromFileTTF(_fonts[i].getPath().c_str(), _fonts[i].getSize());
                    IM_ASSERT(loadedFont);
                    loadedFonts[_fonts[i].getName()] = loadedFont;
                    dlog << "Loaded font " << _fonts[i].getName() << " @ " << _fonts[i].getPath() << " ("
                         << _fonts[i].getSize() << ")\n";
                }
            }
        }
        static ImFont *get(const string &key) {
            return loadedFonts[key];
        }

        ImFont *operator[](const string &key) {
            return loadedFonts[key];
        }

        ImFont *operator[](string &&key) {
            return loadedFonts[key];
        }
    };
}
#endif //ENGINE_FONT_H
