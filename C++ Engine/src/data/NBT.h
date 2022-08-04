//
// Created by brett on 03/08/22.
//

#ifndef ENGINE_NBT_H
#define ENGINE_NBT_H

#include "../std.h"
#include "DataConv.h"

namespace TD {

    constexpr unsigned char ID_TAG_END = 0;
    constexpr unsigned char ID_TAG_BYTE = 1;
    constexpr unsigned char ID_TAG_SHORT = 2;
    constexpr unsigned char ID_TAG_INT = 3;
    constexpr unsigned char ID_TAG_LONG = 4;
    constexpr unsigned char ID_TAG_FLOAT = 5;
    constexpr unsigned char ID_TAG_DOUBLE = 6;
    constexpr unsigned char ID_TAG_BYTE_ARRAY = 7;
    constexpr unsigned char ID_TAG_STRING = 8;
    constexpr unsigned char ID_TAG_LIST = 9;
    constexpr unsigned char ID_TAG_COMPOUND = 10;
    constexpr unsigned char ID_TAG_INT_ARRAY = 11;
    constexpr unsigned char ID_TAG_LONG_ARRAY = 12;

    class NBT_TAG {
    private:
        std::string name;
        unsigned char type;
    public:
        NBT_TAG() {/* read only move. */}
        NBT_TAG(const std::string &name, const unsigned char &type) : name(name), type(type) {}

        void writeName(std::ofstream &file);

        void writeType(std::ofstream &file);

        inline virtual void writePayload(std::ofstream &file) {};

        inline virtual void readPayload(std::ifstream &file) {};

        void readName(std::ifstream &file);

        void readType(std::ifstream &file);
    };

    class TAG_BYTE : NBT_TAG {
    private:
        signed char payload;
    public:
        TAG_BYTE() {}
        TAG_BYTE(const std::string& name, const signed char payload) : NBT_TAG(name, ID_TAG_BYTE) { this->payload = payload; }

        inline signed char getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file){file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_SHORT : NBT_TAG {
    private:
        signed short payload;
    public:
        TAG_SHORT() {}
        TAG_SHORT(const std::string& name, const signed short payload) : NBT_TAG(name, ID_TAG_SHORT) { this->payload = payload; }

        inline signed short getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_INT : NBT_TAG {
    private:
        signed int payload;
    public:
        TAG_INT() {}
        TAG_INT(const std::string& name, const signed int payload) : NBT_TAG(name, ID_TAG_INT) { this->payload = payload; }

        inline signed int getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_LONG : NBT_TAG {
    private:
        signed long payload;
    public:
        TAG_LONG() {}
        TAG_LONG(const std::string& name, const signed long payload) : NBT_TAG(name, ID_TAG_LONG) { this->payload = payload; }

        inline long getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_FLOAT : NBT_TAG {
    private:
        float payload;
    public:
        TAG_FLOAT() {}
        TAG_FLOAT(const std::string& name, const float payload) : NBT_TAG(name, ID_TAG_FLOAT) { this->payload = payload; }

        inline float getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_DOUBLE : NBT_TAG {
    private:
        double payload;
    public:
        TAG_DOUBLE() {}
        TAG_DOUBLE(const std::string& name, const double payload) : NBT_TAG(name, ID_TAG_DOUBLE) { this->payload = payload; }

        inline double getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_STRING : NBT_TAG {
    private:
        std::string payload;
    public:
        TAG_STRING() {}
        TAG_STRING(const std::string& name, std::string& payload) : NBT_TAG(name, ID_TAG_STRING) { this->payload = payload;}

        inline std::string getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {

        }
        inline virtual void readPayload(std::ifstream &file) {

        }
    };

    class TAG_BYTE_ARRAY : NBT_TAG {
    private:
        signed char* payload;
        signed int size;
    public:
        TAG_BYTE_ARRAY() {}
        TAG_BYTE_ARRAY(const std::string& name, signed char* payload, signed int size) : NBT_TAG(name, ID_TAG_BYTE_ARRAY) { this->payload = payload; this->size = size;}
        TAG_BYTE_ARRAY(const std::string& name, std::vector<signed char> payload) : NBT_TAG(name, ID_TAG_BYTE_ARRAY) { this->payload = payload.data(); this->size = payload.size();}

        inline signed int getSize(){return size;}
        inline signed char* getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {
            file << size;
            for (int i = 0; i < size; i++)
                file << payload[i];
        }
        inline virtual void readPayload(std::ifstream &file) {
            file >> size;
            payload[size];
            for (int i = 0; i < size; i++)
                file >> payload[i];
        }
    };

    class TAG_LIST : NBT_TAG {
    private:
        NBT_TAG* payload;
        unsigned char type;
        signed int size;
    public:
        TAG_LIST() {}
        TAG_LIST(const std::string& name, NBT_TAG* payload, signed int size) : NBT_TAG(name, ID_TAG_LIST) { this->payload = payload; this->size = size;}
        TAG_LIST(const std::string& name, std::vector<NBT_TAG> payload) : NBT_TAG(name, ID_TAG_LIST) { this->payload = payload.data(); this->size = payload.size();}

        inline unsigned char getType(){return type;}
        inline signed int getSize(){return size;}
        inline NBT_TAG* getPayload(){return payload;}

        inline virtual void writePayload(std::ofstream &file) {
            if (size <= 0) {
                file << ID_TAG_END;
                return;
            }
            NBT_TAG baseTag = static_cast<NBT_TAG>(payload[0]);
            baseTag.writeType(file);
            file << size;
            for (int i = 0; i < size; i++)
                static_cast<NBT_TAG>(payload[i]).writePayload(file);
        }
        inline virtual void readPayload(std::ifstream &file) {
            file >> type;
            file >> size;
            payload[size];
            switch (type){
                case ID_TAG_BYTE:
                    break;
                case ID_TAG_SHORT:
                    break;
                case ID_TAG_INT:
                    break;
                case ID_TAG_LONG:
                    break;
                case ID_TAG_FLOAT:
                    break;
                case ID_TAG_DOUBLE:
                    break;
                case ID_TAG_BYTE_ARRAY:
                    break;
                case ID_TAG_STRING:
                    break;
                case ID_TAG_LIST:
                    break;
                case ID_TAG_COMPOUND:
                    break;
                case ID_TAG_INT_ARRAY:
                    break;
                case ID_TAG_LONG_ARRAY:
                    break;
            }
            for (int i = 0; i < size; i++) {
                //file >> payload[i];
            }
        }
    };

    class NBTRecursiveReader {

    };


}

#endif //ENGINE_NBT_H
