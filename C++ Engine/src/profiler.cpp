//
// Created by brett on 26/07/22.
//

#include "profiler.h"
#include <boost/timer/timer.hpp>
#include "logging.h"
#include "imgui/imgui.h"
#include "font.h"

namespace TD {

    profiler::profiler(std::string name) {
        this->name = name;
        TD::debugUI::addTab(this);
    }

    void profiler::start() {
        auto p1 = std::chrono::high_resolution_clock::now();
        _start = std::chrono::duration_cast<std::chrono::nanoseconds>(p1.time_since_epoch()).count();
    }

    void profiler::end() {
        auto p1 = std::chrono::high_resolution_clock::now();
        _end = std::chrono::duration_cast<std::chrono::nanoseconds>(p1.time_since_epoch()).count();
        _time = _end - _start;
    }

    void profiler::print() {
        ilog << "Profiler recorded " << _time << "ms to run " << name << "!";
    }

    void profiler::endAndPrint() {
        end();
        print();
    }

    void profiler::render() {
        ImGui::Text("CPU Timings:");
        ImGui::Indent();
        ImGui::Text("Elapsed Time:  %fms", (double) (_time / 1000000.0));
        ImGui::Unindent();
        ImGui::NewLine();
    }

    profiler::~profiler() {
        TD::debugUI::deleteTab(this);
    }
}
