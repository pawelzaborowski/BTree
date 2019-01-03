/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pawel
 */
public class RecordPage {

    private boolean dirty = false; //todo dirty?
    private Record[] buffer;
    private int bufferSize;
    private long address = -1;

    boolean isDirty() {
        return dirty;
    }

    void setDirty() {
        dirty = true;
    }

    public long getAddress() {
        return address;
    }

    public void setRecordAtIndex(int index, Record record) {
        buffer[index] = record;
    }

    public Record getRecordAtIndex(int index) {
        return buffer[index];
    }

    public void create(long address) {
        this.address = address;
    }

    public RecordPage(int bufferSize) {
        dirty = false;
        this.bufferSize = bufferSize;
        buffer = new Record[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            buffer[i] = new Record(-1);
        }
    }

    public RecordPage(int bufferSize, long address) {
        dirty = false;
        this.bufferSize = bufferSize;
        buffer = new Record[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            buffer[i] = new Record(-1);
        }
        this.address = address;
    }
    
    public void load(RandomAccessFile input, long address) {
        dirty = false;
        try {
            input.seek(address);
            for (int i = 0; i < bufferSize; i++) {
                double t = input.readDouble();
                Record record = new Record(t);
                buffer[i] = record;
            }
        } catch (IOException ex) {

        }
        this.address = address;
    }

    public void store(RandomAccessFile output) {
        dirty = false;
        try {
            output.seek(address);
            for (int i = 0; i < bufferSize; i++) {
                output.writeDouble(buffer[i].getTime());
            }
        } catch (IOException ex) {
           
        }
    }
}

