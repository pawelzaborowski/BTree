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
public class IndexPage {

    private long address;
    private int bsize;
    private int icount;
    private boolean dirty;
    public Index i[];
    public long p[];

    public IndexPage(int size) {
        bsize = size;
        icount = 0;
        i = new Index[bsize];
        p = new long[bsize + 1];
        for (int k = 0; k < bsize + 1; k++) {
            p[k] = -1;
        }
        for (int k = 0; k < bsize; k++) {
            i[k] = new Index(-1, -1);
        }
        dirty = !dirty;
        address = -1;
    }

    public long getAddress() {
        return address;
    }

    public int getIcount() {
        return icount;
    }

    public void setIcount(int newIcount) {
        icount = newIcount;
    }

    public void create(long address) {
        this.address = address;
        icount = 0;
    }

    public void setDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isAvailable() {
        return icount <= 0;
    }

    public void setAvailable(long nextAvailable) {
        icount = -1;
        p[0] = nextAvailable;
    }

    public long nextAvailablePage() {
        return p[0];
    }

    public void load(RandomAccessFile indexFile, long address) {
        try {
            dirty = false;

            indexFile.seek(address);
            int newIcount = indexFile.readInt();
            icount = newIcount;
            for (int j = 0; j < bsize; j++) {
                Index index = new Index(indexFile.readLong(), indexFile.readLong());
                i[j] = index;
            }
            for (int j = 0; j < bsize + 1; j++) {
                p[j] = indexFile.readLong();
            }
        } catch (IOException ex) {

        }
        this.address = address;
    }

    public void store(RandomAccessFile indexFile) {
        try {
            dirty = false;
            indexFile.seek(address);
            indexFile.writeInt(icount);
            for (int j = 0; j < bsize; j++) {
                indexFile.writeLong(i[j].getKey());
                indexFile.writeLong(i[j].getAddress());
            }
            for (int j = 0; j < bsize + 1; j++) {
                indexFile.writeLong(p[j]);
            }
        } catch (IOException ex) {

        }
    }
}

