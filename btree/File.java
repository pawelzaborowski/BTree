package btree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public abstract class File {
    
    protected RandomAccessFile file;
    protected RandomAccessFile recoveryFile;
    protected btree.FileRecovery recovery;
    protected int pageSize;
    protected btree.BTree btree;
    protected byte[] buffor;
    
    protected int counter;
    
    public File(String fileName, int d, BTree btree) throws FileNotFoundException, IOException {
        
        this.file = new RandomAccessFile(fileName, "rw");
        this.file.setLength(0);
            
        this.recoveryFile = new RandomAccessFile(fileName + "_recovery", "rw");
        this.recoveryFile.setLength(0);
        
        this.btree = btree;
        //obliczanie wielkości strony
        int sizeOfKeys = 2 * d * (4 + 4);
        int sizeOfPointers = (2 * d + 1) * 4;
        int sizeOfM = 4;
        this.pageSize = sizeOfKeys + sizeOfPointers + sizeOfM;
        this.buffor = new byte[this.pageSize];
        
       // this.recovery = new FileRecovery(this.recoveryFile, pageSize);
    }
    
    protected void addFreeSpace(int pointer) throws IOException {
        this.recovery.addFreeSpace(pointer);
    }
    public void remove(int pointer) throws IOException {
       this.addFreeSpace(pointer);
    }
    
    public void clearCounter() {
        this.counter = 0;
    }
    
    public int getCounter() {
        return this.counter;
    }
    
    protected int read(byte[] b, boolean save) throws IOException {
        if(save)
            this.counter++;
        return this.file.read(b);
    }
    
    protected void write(byte[] b, boolean save) throws IOException {
        if(save)
            this.counter++;
        this.file.write(b);
    }
    
}
