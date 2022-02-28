//
// Created by laptop on 2022-02-25.
//

#ifndef TRAPDOOR_ARRAYLIST_H
#define TRAPDOOR_ARRAYLIST_H

template<typename T>
class ArrayList {
private:
    T* array = nullptr;
    int currentSize = 0;
    int currentIndex = 0;
    void expand(){
        T* newArray = new T[currentSize*2];
        for (int i = 0; i < currentSize; i++)
            newArray[i] = array[i];
        delete[](array);
        array = newArray;
        currentSize = currentSize * 2;
    }
    void shuffle(int start){
        for (int i = start; i < size(); i++){
            array[i-1] = array[i];
        }
    }
public:
    // default arrays are 64 in length
    ArrayList(): ArrayList(64) {}
    // or create an array of starting length
    ArrayList(int initSize){
        array = new T[initSize];
        currentSize = initSize;
    }
    int add(const T& t){
        if (currentIndex > currentSize)
            expand();
        array[currentIndex] = t;
        return currentIndex++;
    }
    T& get(int index){
        return array[index];
    }
    void remove(int index){
        if (index + 1 >= size())
            return;
        shuffle(index + 1);
    }
    // removes the first reference to t
    void remove(T& t){
        for (int i = 0; i < size(); i++){
            if (array[i] == t){
                if (i+1 >= size())
                    return;
                shuffle(i+1);
                return;
            }
        }
    }
    int size(){
        return currentIndex;
    }
    T& operator[](int i){
        return get(i);
    }
    ArrayList<T> operator+(ArrayList<T>& other){
        ArrayList<T> newArr(other.size() + size());
        for (int i = 0; i < size(); i++){
            newArr.add(get(i));
        }
        for (int i = 0; i < other.size(); i++)
            newArr.add(other.get(i));
        return newArr;
    }
    T* toArrayCopy(){
        T ret[size()];
        for (int i = 0; i < size(); i++)
            ret[i] = get(i);
        return ret;
    }
    T* toArray(){
        return array;
    }
    ~ArrayList(){
        delete[](array);
    }
};


#endif //TRAPDOOR_ARRAYLIST_H
