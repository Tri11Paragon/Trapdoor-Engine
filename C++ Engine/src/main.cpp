#include <iostream>

// https://github.com/ocornut/imgui/tree/master/docs

#include "logging.h"
#include "window.h"
#include <nfd.h>
#include <stdio.h>
#include "input.h"
#include "renderer/ui/debug.h"
#include "renderer/shader.h"
#include "renderer/camera.h"
#include "util.h"
#include "profiler.h"
#include "world/World.h"
#include "game/TestDisplay.h"
#include "world/GameRegistry.h"
#include <config.h>

#include "data/NBT.h"

int main(int, char**){
    tlog << DEBUG_ENABLED_BOOL;
    TD::profiler loadTimer("Load Time");
    loadTimer.start("Load Time");
    init_logging("output");

    const int size = 10000;
    TD::profiler nbtloader("NBT Time");
    nbtloader.start("NBT Write Basic");
    {
        for (int i = 0; i < size; i++) {
            TD::TAG_COMPOUND root("rooterTagger");
            root.put(new TD::TAG_INT("Interino", 5));
            root.put(new TD::TAG_INT("Intry", 420));
            auto *byters = new TD::TAG_COMPOUND("byters");
            byters->put(new TD::TAG_BYTE("Treaty", 53));
            byters->put(new TD::TAG_BYTE_ARRAY("SUNNY",
                                               {23, 43, 2, 4, 50, 10, 04, 65, 94, 86, 49, 39, 95, 42, 68, 29, 24, 42,
                                                21, 49, 23, 49}));
            root.put(byters);
            auto *shortsAndMore = new TD::TAG_COMPOUND("shortsAndMore");
            shortsAndMore->put(new TD::TAG_SHORT("ShortMenandWOMEN", 5230));
            shortsAndMore->put(new TD::TAG_FLOAT("SuperFloat", 52304.04324f));
            shortsAndMore->put(new TD::TAG_DOUBLE("SuperDouble", 452340.593459234));
            shortsAndMore->put(new TD::TAG_LONG("LongestLonger", 5024340304234));
            shortsAndMore->put(new TD::TAG_STRING("Stringy Men", "HELPIMRUNNINGOUTOFCHARACTERSFORME"));
            root.put(shortsAndMore);
            auto *listings = new TD::TAG_LIST("sexistListsy");
            listings->put(new TD::TAG_INT("Doesn'tMatter", 5213));
            listings->put(new TD::TAG_INT("Doesn'tMatter", 5223));
            listings->put(new TD::TAG_INT("Doesn'tMatter", 6213));
            listings->put(new TD::TAG_INT("Doesn'tMatter", 5234423));
            listings->put(new TD::TAG_INT("Doesn'tMatter", 5993));
            listings->put(new TD::TAG_INT("Doesn'tMatter", 9877));
            listings->put(new TD::TAG_INT("Doesn'tMatter", 94835));
            root.put(listings);
            //TD::NBTWriterThreaded threadWriter(root, "superbased.nbt");
            TD::NBTWriter::write(root, "superbased.nbt");
        }
    }
    nbtloader.end("NBT Write Basic");
    nbtloader.start("NBT Read Basic");
    {
        for (int i = 0; i < size; i++) {
            TD::TAG_COMPOUND root = TD::NBTRecursiveReader::read("superbased.nbt");
            if (root.hasTag("Interino"))
                tlog << root.get<TD::TAG_INT>("Interino")->getPayload();
            if (root.hasTag("Intry"))
                tlog << root.get<TD::TAG_INT>("Intry")->getPayload();
            if (root.hasTag("byters")) {
                TD::TAG_COMPOUND *byters = root.get<TD::TAG_COMPOUND>("byters");
                if (byters->hasTag("Treaty"))
                    tlog << std::to_string(byters->get<TD::TAG_BYTE>("Treaty")->getPayload());
                if (byters->hasTag("SUNNY")) {
                    TD::TAG_BYTE_ARRAY *tag = byters->get<TD::TAG_BYTE_ARRAY>("SUNNY");
                    tlog << "Size: " << tag->getPayload().size() << " First Element: " << std::to_string(tag->getPayload()[0]);
                    std::string str = "zBytes{";
                    int sz = tag->getPayload().size();
                    for (int i = 0; i < sz; i++) {
                        str += std::to_string(tag->getPayload()[i]);
                        if (i < sz - 1)
                            str += ", ";
                    }
                    tlog << str << "}";
                }
            }
            if (root.hasTag("shortsAndMore")) {
                TD::TAG_COMPOUND *shorts = root.get<TD::TAG_COMPOUND>("shortsAndMore");
                if (shorts->hasTag("ShortMenandWOMEN"))
                    tlog << shorts->get<TD::TAG_SHORT>("ShortMenandWOMEN")->getPayload();
                if (shorts->hasTag("SuperFloat"))
                    tlog << std::to_string(shorts->get<TD::TAG_FLOAT>("SuperFloat")->getPayload());
                if (shorts->hasTag("SuperDouble"))
                    tlog << std::to_string(shorts->get<TD::TAG_DOUBLE>("SuperDouble")->getPayload());
                if (shorts->hasTag("LongestLonger"))
                    tlog << shorts->get<TD::TAG_LONG>("LongestLonger")->getPayload();
                if (shorts->hasTag("Stringy Men"))
                    tlog << shorts->get<TD::TAG_STRING>("Stringy Men")->getPayload();
            }
            if (root.hasTag("sexistListsy")){
                TD::TAG_LIST* listings = root.get<TD::TAG_LIST>("sexistListsy");
                tlog << listings->getPayload().size();
                for (auto tag : listings->getPayload()){
                    auto ctag = static_cast<TD::TAG_INT*>(tag.get());
                    tlog << ctag->getPayload();
                }
            }
        }
    }
    nbtloader.end("NBT Read Basic");
    nbtloader.print();
    return 0;

    TD::GameRegistry::registerRegistrationCallback([]() -> void* {
        // Register Models
        TD::GameRegistry::registerModel("taylor_plane", "../assets/models/32x32plane_sided.dae");
        TD::GameRegistry::registerModel("kent", "../assets/models/kent.dae");
        TD::GameRegistry::registerModel("plane", "../assets/models/32x32plane.dae");
        TD::GameRegistry::registerModel("sponza", "../assets/models/sponzame/sponza.dae");
        // Register Fonts
        TD::GameRegistry::registerFont("quicksand", "../assets/fonts/quicksand/Quicksand-Regular.ttf", 16.0f);
        TD::GameRegistry::registerFont("roboto", "../assets/fonts/roboto/Roboto-Regular.ttf", 16.0f);
        return nullptr;
    });

    TD::DisplayManager::init("Trapdoor " + std::to_string(ENGINE_VERSION_MAJOR) + "." + std::to_string(ENGINE_VERSION_MINOR) + "."
            + std::to_string(ENGINE_VERSION_PATCH) + " // C++ Test");

    // Standard Defered about 120fps @ 1024 lights (8.5ms)

    // will automatically be cleaned up when the display manager exits
    new TD::TestDisplay("TestDisplay");
    TD::DisplayManager::changeDisplay("TestDisplay");


    loadTimer.end("Load Time");
    loadTimer.print();
    TD::DisplayManager::update();
    TD::DisplayManager::close();
    return 0;
}
