//
// Created by brett on 26/07/22.
//

#ifndef ENGINE_PROFILER_H
#define ENGINE_PROFILER_H

#include <chrono>
#include "renderer/ui/ui.h"

namespace TD {
    class profiler : public DebugTab {
    private:
        long _start = 0;
        long _end = 0;
        std::unordered_map<std::string, std::pair<long, long>> timings;
    public:
        profiler(std::string name);

        void start();
        void start(std::string name);

        void end();
        void end(std::string name);

        void print();

        void endAndPrint();

        void render();

        ~profiler();
    };
}

#endif //ENGINE_PROFILER_H
