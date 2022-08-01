//
// Created by brett on 26/07/22.
//

#include "profiler.h"
#include "logging.h"
#include "imgui/imgui.h"
#include "font.h"

namespace TD {

    profiler::profiler(std::string name) {
        this->name = name;
        TD::debugUI::addTab(this);
    }

    void profiler::start() {
        start("Unnamed");
    }
    void profiler::start(std::string name) {
        auto p1 = std::chrono::high_resolution_clock::now();
        _start = std::chrono::duration_cast<std::chrono::nanoseconds>(p1.time_since_epoch()).count();
        timings[name] = std::pair<long, long>(_start, 0);
    }

    void profiler::end() {
        end("Unnamed");
    }
    void profiler::end(std::string name) {
        auto p1 = std::chrono::high_resolution_clock::now();
        _end = std::chrono::duration_cast<std::chrono::nanoseconds>(p1.time_since_epoch()).count();
        timings[name] = std::pair<long, long>(timings[name].first, _end);
    }

    void profiler::print() {
        ilog << "Profiler " << name << " recorded: ";
        for (std::pair<std::string, std::pair<long, long>> e : timings){
            ilog << "\t" << e.first << " took " << ((double)(e.second.second - e.second.first) / 1000000.0) << "ms to run!";
        }

    }

    void profiler::endAndPrint() {
        end();
        print();
    }

    void profiler::render() {
        ImGui::Text("CPU Timings:");
        ImGui::Indent();
        for (std::pair<std::string, std::pair<long, long>> e : timings) {
            ImGui::Text("Elapsed Time(%s):  %fms", e.first.c_str(), (double) ((e.second.second - e.second.first) / 1000000.0));
        }
        ImGui::Unindent();
        ImGui::NewLine();
    }

    profiler::~profiler() {
        TD::debugUI::deleteTab(this);
    }
}
