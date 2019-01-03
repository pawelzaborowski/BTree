/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

/**
 *
 * @author Pawel
 */
public class IndexInfo {

    private Index index;
    private long leftChild;
    private long rightChild;

    public IndexInfo() {
        index = null;
        leftChild = -1;
        rightChild = -1;
    }

    public IndexInfo(Index i, long l, long r) {
        index = i;
        leftChild = l;
        rightChild = r;
    }

    public long getRightChild() {
        return rightChild;
    }

    public long getLeftChild() {
        return leftChild;
    }

    public Index getIndex() {
        return index;
    }

    public void setRightChild(long newRightChild) {
        rightChild = newRightChild;
    }

    public void setLeftChild(long newLeftChild) {
        leftChild = newLeftChild;
    }

    public void setIndex(Index newIndex) {
        index = newIndex;
    }
}
