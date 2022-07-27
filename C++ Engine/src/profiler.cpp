//
// Created by brett on 26/07/22.
//

#include "profiler.h"
#include <boost/timer/timer.hpp>
#include "logging.h"
#include "imgui/imgui.h"
#include "font.h"

namespace TD {

    profiler::profiler() {
        timer = boost::timer::cpu_timer();
    }

    void profiler::start() {
        timer.resume();
    }

    void profiler::end() {
        timer.stop();
    }

    void profiler::print() {
        ilog << "Profiler recorded " << timer.format();
    }

    void profiler::endAndPrint() {
        end();
        print();
    }

    void profiler::addToDebugMenu() {
        boost::timer::cpu_times times = timer.elapsed();
        ImGui::PushFont(TD::fontContext::get("roboto"));
        ImGui::Begin("Profiler Realtime Results");

        ImGui::Text("");

        ImGui::End();
        ImGui::PopFont();
    }

    profiler::~profiler() {

    }
}
