
#include <iostream>
#include "config/version.h"
#include "display/DisplayManager.h"
#include "tools/data/ArrayList.h"
#include "tools/data/BinaryTree.h"

int main(){

    std::cout << "Loading " << TRAPDOOR_NAME << " Engine v";
    printClean();

    ArrayList<int> arrayList1;
    ArrayList<int> arrayList2;
    for (int i = 0; i < 340; i++) {
        arrayList1.add(i);
        arrayList2.add(i*2 + 512);
    }

    ArrayList<int> arrayList3 = arrayList1 + arrayList2;

    for (int i = 0; i < arrayList3.size(); i++){
        std::cout << i << "L " << arrayList3[i] << std::endl;
    }

    BinaryTree<float> treer;
    for (int i = 0; i < arrayList3.size(); i++){
        treer.insert(arrayList3[i], i - arrayList3.size()/2);
    }

    treer.inOrderTraverse([](BinaryTree<float>::Node* n) -> void {
        std::cout << n->data << std::endl;
    });

    //createDisplay();

    //while (!isCloseRequested()){
        //updateDisplay();
    //}

    //closeDisplay();

}