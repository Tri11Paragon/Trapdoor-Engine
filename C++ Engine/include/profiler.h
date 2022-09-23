//
// Created by brett on 26/07/22.
//

#ifndef ENGINE_PROFILER_H
#define ENGINE_PROFILER_H

#include <chrono>
#include "renderer/ui/debug.h"
#include "hashmaps.h"

namespace TD {
    class profiler;

    extern parallel_flat_hash_map<std::string, profiler*> profiles;

    class profiler : public DebugTab {
    private:
        long _start = 0;
        long _end = 0;
        std::unordered_map<std::string, std::pair<long, long>> timings;
    public:
        profiler(std::string name);

        void start();
        void start(std::string name);
        static void start(const std::string& name, const std::string& tabName) {
            auto p = new TD::profiler(name);
            profiles.insert(std::pair(name, p));
            p->start(tabName);
        }

        void end();
        void end(std::string name);
        static void end(const std::string& name, const std::string& tabName){
            try {
                profiles.at(name)->end(tabName);
            } catch (std::exception& e){}
        }

        void print();
        static void print(const std::string& name){
            try {
                profiles.at(name)->print();
                delete(profiles.at(name));
            } catch (std::exception& e){}
        }

        void endAndPrint();
        static void endAndPrint(const std::string& name, const std::string& tabName){
            profiler::end(name, tabName);
            profiler::print(name);
        }

        void render();
        static void render(int count) {
            for (auto p : profiles)
                p.second->render();
        }

        ~profiler();
        static void cleanup(){
            for (const auto& p : profiles)
                delete(p.second);
        }
    };
}

#endif //ENGINE_PROFILER_H
