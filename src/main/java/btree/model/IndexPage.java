package btree.model;

import btree.controller.BTree;

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


    public int findKey(int key){
        int index = 0;

        //for (Key key : this.keys){

        //}
        return 0;
    }

    public int getIndex(int key){
        return 0;
    }

    public int insertKey(Key key, Integer p){
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
