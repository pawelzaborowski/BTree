package btree.controller;

import btree.Records;
import btree.model.Index;
import btree.model.IndexPage;
import btree.model.ReplacingKey;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;

public class BTree {

    private Index index;
    private Records records;
    private int d;
    private LinkedList pagesList;
    private PrintStream printStream;

    BTree(String index, String records, int d, String fileName) throws FileNotFoundException {
        this.index = new Index(index, d, this);
        this.records = new Records(records, d, this);
        this.d = d;
        this.pagesList = new LinkedList();
        this.printStream = new PrintStream(new File(fileName));
    }

    public FindResult find(IndexPage page, int key){

        if(page == null){
            return new FindResult(0);
        }
        else{
            this.pagesList.add(page);
            FindResult result = page.findKey(key);

            if(result.getType() == 0) {
                return result;
            }
            else {
                return this.find(this.index.getPage(page.getChildPointer(result.getResult()), page), key);
            }

        }


    }

    public int insert(int key, double time){
        return 0;
    }

    private void compensation(IndexPage leftPage, IndexPage rigthPage){
        return;
    }

    private void split(IndexPage rightPage){
        return;
    }

    public void printBTree(){
        return;
    }

    public int read(int key){
        return 0;
    }

    public int update(int key, double time){
        return 0;
    }

    public int remove(int key){
        return 0;
    }

    private ReplacingKey getKeyToReplace(int key){
        return ;
    }

    private IndexPage getLeafFromSubTree(IndexPage page){
        return ;
    }

    @Contract(pure = true)
    private int reorganise(IndexPage page){
        return 0;
    }

    public int getD(){
        return this.d;
    }




}
