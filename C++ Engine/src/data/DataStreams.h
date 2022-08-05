//
// Created by brett on 05/08/22.
//

#ifndef ENGINE_DATASTREAMS_H
#define ENGINE_DATASTREAMS_H

#include "../std.h"
#include "DataConv.h"

#include <zlib.h>

#if defined(MSDOS) || defined(OS2) || defined(WIN32) || defined(__CYGWIN__)
#  include <fcntl.h>
#  include <io.h>
#  define SET_BINARY_MODE(file) setmode(fileno(file), O_BINARY)
#else
#  define SET_BINARY_MODE(file)
#endif

namespace TD {

    class DataOutputStream {
    private:
        std::ofstream output;
    public:
        DataOutputStream(std::string file, const bool compressed = true, const int bufferSize = 128*1024){
            
        }
        ~DataOutputStream(){

        }
    };

} // TD

#endif //ENGINE_DATASTREAMS_H
