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

        bool usingCompression = true;
        unsigned int bufferSize;

        unsigned char* buffer;
        unsigned char* compressBuffer;
        // position that we can currently write to.
        unsigned long bufferPos = 0;
        unsigned long compressedBufferStartSize;
        unsigned long compressedBufferSize;
        bool checkForFlush(int requested = 0){
            // padded room, buffer is large enough that 1 byte doesn't make much of a difference
            if (bufferPos + requested >= bufferSize){
                flush();
                return true;
            } else
                return false;
        }
    public:
        explicit DataOutputStream(const std::string& file, const bool compressed = true, const unsigned long bufferSize = 256*1024){
            this->bufferSize = bufferSize;
            this->usingCompression = compressed;
            this->buffer = new unsigned char[bufferSize];
            output = std::ofstream(file, std::ios::binary);
            if (compressed) {
                compressedBufferStartSize = (unsigned long) (bufferSize * 1.1 + 12);
                compressedBufferSize = compressedBufferStartSize;
                compressBuffer = new unsigned char[compressedBufferStartSize];
            }
        }
        inline void writeByte(unsigned char byte){
            checkForFlush(1);
            buffer[bufferPos++] = byte;
        }
        inline void writeShort(unsigned short s){
            unsigned char data[2];
            TD::DataConv::getShort(s, (char*)&data[0]);
            checkForFlush(2);
            buffer[bufferPos++] = data[0];
            buffer[bufferPos++] = data[1];
        }
        inline void writeInt(unsigned int i){
            unsigned char data[4];
            TD::DataConv::getInt(i, (char*)&data[0]);
            checkForFlush(4);
            buffer[bufferPos++] = data[0];
            buffer[bufferPos++] = data[1];
            buffer[bufferPos++] = data[2];
            buffer[bufferPos++] = data[3];
        }
        void flush(){
            if (bufferPos == 0)
                return;
            if (usingCompression){
                int z_result = compress(compressBuffer, &compressedBufferSize, buffer, bufferPos-1);
                switch (z_result){
                    case Z_OK: {
                        break;
                    }
                    case Z_MEM_ERROR: {
                        flog << "ZLib Compress out of memory!";
                        exit(-1);
                        break;
                    }
                    case Z_BUF_ERROR: {
                        flog << "Zlib output buffer wasn't large enough!";
                        exit(-1);
                        break;
                    }
                }
                tlog << "New Compressed Size: " << compressedBufferSize << " Old size: " << bufferPos;
                output.write((char *)&compressBuffer[0], (long)compressedBufferSize);
                compressedBufferSize = compressedBufferStartSize;
            } else {
                output.write((char *)&buffer[0], (long) bufferPos);
            }
            bufferPos = 0;
        }
        ~DataOutputStream(){
            flush();
            delete[] (buffer);
            delete[] (compressBuffer);
        }
    };

} // TD

#endif //ENGINE_DATASTREAMS_H
