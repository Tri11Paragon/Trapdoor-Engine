//
// Created by laptop on 2022-02-25.
//

#ifndef TRAPDOOR_ARRAYLIST_H
#define TRAPDOOR_ARRAYLIST_H

template<typename T>
class ArrayList {
private:
    const int m_INITSIZE = 64;
    T* array = nullptr;
    int currentSize = 0;
    int currentIndex = 0;
public:
    ArrayList(): ArrayList(m_INITSIZE){}
    ArrayList(int initSize){
        array = new T[initSize];
        currentSize = initSize;
    }
    int add(T& t);
    int add(T t);
    T& get(int index);
    void remove(int index);
    void remove(T& t);
    int size();
    T& operator+(T& other);
    ~ArrayList(){
        delete[](array);
    }
};


#endif //TRAPDOOR_ARRAYLIST_H
