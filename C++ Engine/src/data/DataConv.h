//
// Created by brett on 04/08/22.
//

#ifndef ENGINE_DATACONV_H
#define ENGINE_DATACONV_H

#include <codecvt>
#include <locale>

namespace TD {

    class DataConv {
    public:
        static int getInt(char* bytes){
            return int((unsigned char)(bytes[0]) << 24 | (unsigned char)(bytes[1]) << 16 | (unsigned char)(bytes[2]) << 8 | (unsigned char)(bytes[3]));
        }
        static short getShort(char* bytes){
            return short((unsigned char)(bytes[0]) << 8 | (unsigned char)(bytes[1]));
        }
        /*static long getLong(unsigned char* bytes){
            return long(bytes[0] << 56 | bytes[1] << 48 | bytes[2] << 40 | bytes[3] << 32 | bytes[4] << 24 | bytes[5] << 16 | bytes[6] << 8 | bytes[7]);
        }*/
        static unsigned int getUInt(char* bytes){
            return (unsigned int)((unsigned char)(bytes[0]) << 24 | (unsigned char)(bytes[1]) << 16 | (unsigned char)(bytes[2]) << 8 | (unsigned char)(bytes[3]));
        }
        /*static unsigned long getULong(unsigned char* bytes){
            return (unsigned long)(bytes[0] << 56 | bytes[1] << 48 | bytes[2] << 40 | bytes[3] << 32 | bytes[4] << 24 | bytes[5] << 16 | bytes[6] << 8 | bytes[7]);
        }*/
    };

}


#endif //ENGINE_DATACONV_H
