//
// Created by brett on 04/08/22.
//

#ifndef ENGINE_DATACONV_H
#define ENGINE_DATACONV_H

#include <codecvt>
#include <locale>

namespace TD {

    class DataConv {
    private:
        typedef char byte ;
    public:
        static int getInt(char* bytes){
            return int((unsigned char)(bytes[0]) << 24 | (unsigned char)(bytes[1]) << 16 | (unsigned char)(bytes[2]) << 8 | (unsigned char)(bytes[3]));
        }
        static long getLong(char* bytes){
            return long(
                    (unsigned long)((unsigned char)(bytes[0])) << 56 | (unsigned long)((unsigned char)(bytes[1])) << 48
                    | (unsigned long)((unsigned char)(bytes[2])) << 40 | (unsigned long)((unsigned char)(bytes[3])) << 32
                    | (unsigned long)((unsigned char)(bytes[4])) << 24 | (unsigned long)((unsigned char)(bytes[5])) << 16
                    | (unsigned long)((unsigned char)(bytes[6])) << 8 | (unsigned long)((unsigned char)(bytes[7]))
                    );
        }
        static short getShort(char* bytes){
            return short((unsigned char)(bytes[0]) << 8 | (unsigned char)(bytes[1]));
        }
        static std::vector<char> getShort(short sh) {
            std::vector<char> sizeBytes;
            sizeBytes.push_back((char) (unsigned char) ((sh >> 8) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) (sh & 0xFF));
            return sizeBytes;
        }
        static std::vector<char> getInt(int sh) {
            std::vector<char> sizeBytes;
            sizeBytes.push_back((char) (unsigned char) ((sh >> 24) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) ((sh >> 16) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) ((sh >> 8) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) (sh & 0xFF));
            return sizeBytes;
        }
        static std::vector<char> getLong(long sh) {
            std::vector<char> sizeBytes;
            sizeBytes.push_back((char) (unsigned char) ((sh >> 56) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) ((sh >> 48) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) ((sh >> 40) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) ((sh >> 32) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) ((sh >> 24) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) ((sh >> 16) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) ((sh >> 8) & 0xFF));
            sizeBytes.push_back((char) (unsigned char) (sh & 0xFF));
            return sizeBytes;
        }
        static float getFloat(byte* data){
            union {
                float f;
                byte a[4];
            } floatToArray;

            floatToArray.a[0] = data[0];
            floatToArray.a[1] = data[1];
            floatToArray.a[2] = data[2];
            floatToArray.a[3] = data[3];

            return floatToArray.f;
        }
        static std::vector<byte> getFloat(float f){
            union {
                float f;
                byte a[4];
            } floatToArray;

            floatToArray.f = f;

            std::vector<byte> bytes;
            bytes.push_back(floatToArray.a[0]);
            bytes.push_back(floatToArray.a[1]);
            bytes.push_back(floatToArray.a[2]);
            bytes.push_back(floatToArray.a[3]);

            return bytes;
        }
        static double getDouble(byte* data){
            union {
                double f;
                byte a[8];
            } doubleToArray;

            doubleToArray.a[0] = data[0];
            doubleToArray.a[1] = data[1];
            doubleToArray.a[2] = data[2];
            doubleToArray.a[3] = data[3];
            doubleToArray.a[4] = data[4];
            doubleToArray.a[5] = data[5];
            doubleToArray.a[6] = data[6];
            doubleToArray.a[7] = data[7];

            return doubleToArray.f;
        }
        static std::vector<byte> getDouble(double f){
            union {
                double f;
                byte a[8];
            } doubleToArray;

            doubleToArray.f = f;

            std::vector<byte> bytes;
            bytes.push_back(doubleToArray.a[0]);
            bytes.push_back(doubleToArray.a[1]);
            bytes.push_back(doubleToArray.a[2]);
            bytes.push_back(doubleToArray.a[3]);
            bytes.push_back(doubleToArray.a[4]);
            bytes.push_back(doubleToArray.a[5]);
            bytes.push_back(doubleToArray.a[6]);
            bytes.push_back(doubleToArray.a[7]);

            return bytes;
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
