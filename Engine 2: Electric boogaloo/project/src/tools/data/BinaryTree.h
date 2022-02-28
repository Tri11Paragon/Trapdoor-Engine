//
// Created by laptop on 2022-02-28.
//

#ifndef TRAPDOOR_BINARYTREE_H
#define TRAPDOOR_BINARYTREE_H

template<typename T>
class BinaryTree{
public:
    struct Node {
        T& data;
        int v;
        Node* left;
        Node* right;
        Node(){
            v = 0;
            data = 0;
            left = nullptr;
            right = nullptr;
        }
    };
private:
    Node* rootNode;
    void m_postOrderTraverseDelete(Node* node){
        if (node == nullptr)
            return;
        else {
            m_postOrderTraverseDelete(node->left);
            m_postOrderTraverseDelete(node->right);
            deleteNode(node);
        }
    }
    void m_postOrderTraverse(Node* node, void (*func)(Node*)){
        if (node == nullptr)
            return;
        else{
            m_postOrderTraverse(node->left, func);
            m_postOrderTraverse(node->right, func);
            func(node);
        }
    }
    void m_preOrderTraverse(Node* node, void (*func)(Node*)){
        if (node == nullptr)
            return;
        else{
            func(node);
            m_preOrderTraverse(node->left, func);
            m_preOrderTraverse(node->right, func);
        }
    }
    void m_inOrderTraverse(Node* node, void (*func)(Node*)){
        if (node == nullptr)
            return;
        else{
            m_inOrderTraverse(node->left, func);
            func(node);
            m_inOrderTraverse(node->right, func);
        }
    }
    void m_insert(Node*& root, Node* newNode){
        if (root == nullptr) {
            root = newNode;
            return;
        }
        Node* current = root;
        Node* parent = root;
        while(true){
            parent = current;
            if (newNode->v < current->v){ // Left
                current = current->left;
                if (current == nullptr){
                    parent->left = newNode;
                    return;
                }
            } else { // Right
                current = current->right;
                if (current == nullptr){
                    parent->right = newNode;
                    return;
                }
            }
        }
    }
    void deleteNode(Node* node){
        delete node;
    }
public:
    BinaryTree(){
        rootNode = nullptr;
    }
    void inOrderTraverse(void (*func)(Node*)){
        m_inOrderTraverse(rootNode, func);
    }
    void preOrderTraverse(void (*func)(Node*)){
        m_preOrderTraverse(rootNode, func);
    }
    void postOrderTraverse(void (*func)(Node*)){
        m_postOrderTraverse(rootNode, func);
    }
    void insert(Node* newNode){
        m_insert(rootNode, newNode);
    }
    void insert(const T& data, int value){
        Node* node = new Node;
        node->right = nullptr;
        node->left = nullptr;
        node->data = data;
        node->v = value;
        insert(node);
    }
    bool remove(int key){
        Node* curr = rootNode;
        Node* parent = rootNode;
        while (curr->v != key){
            parent = curr;
            if (key < curr->v)
                curr = curr->left;
            else
                curr = curr->right;
            // doesn't exist
            if (curr == nullptr)
                return false;
        }
        if (parent->left == curr)
            parent->left = nullptr;
        else
            parent->right = nullptr;
        if (curr->left != nullptr){
            m_insert(parent, curr->left);
        }
        if (curr->right != nullptr){
            m_insert(parent, curr->right);
        }
        deleteNode(curr);
        return true;
    }
    T& find(int key){
        Node* curr = rootNode;
        while (curr->v != key){
            if (key < curr->v)
                curr = curr->left;
            else
                curr = curr->right;
            if (curr == nullptr)
                return nullptr;
        }
        return curr->data;
    }
    ~BinaryTree(){
        m_postOrderTraverseDelete(rootNode);
    }
};

#endif //TRAPDOOR_BINARYTREE_H
