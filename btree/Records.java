package btree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class Records extends File {
    
    Records(String fileName, int d, BTree btree) throws FileNotFoundException, IOException {
        super(fileName, d, btree);
    }
    
    public Record getRecord(int pointer) throws IOException {
        
        int page = pointer / this.pageSize;
        page *= this.pageSize;
        int offset = pointer % this.pageSize;
        
        this.file.seek(page);
        this.read(this.buffor, true);
        ByteBuffer wrapper = ByteBuffer.wrap(this.buffor);
        
        double time = wrapper.getDouble(offset + 4);
        
        return new Record(time);
        
    }
    
    public int insert(Record record) throws IOException {
        
        System.out.print(record);
        
        int freeSpacePointer = this.getFreeSpace();
        int page = freeSpacePointer / this.pageSize;
        page *= this.pageSize;
        int offset = freeSpacePointer % this.pageSize;
        
        this.file.seek(page);
        if(offset != 0 || page < this.file.length())
            this.read(this.buffor, true);
        else
            this.buffor = new byte[this.pageSize];
        
        ByteBuffer wrapper = ByteBuffer.wrap(this.buffor);
          wrapper.putDouble(offset + 4, record.getTime());
        
        this.file.seek(page);
        this.write(this.buffor, true);
        
        return freeSpacePointer;
        
    }
    
    private int getFreeSpace() throws IOException {
        
        int pointer = this.recovery.getFreeSpace();
        if(pointer != Statics.NULL_POINTER)
            return pointer;
        
        if(this.file.length() > 0) {
            
            int page = (int) (this.file.length() - this.pageSize);
            int offset = 0;
            this.file.seek(page);
            this.read(this.buffor, false);
            ByteBuffer wrapper = ByteBuffer.wrap(this.buffor);
            
            while(offset + 12 <= this.pageSize) { //gdzie się kończy
                if(wrapper.getFloat(offset) == 0 && 
                        wrapper.getDouble(offset + 4) == 0)
                    return page + offset;
                
                offset += 12;
            }
        }
        
        //albo length == 0 lub gdy trzeba napocząć nową stronę
        int page = (int) this.file.length();
        return page;
        
    }
    
    public void update(int pointer, Record record) throws IOException {
        System.out.print(record);
        
        int page = pointer / this.pageSize;
        page *= this.pageSize;
        int offset = pointer % this.pageSize;
        
        this.file.seek(page);
        this.read(this.buffor, true);
        ByteBuffer wrapper = ByteBuffer.wrap(this.buffor);        

        wrapper.putDouble(offset + 4, record.getTime());
        
        this.file.seek(page);
        this.write(this.buffor, true);
    }
    
    public int print() throws IOException {
        
        int page = 0, offset, counter = 1;
        double time;
        
        while(page < this.file.length()) {            
            this.file.seek(page);
            this.read(this.buffor, true);
            ByteBuffer wrapper = ByteBuffer.wrap(this.buffor);
            offset = 0;
            
            while(offset + 12 <= this.pageSize) { //gdzie się kończy    
                time = wrapper.getDouble(offset + 4);
                
                if(time == 0)
                    return 1; //koniec pliku
                
                System.out.println(counter + ". " + new Record(time));
                counter++;
                offset += 12;
            }
            page += this.pageSize;
        }
        return 1;
    }
}

class MyRandom {
    
    private final Random random;
    
    public MyRandom() {
        
        this.random = new Random();
        
    }
    
    public double getDouble() {
        
        char value = (char) this.random.nextInt();
        value = (char) (value % (32751 - 16 + 1));
        value += 16;
        
        byte[] array = new byte[8];
        this.random.nextBytes(array);        
        
        ByteBuffer.wrap(array).putChar(0, value);
        
        return ByteBuffer.wrap(array).getDouble(0);
        
    }
    
    public float getFloat() {
        
        char value = (char) this.random.nextInt();
        value = (char) (value % (32639 - 128 + 1));
        value += 128;
        
        byte[] array = new byte[4];
        this.random.nextBytes(array);        
        
        ByteBuffer.wrap(array).putChar(0, value);
        
        return ByteBuffer.wrap(array).getFloat(0);
        
    }
    
}