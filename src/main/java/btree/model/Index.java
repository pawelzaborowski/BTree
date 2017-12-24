package btree.model;

import btree.controller.BTree;
import java.nio.ByteBuffer;

import java.io.IOException;
import java.util.LinkedList;

public class Index extends File{


    public Index(BTree bTree, String fileName, int d) throws IOException {
        super(bTree, fileName, d);
    }

    public IndexPage createRootPage(){
        return this.createPage(null);
    }

    public IndexPage createPage(IndexPage parent) throws IOException {

        int pointer = (int) this.file.length();

        this.buffer = new byte[this.pageSize];
        ByteBuffer bb = ByteBuffer.wrap(this.buffer);

        bb.putInt(0);
        //bb.putInt(null_pointe)
        this.file.seek(pointer);
        this.writeToFile(this.buffer, true);

        return new IndexPage(this.bTree, parent, pointer, 0, 0);
    }

    public void savePage(IndexPage page){
        this.buffer = new byte[this.pageSize];
        ByteBuffer bb = ByteBuffer.wrap(this.buffer);

        LinkedList<Integer> pointers = (LinkedList <Integer>) page.getPointers().clone();
        LinkedList<Key> keys = (LinkedList <Key>) page.getPointers().clone();

        bb.putInt(page.getM());
        bb.putInt(pointers.removeFirst());

        for(int i = 0; i < page.getM(); i++){
            Key key = keys.removeFirst();
            bb.putInt(key.getValue());
            bb.putInt(key.getRecPonint());
            bb.putInt(pointers.removeFirst());
        }

        this.file.seek(page.getPagePointer());
        this.writeToFile(this.buffer, true);
    }

    public IndexPage getRootPage(){
        return this.getPage(0, null);
    }

    public IndexPage getPage(int pointer, IndexPage parent)
    {

        if(pointer >= this.file.length()) {  //pointer == statistic.null
            return null;
        }
        this.file.seek(pointer);
        this.readFromFile(this.buffer, true);
        ByteBuffer bb = ByteBuffer.wrap(this.buffer);

        int m = bb.getInt();
        int p = bb.getInt();    //pointer
        IndexPage page = new IndexPage(this.bTree, parent, pointer, m, p);

        for(int i = 0; i < m; i++){
            Key key = new Key(bb.getInt(), bb.getInt());
            page.getKeys().addLast(key);

            p = bb.getInt();
            page.getPointers().addLast(p);
        }

        return page;
    }




    


}
