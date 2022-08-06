//
// Created by brett on 22/07/22.
//

#ifndef ENGINE_STD_H
#define ENGINE_STD_H

#include <memory>
#include <vector>
#include <map>
#include <string>
#include <queue>
#include <ios>
#include <fstream>
#include <iostream>
#include <memory>

/**
 *
    The flat hash maps will move the keys and values in memory.
    So if you keep a pointer to something inside a flat hash map, this pointer may become invalid when the map is mutated.
    The node hash maps don't, and should be used instead if this is a problem.

    The flat hash maps will use less memory, and usually be faster than the node hash maps, so use them if you can.
    the exception is when the values inserted in the hash map are large (say more than 100 bytes [needs testing]) and costly to move.

    The parallel hash maps are preferred when you have a few hash maps that will store a very large number of values.
    The non-parallel hash maps are preferred if you have a large number of hash maps, each storing a relatively small number of values.

    The benefits of the parallel hash maps are:
    a. reduced peak memory usage (when resizing), and
    b. multithreading support (and inherent internal parallelism)

 */
namespace TD {

    /**
     * A simple pointer container which returns nullptr if the stored ptr has been deleted
     * Delete with the .free() command
     * @tparam T
     */
    template<typename T>
    class dPtr {
    private:
        T* data;
        std::shared_ptr<bool> avail;
    public:
        dPtr(const dPtr& ptr){
            this->data = ptr.data;
            this->avail = ptr.avail;
        }
        explicit dPtr(T* ptr){
            data = ptr;
            avail = std::make_shared<bool>(true);
        }
        /**
         * casts the internal pointer to a pointer of type C
         * will return nullptr if pointer was deleted
         */
        template<typename C>
        C* cast(){
            if (!*avail)
                return nullptr;
            return static_cast<C*>(data);
        }
        T* get(){
            if (!*avail)
                return nullptr;
            return data;
        }
        bool isValid(){
            return *avail;
        }
        void free(){
            delete(data);
            *avail = false;
            std::cout << *avail;
        }
        T operator *(){
            if (!*avail)
                return nullptr;
            return *data;
        }
        T* operator ->(){
            if (!avail)
                return nullptr;
            return data;
        }
    };
    //static inline void removeFromVector(std::vector<T> vector, T objectToRemove){
    //    vector.erase(std::remove(vector.begin(), vector.end(), objectToRemove), vector.end());
    //}
    // windowResizeCallbacks.erase(std::remove(windowResizeCallbacks.begin(), windowResizeCallbacks.end(), this), windowResizeCallbacks.end());
}

#endif //ENGINE_STD_H
