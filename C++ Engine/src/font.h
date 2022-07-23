//
// Created by brett on 21/07/22.
//

#ifndef ENGINE_FONT_H
#define ENGINE_FONT_H

#include <string>
#include <vector>
#include <map>
#include "logging.h"

using namespace std;
class font{
private:
    string _path;
    string _name;
    float _size;
public:
    font(string name, string path, float size){
        this->_name = name;
        this->_path = path;
        this->_size = size;
    }
    string getPath(){
        return _path;
    }
    float getSize(){
        return _size;
    }
    string getName(){
        return _name;
    }
};

class fontContext{
private:
    vector<font> _fonts;
public:
    unordered_map<string, ImFont*> fonts;
    fontContext(vector<font> fonts){
        this->_fonts = fonts;
    }
    void load(ImGuiIO& io){
        if (_fonts.size() > 0){
            for (int i = 0; i < _fonts.size(); i++){
                ImFont* loadedFont = io.Fonts->AddFontFromFileTTF(_fonts[i].getPath().c_str(), _fonts[i].getSize());
                IM_ASSERT(loadedFont);
                fonts[_fonts[i].getName()] = loadedFont;
                dlog << "Loaded font " << _fonts[i].getName() << " @ " << _fonts[i].getPath() << " (" << _fonts[i].getSize() << ")\n";
            }
        }
    }
    ImFont* operator [](const string& key){
        return fonts[key];
    }
    ImFont* operator [](string&& key){
        return fonts[key];
    }
};

#endif //ENGINE_FONT_H
