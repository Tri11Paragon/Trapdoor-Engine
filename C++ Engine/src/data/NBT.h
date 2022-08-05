//
// Created by brett on 03/08/22.
//

#ifndef ENGINE_NBT_H
#define ENGINE_NBT_H

#include "../std.h"
#include "DataConv.h"
#include "../hashmaps.h"
#include <thread>

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
    protected:
        std::string name;
        unsigned char type;
    public:
        NBT_TAG(unsigned char type){this->type = type;}
        NBT_TAG(const std::string &name, const unsigned char &type) : name(name), type(type) {}

        inline std::string getName(){return name;}
        inline unsigned char getType(){return type;}

        void writeName(std::ofstream &file) {
            char data[2];
            TD::DataConv::getShort(name.size(), data);
            file.write(data, 2);
            for (int i = 0; i < name.size(); i++)
                file << this->name[i];
        }

        void writeType(std::ofstream &file) {
            file << type;
        }

        inline virtual void writePayload(std::ofstream &file) = 0;

        inline virtual void readPayload(std::ifstream &file) = 0;

        void readName(std::ifstream &file) {
            short size = 0;
            char sizeArr[2];
            file.read(sizeArr, 2);
            size = TD::DataConv::getShort(sizeArr);
            char str[size];
            file.read(str, size);
            this->name = std::string(str, size);
        }

        void readType(std::ifstream &file) {
            char type;
            file.read(&type, 1);
            this->type = (unsigned char)type;
        }
        virtual ~NBT_TAG(){}
    };

    class TAG_BYTE : public NBT_TAG {
    private:
        signed char payload;
    public:
        TAG_BYTE(): NBT_TAG(ID_TAG_BYTE){}
        TAG_BYTE(const std::string& name): NBT_TAG(name, ID_TAG_BYTE){}
        TAG_BYTE(const std::string& name, const signed char payload) : NBT_TAG(name, ID_TAG_BYTE) { this->payload = payload; }

        inline signed char getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file){file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_SHORT : public NBT_TAG {
    private:
        signed short payload;
    public:
        TAG_SHORT(): NBT_TAG(ID_TAG_SHORT){}
        TAG_SHORT(const std::string& name): NBT_TAG(name, ID_TAG_SHORT){}
        TAG_SHORT(const std::string& name, const signed short payload) : NBT_TAG(name, ID_TAG_SHORT) { this->payload = payload; }

        inline signed short getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_INT : public NBT_TAG {
    private:
        signed int payload;
    public:
        TAG_INT(): NBT_TAG(ID_TAG_INT){}
        TAG_INT(const std::string& name): NBT_TAG(name, ID_TAG_INT){}
        TAG_INT(const std::string& name, const signed int payload) : NBT_TAG(name, ID_TAG_INT) { this->payload = payload;}

        inline signed int getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_LONG : public NBT_TAG {
    private:
        signed long payload;
    public:
        TAG_LONG(): NBT_TAG(ID_TAG_LONG){}
        TAG_LONG(const std::string& name): NBT_TAG(name, ID_TAG_LONG){}
        TAG_LONG(const std::string& name, const signed long payload) : NBT_TAG(name, ID_TAG_LONG) { this->payload = payload; }

        inline long getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {file << payload;}
        inline virtual void readPayload(std::ifstream &file) {file >> payload;}
    };

    class TAG_FLOAT : public NBT_TAG {
    private:
        float payload;
    public:
        TAG_FLOAT(): NBT_TAG(ID_TAG_FLOAT) {}
        TAG_FLOAT(const std::string& name): NBT_TAG(name, ID_TAG_FLOAT){}
        TAG_FLOAT(const std::string& name, const float payload) : NBT_TAG(name, ID_TAG_FLOAT) { this->payload = payload; }

        inline float getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {
            char data[4];
            TD::DataConv::getFloat(payload, data);
            file.write(data, 4);
        }
        inline virtual void readPayload(std::ifstream &file) {
            char data[4];
            file.read(data, 4);
            this->payload = TD::DataConv::getFloat(data);
        }
    };

    class TAG_DOUBLE : public NBT_TAG {
    private:
        double payload;
    public:
        TAG_DOUBLE(): NBT_TAG(ID_TAG_DOUBLE){}
        TAG_DOUBLE(const std::string& name): NBT_TAG(name, ID_TAG_DOUBLE){}
        TAG_DOUBLE(const std::string& name, const double payload) : NBT_TAG(name, ID_TAG_DOUBLE) { this->payload = payload; }

        inline double getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {
            char data[8];
            TD::DataConv::getDouble(payload, data);
            file.write(data, 8);
        }
        inline virtual void readPayload(std::ifstream &file) {
            char data[8];
            file.read(data, 8);
            this->payload = TD::DataConv::getDouble(data);
        }
    };

    class TAG_STRING : public NBT_TAG {
    private:
        std::string payload;
    public:
        TAG_STRING(): NBT_TAG(ID_TAG_STRING){}
        TAG_STRING(const std::string& name): NBT_TAG(name, ID_TAG_STRING){}
        TAG_STRING(const std::string& name, const std::string& payload) : NBT_TAG(name, ID_TAG_STRING) { this->payload = payload;}

        inline std::string getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {
            char bytes[2];
            TD::DataConv::getShort(this->payload.size(), bytes);
            file.write(bytes, 2);
            for (int i = 0; i < payload.size(); i++)
                file << this->payload[i];
        }
        inline virtual void readPayload(std::ifstream &file) {
            short size = 0;
            char sizeArr[2];
            file.read(sizeArr, 2);
            size = TD::DataConv::getShort(sizeArr);
            char str[size];
            file.read(str, size);
            this->payload = std::string(str, size);
        }
    };

    class TAG_BYTE_ARRAY : public NBT_TAG {
    private:
        std::vector<signed char> payload;
    public:
        TAG_BYTE_ARRAY(): NBT_TAG(ID_TAG_BYTE_ARRAY){}
        TAG_BYTE_ARRAY(const std::string& name): NBT_TAG(name, ID_TAG_BYTE_ARRAY){}
        TAG_BYTE_ARRAY(const std::string& name, std::vector<signed char> payload) : NBT_TAG(name, ID_TAG_BYTE_ARRAY) { this->payload = payload;}

        inline std::vector<signed char>& getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {
            char data[4];
            TD::DataConv::getInt(payload.size(), data);
            file.write(data, 4);
            for (int i = 0; i < payload.size(); i++) {
                file << payload[i];
            }
        }
        inline virtual void readPayload(std::ifstream &file) {
            char sizeData[4];
            file.read(sizeData, 4);
            int size = TD::DataConv::getInt(sizeData);
            std::vector<signed char> ptr;
            for (int i = 0; i < size; i++) {
                char byt;
                file.read(&byt, 1);
                ptr.push_back(byt);
            }
            this->payload = ptr;
        }
    };

    class TAG_INT_ARRAY : public NBT_TAG {
    private:
        std::vector<int> payload;
    public:
        TAG_INT_ARRAY(): NBT_TAG(ID_TAG_INT_ARRAY){}
        TAG_INT_ARRAY(const std::string& name): NBT_TAG(name, ID_TAG_INT_ARRAY){}
        TAG_INT_ARRAY(const std::string& name, std::vector<int> payload) : NBT_TAG(name, ID_TAG_INT_ARRAY) { this->payload = payload;}

        inline std::vector<int>& getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {
            char data[4];
            TD::DataConv::getInt(payload.size(), data);
            file.write(data, 4);
            for (int i = 0; i < payload.size(); i++) {
                char lData[4];
                TD::DataConv::getInt(payload[i], lData);
                file.write(lData, 4);
            }
        }
        inline virtual void readPayload(std::ifstream &file) {
            char sizeData[4];
            file.read(sizeData, 4);
            int size = TD::DataConv::getInt(sizeData);
            std::vector<int> ptr;
            for (int i = 0; i < size; i++) {
                char byt[4];
                file.read(byt, 4);
                ptr.push_back(TD::DataConv::getInt(byt));
            }
            this->payload = ptr;
        }
    };

    class TAG_LONG_ARRAY : public NBT_TAG {
    private:
        std::vector<long> payload;
    public:
        TAG_LONG_ARRAY(): NBT_TAG(ID_TAG_LONG_ARRAY){}
        TAG_LONG_ARRAY(const std::string& name): NBT_TAG(name, ID_TAG_LONG_ARRAY){}
        TAG_LONG_ARRAY(const std::string& name, std::vector<long> payload) : NBT_TAG(name, ID_TAG_LONG_ARRAY) { this->payload = payload;}

        inline std::vector<long>& getPayload(){return payload;}
        inline virtual void writePayload(std::ofstream &file) {
            char data[4];
            TD::DataConv::getInt(payload.size(), data);
            file.write(data, 4);
            for (int i = 0; i < payload.size(); i++) {
                char lData[8];
                TD::DataConv::getLong(payload[i], lData);
                file.write(lData, 8);
            }
        }
        inline virtual void readPayload(std::ifstream &file) {
            char sizeData[4];
            file.read(sizeData, 4);
            int size = TD::DataConv::getInt(sizeData);
            std::vector<long> ptr;
            for (int i = 0; i < size; i++) {
                char byt[8];
                file.read(byt, 8);
                ptr.push_back(TD::DataConv::getLong(byt));
            }
            this->payload = ptr;
        }
    };

    static shared_ptr<NBT_TAG> getCompound();

    class TAG_LIST : public NBT_TAG {
    private:
        std::vector<std::shared_ptr<NBT_TAG>> payload;
        unsigned char lType = 0;
    public:
        TAG_LIST(): NBT_TAG(ID_TAG_LIST){}
        TAG_LIST(const std::string& name): NBT_TAG(name, ID_TAG_LIST){}
        TAG_LIST(const std::string& name, std::vector<std::shared_ptr<NBT_TAG>> payload) : NBT_TAG(name, ID_TAG_LIST) { this->payload = payload;}

        inline std::vector<std::shared_ptr<NBT_TAG>>& getPayload(){return payload;}
        inline void put(NBT_TAG* tag){
            if (lType == 0)
                lType = tag->getType();
            std::shared_ptr<NBT_TAG> taggers = std::shared_ptr<NBT_TAG>(tag);
            payload.push_back(taggers);
        }

        inline virtual void writePayload(std::ofstream &file) {
            if (payload.size() <= 0) {
                file << ID_TAG_END;
                return;
            }
            file << lType;
            char data[4];
            TD::DataConv::getInt(payload.size(), data);
            file.write(data, 4);
            for (int i = 0; i < payload.size(); i++)
                payload[i]->writePayload(file);
        }
        inline virtual void readPayload(std::ifstream &file) {
            char sid;
            file.read(&sid, 1);
            lType = (unsigned char) sid;
            char sizeData[4];
            file.read(sizeData, 4);
            int size = TD::DataConv::getInt(sizeData);
            tlog << "Reading! " << std::to_string(lType) << " " << size;
            for (int i = 0; i < size; i++){
                std::shared_ptr<NBT_TAG> taggers;
                switch (lType){
                    case ID_TAG_BYTE: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_BYTE());
                        break;
                    }
                    case ID_TAG_SHORT: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_SHORT());
                        break;
                    }
                    case ID_TAG_INT: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_INT());
                        break;
                    }
                    case ID_TAG_LONG: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_LONG());
                        break;
                    }
                    case ID_TAG_FLOAT: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_FLOAT);
                        break;
                    }
                    case ID_TAG_DOUBLE: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_DOUBLE());
                        break;
                    }
                    case ID_TAG_BYTE_ARRAY: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_BYTE_ARRAY());
                        break;
                    }
                    case ID_TAG_STRING: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_STRING());
                        break;
                    }
                    case ID_TAG_LIST: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_LIST());
                        break;
                    }
                    case ID_TAG_COMPOUND: {
                        taggers = getCompound();
                        break;
                    }
                    case ID_TAG_INT_ARRAY: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_INT_ARRAY());
                        break;
                    }
                    case ID_TAG_LONG_ARRAY: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_LONG_ARRAY());
                        break;
                    }
                    default: {
                        continue;
                    }
                }
                taggers->readPayload(file);
                payload.push_back(taggers);
            }
        }
    };

    class TAG_COMPOUND : public NBT_TAG {
    private:
        std::vector<std::shared_ptr<NBT_TAG>> tags;
        parallel_flat_hash_map<std::string, std::shared_ptr<NBT_TAG>> tagMap;
    public:
        TAG_COMPOUND(): NBT_TAG(ID_TAG_COMPOUND){}
        TAG_COMPOUND(const std::string& name): NBT_TAG(name, ID_TAG_COMPOUND){}
        TAG_COMPOUND(const std::string& name, std::vector<std::shared_ptr<NBT_TAG>> payload) : NBT_TAG(name, ID_TAG_COMPOUND) {
            this->tags = payload;
            for (std::shared_ptr<NBT_TAG> t : payload)
                tagMap.insert(std::pair(t->getName(), t));
        }

        inline std::vector<std::shared_ptr<NBT_TAG>> getPayload(){return tags;}
        inline void put(NBT_TAG* tag){
            std::shared_ptr<NBT_TAG> taggers = std::shared_ptr<NBT_TAG>(tag);
            tags.push_back(taggers);
            tagMap[tag->getName()] = taggers;
        }
        inline void put(std::shared_ptr<NBT_TAG> tag){tags.push_back(tag); tagMap[tag->getName()] = tag;}
        template<class T>
        inline T* get(std::string tag){
            return static_cast<T*>(tagMap[tag].get());
        }
        inline bool hasTag(std::string tag){
            return tagMap.find(tag) != tagMap.end();
        }

        inline virtual void writePayload(std::ofstream &file) {
            for (int i = 0; i < tags.size(); i++) {
                tags[i]->writeType(file);
                tags[i]->writeName(file);
                tags[i]->writePayload(file);
            }
            file << ID_TAG_END;
        }
        inline virtual void readPayload(std::ifstream &file) {
            unsigned char id;
            do {
                char sid;
                file.read(&sid, 1);
                id = (unsigned char) sid;
                std::shared_ptr<NBT_TAG> taggers;
                switch (id){
                    case ID_TAG_BYTE: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_BYTE());
                        break;
                    }
                    case ID_TAG_SHORT: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_SHORT());
                        break;
                    }
                    case ID_TAG_INT: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_INT());
                        break;
                    }
                    case ID_TAG_LONG: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_LONG());
                        break;
                    }
                    case ID_TAG_FLOAT: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_FLOAT);
                        break;
                    }
                    case ID_TAG_DOUBLE: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_DOUBLE());
                        break;
                    }
                    case ID_TAG_BYTE_ARRAY: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_BYTE_ARRAY());
                        break;
                    }
                    case ID_TAG_STRING: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_STRING());
                        break;
                    }
                    case ID_TAG_LIST: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_LIST());
                        break;
                    }
                    case ID_TAG_COMPOUND: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_COMPOUND());
                        break;
                    }
                    case ID_TAG_INT_ARRAY: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_INT_ARRAY());
                        break;
                    }
                    case ID_TAG_LONG_ARRAY: {
                        taggers = std::shared_ptr<NBT_TAG>(new TAG_LONG_ARRAY());
                        break;
                    }
                    default: {
                        return;
                    }
                }
                taggers->readName(file);
                taggers->readPayload(file);
                tags.push_back(taggers);
                tagMap.insert(std::pair(taggers->getName(), taggers));
            } while (id != ID_TAG_END);
        }
    };

    static shared_ptr<NBT_TAG> getCompound(){
        return std::shared_ptr<NBT_TAG>(new TAG_COMPOUND());
    }

    class NBTRecursiveReader {
    public:
        static TAG_COMPOUND read(std::string path){
            const unsigned int length = 128*1024;
            char buffer[length];
            std::ifstream file;
            file.rdbuf()->pubsetbuf(buffer, length);
            file.open(path, std::ios::binary);

            TAG_COMPOUND root;
            char type;
            file.read(&type, 1);
            if (type != ID_TAG_COMPOUND) {
                flog << "File started with " << root.getType();
                flog << "Now, im sure you know the rules, but files must start with TAG_COMPOUND!";
                throw runtime_error("Error! File does not start with TAG_COMPOUND!");
            }
            root.readName(file);
            root.readPayload(file);

            file.close();

            return root;
        }
    };

    class NBTWriter {
    private:
    public:
        static void write(TAG_COMPOUND& compound, const std::string& path){
            const unsigned int length = 256*1024;
            char buffer[length];
            std::ofstream file;
            file.rdbuf()->pubsetbuf(buffer, length);
            file.open(path, std::ios::binary);

            compound.writeType(file);
            compound.writeName(file);
            compound.writePayload(file);

            file.close();
        }
    };

    class NBTWriterThreaded{
    private:
        std::thread* thread;
    public:
        NBTWriterThreaded(TAG_COMPOUND& compound, const std::string& path){
            thread = new std::thread([&]() -> void {
                NBTWriter::write(compound, path);
            });
        }
        ~NBTWriterThreaded(){
            thread->join();
            delete(thread);
        }
    };


}

#endif //ENGINE_NBT_H
