//
// Created by brett on 03/08/22.
//

#include <sstream>
#include "NBT.h"

namespace TD {

    void NBT_TAG::writeName(std::ofstream &file) {
        file << (short) this->name.size();
        file << this->name;
    }

    void NBT_TAG::writeType(std::ofstream &file) {
        file << type;
    }

    void NBT_TAG::readName(std::ifstream &file) {
        short size = 0;
        file >> size;
        char str[size];
        file.read(str, size);
        this->name = std::string(str);
    }

    void NBT_TAG::readType(std::ifstream &file) {
        file >> type;
    }

}
