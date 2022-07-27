//
// Created by brett on 26/07/22.
//

#ifndef ENGINE_PROFILER_H
#define ENGINE_PROFILER_H

#include <boost/timer/timer.hpp>

namespace TD {
    class profiler {
    private:
        boost::timer::cpu_timer timer;
    public:
        profiler();

        void start();

        void end();

        void print();

        void endAndPrint();

        void addToDebugMenu();

        ~profiler();
    };
}

#endif //ENGINE_PROFILER_H
