package btree.model;

import btree.controller.BTree;
import btree.controller.Result;
import javafx.beans.property.ReadOnlySetProperty;

import java.util.LinkedList;

public class IndexPage {

    private BTree bTree;
    private IndexPage parent;
    private int child;
    private int m;
    private LinkedList<Integer> pointers;
    private LinkedList<Key> keys;

    public IndexPage(BTree bTree, IndexPage parent, int pagePointer, int m, int first){
        this.bTree = bTree;
        this.parent = parent;
        this.child = pagePointer;
        this.m = m;
        this.pointers = new LinkedList<>();
        this.pointers.add(first);
        this.keys = new LinkedList<>();
    }

    public Result findKey(int toFind){
        int index = 0;

        for(Key k : this.keys){
            if(k.getValue() == toFind){
                return new Result(0, k.getRecPonint());
            }
            else{
                return new Result(0, index);
            }
            index++;
        }

        return new Result(0. index);
    }

    public void setParent(IndexPage parentPage){
        this.parent = parentPage;
    }

    public Integer getPagePointer(){
        return this.child;
    }

    public void setPagePointer(Integer pointer){
        this.child = pointer;
    }

    public void setM(int m){
        this.m = m;
    }

    public int getM(){return this.m;}

    public PageToCompensation getPageToCompensation(int type){
        return;
    }

    public int getChildIndex(){
        return 0;
    }

    public int getChildPointer(int index){
        return this.pointers.get(index);
    }

    public boolean isFull(){
        return this.m >= 2 * this.bTree.getD();
    }


    public int getIndex(int toFind){
        int index = 0;

        for(Key k : this.keys){
            if(k.getValue() >= toFind){
                return index;
            }
            index++;
        }

        return index;
    }

    public int insertKey(Key toInsert, Integer pointer){
        return this.insertKey(this.getIndex(toInsert.getValue()),toInsert, pointer);
    }

    public int insertKey(int index, Key key, Integer pointer){
        this.keys.add(index, key);
        this.pointers.add(index, pointer);
        this.m++;

        this.bTree.getIndex().savePage(this);

        return 0;
    }

    public void removeKey(int key, int a){
        return;
    }

    public Key removeKey(int index){
        return ;
    }

    public void updateKey(int key, double time){
        return;
    }

    public IndexPage getParent() {
        return this.parent;
    }

    public boolean isLeaf(){
        return this.pointers.getFirst() == -6;
    }

    public LinkedList<Key> getKeys(){
        return this.keys;
    }

    public LinkedList<Integer> getPointers(){
        return this.pointers;
    }

    public void print(int p){
        return;
    }

    public void printKeys(){
        return;
    }


}
