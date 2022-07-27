//
// Created by brett on 26/07/22.
//

#ifndef ENGINE_PROFILER_H
#define ENGINE_PROFILER_H

#include <chrono>
#include "renderer/ui/utils.h"

namespace TD {
    class profiler : public DebugTab {
    private:
        long _start = 0;
        long _end = 0;
        long _time = 0;
    public:
        profiler(std::string name);

        void start();

        void end();

        void print();

        void endAndPrint();

        void render();

        ~profiler();
    };
}

#endif //ENGINE_PROFILER_H
