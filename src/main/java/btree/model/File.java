package btree.model;

import btree.controller.BTree;

import java.io.IOException;
import java.io.RandomAccessFile;

public class File {

    protected RandomAccessFile file;
    protected int pageSize;
    protected BTree bTree;
    protected byte[] buffer;
    private int counter;

    public File(BTree bTree, String fileName, int d) throws IOException {

        this.file = new RandomAccessFile(fileName, "rw");
        this.file.setLength(0);
        this.bTree = bTree;

        int sizeOfKeys = 2 * d * (4 + 4); // chyba tylko jedno 4
        int sizeOfPointers = (2 * d +1 ) * 4;
        int sizeOfM = 4;

        this.pageSize = sizeOfKeys + sizeOfPointers + sizeOfM;
        this.buffer = new byte[this.pageSize];

    }

    protected void clearCounter(){
        this.counter = 0;
    }

    protected int getCouner(){
        return this.counter;
    }

    protected int readFromFile(byte[] bytes, boolean ifSaved) throws IOException {
        if(ifSaved)
            this.counter++;
        return this.file.read(bytes);
    }

    protected void writeToFile(byte[] bytes, boolean ifSaved) throws IOException{
        if(ifSaved)
            this.counter++;
        this.file.write(bytes);
    }
}
