/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pawel
 */
public abstract class BtreeBase {

    //wysokosc drzewa
    private int h;;

    //szerokosc drzewa
    private int d;
    //bufor rekordow pliku glownego
    private int r;
    //adres roota
    private long root;

    private RandomAccessFile mainFile;
    private RandomAccessFile indexFile;

    //strona rekordów w pamięci
    private RecordPage recordPage;
    private long recordBound = 0;
    private long recordFileSize = 0;
    private long indexFileSize = 0;

    //zapelnianie zwolnionego miejsca
    long recordAvailableChain = 0;
    long indexAvailableChain = 0;

    //bufory tymczasowy i sciezki od korzenia do klucza docelowego
    public IndexPage tpages[] = new IndexPage[3];
    public ArrayList<IndexPage> lpages = new ArrayList<>();
    private int tpage;
    private int lpage;

    //rozmiar jednego rekordu w bajtach
    private final int recordSize = Double.SIZE / 8;
    
    //rozmiar naglowka w bajtach
    private final int headerSize = 3 * Integer.SIZE / 8 + 6 * Long.SIZE / 8;

    //liczby zapisow i odczytow z pliku glownego i indeksowego
    private int mainWrites, mainReads = 0;
    private int indexWrites, indexReads = 0;

    public abstract void delete(long key);

    public abstract void insert(long key, Record record);

    public abstract void update(long key, Record record);

    public abstract void search(long key);

    public abstract void readEntireFile();

    public int getH() {
        return h;
    }

    public void setH(int newH) {
        h = newH;
    }

    public int getR() {
        return r;
    }

    public int getD() {
        return d;
    }

    public long getRecordBound() {
        return recordBound;
    }

    public long getRecordSize() {
        return recordSize;
    }

    public void setRecordBound(long newBound) {
        recordBound = newBound;
    }

    public long getRoot() {
        return root;
    }

    public void setRoot(long newRoot) {
        root = newRoot;
    }

    public long getRecordFileSize() {
        return recordFileSize;
    }

    public void setRecordFileSize(long newRecordFileSize) {
        recordFileSize = newRecordFileSize;
    }

    public RandomAccessFile getMainFile() {
        return mainFile;
    }

    public void setLpage(int newLpage) {
        lpage = newLpage;
    }

    private int indexPageSize(int size) {
        return Integer.SIZE / 8 + 2 * size * Long.SIZE / 8 + (size + 1) * Long.SIZE / 8;
    }

    public void open(String indexName, String mainName, int d, int r, int e) {
        this.d = d;
        this.r = r;
        if (e == 1) {
            try {
                new RandomAccessFile(mainName, "rw").setLength(0);
                new RandomAccessFile(indexName, "rw").setLength(0);
                mainFile = new RandomAccessFile(mainName, "rw");
                indexFile = new RandomAccessFile(indexName, "rw");
            } catch (FileNotFoundException ex) {
                
            } catch (IOException ex) {
                
            }
            createHeader();
            initPageManaging();
            recordPage = new RecordPage(r, -1);
            writeHeader();
        } else {
            try {
                mainFile = new RandomAccessFile(mainName, "rw");
                indexFile = new RandomAccessFile(indexName, "rw");
            } catch (FileNotFoundException ex) {
                
            }
            readHeader();
            initPageManaging();
            recordPage = new RecordPage(this.r, -1);
        }
    }

    private void initPageManaging() {
        for (tpage = 0; tpage < 3; tpage++) {
            tpages[tpage] = new IndexPage(2 * d);
        }
        tpage = 0;
        for (lpage = 0; lpage < h; lpage++) {
            lpages.add(new IndexPage(2 * d));
        }
        lpage = 0;
    }

    private void createHeader() {
        h = 0; 
        root = -1;
        recordAvailableChain = -1;
        recordFileSize = 0;
        recordBound = 0;
        indexAvailableChain = -1;
        indexFileSize = headerSize;
    }

    public void setRecordAtAddress(Record record, long address, boolean dirty) {
        int recordPosition = (int) ((address % (r * recordSize)) / recordSize);
        long pageAddress = address - recordPosition * recordSize;
        getRecordPage(pageAddress, dirty).setRecordAtIndex(recordPosition, record);
    }

    public Record getRecordFromAddress(long address, boolean dirty) {
        int recordPosition = (int) ((address % (r * recordSize)) / recordSize);
        long pageAddress = address - recordPosition * recordSize;
        return getRecordPage(pageAddress, dirty).getRecordAtIndex(recordPosition); //2 z bufora
    }

    public RecordPage getRecordPage(long address, boolean dirty) {
        if (address < recordFileSize) {
            if (address != recordPage.getAddress()) {
                if (recordPage.getAddress() != -1 && recordPage.isDirty()) {
                    recordPage.store(mainFile);
                    mainWrites++;
                }
                recordPage.load(mainFile, address);
                mainReads++;
            }
        } else {
            if (recordPage.getAddress() != -1 && recordPage.isDirty()) {
                recordPage.store(mainFile);
                mainWrites++;
            }
            recordPage.create(address);
            recordFileSize += r * recordSize;
        }
        if (dirty) {
            recordPage.setDirty();
        }
        return recordPage;
    }

    public int locateKey(IndexPage page, long key) {
        int a = 0;
        int b = page.getIcount() - 1;

        if (b == 0) {
            return 0;
        }

        int i = 0;
        while (a <= b) {
            i = (a + b) / 2;
            if (page.i[i].getKey() == key) {
                break; // i to szukany element
            }

            if (page.i[i].getKey() < key) {
                a = i + 1;
            } else {
                b = i - 1;
            }
        }
        return i;
    }

    public IndexPage newPage() {
        if (indexAvailableChain == -1) {
            long address = indexFileSize;
            IndexPage page = getTempPage(address);

            indexFileSize += indexPageSize(2 * d);
            return page;
        } else {
            IndexPage page = getTempPage(indexAvailableChain);
            indexAvailableChain = page.nextAvailablePage();
            return page;
        }
    }

    public IndexPage getTempPage(long address) {
        // sprawdzamy czy jest juz w pamieci w temp
        for (int i = 0; i < 3; i++) {
            if (tpages[i].getAddress() == address) {
                tpage = (tpage + 1) % 3;
                return tpages[i];
            }
        }

        if (tpages[tpage].getAddress() != -1 && tpages[tpage].isDirty()) {
            tpages[tpage].store(indexFile);
            indexWrites++;

        }
        if (address < indexFileSize) {
            tpages[tpage].load(indexFile, address);
            indexReads++;
        } else {
            tpages[tpage].create(address);
        }
        int ret = tpage;
        tpage = (tpage + 1) % 3;
        return tpages[ret];
    }

    public IndexPage getTracePage(long address) {
        // sciezka od roota
        for (int i = (lpage == h ? h - 1 : lpage); i >= 0; i--) {
            if (lpages.get(i).getAddress() == address) {
                lpage = i + 1;
                return lpages.get(i);
            }
        }

        // sprawdzamy, czy nie ma jej w stronach tymczasowych
        for (int i = 0; i < 3; i++) {
            if (tpages[i].getAddress() == address) {

                IndexPage tmp = lpages.get(lpage);
                lpages.set(lpage, tpages[i]);
                tpages[i] = tmp;
                return lpages.get(lpage++);
            }
        }

        // trzeba pobrac strone z dysku
        if (lpages.get(lpage).getAddress() != -1 && lpages.get(lpage).isDirty()) {
            lpages.get(lpage).store(indexFile);
            indexWrites++;
        }
        if (address < indexFileSize) {
            lpages.get(lpage).load(indexFile, address);
            indexReads++;
        } else {
            lpages.get(tpage).create(address);
        }
        return lpages.get(lpage++);
    }

    public void grow() {
        h++;
        lpages.add(new IndexPage(2 * d));
    }

    public void shrink() {
        setH(h - 1);
        if (lpages.get(h).getAddress() != -1 && lpages.get(h).isDirty()) {
            lpages.get(h).store(indexFile);
            indexWrites++;
        }
        lpages.remove(getH());
    }

    private void writeHeader() {
        try {
            indexFile.seek(0);
            indexFile.writeInt(d);
            indexFile.writeInt(r);
            indexFile.writeInt(h);
            indexFile.writeLong(recordAvailableChain);
            indexFile.writeLong(recordFileSize);
            indexFile.writeLong(recordBound);
            indexFile.writeLong(indexAvailableChain);
            indexFile.writeLong(indexFileSize);
            indexFile.writeLong(root);
        } catch (IOException ex) {

        }
    }

    private void readHeader() {
        try {
            indexFile.seek(0);
            d = indexFile.readInt();
            r = indexFile.readInt();
            h = indexFile.readInt();
            recordAvailableChain = indexFile.readLong();
            recordFileSize = indexFile.readLong();
            recordBound = indexFile.readLong();
            indexAvailableChain = indexFile.readLong();
            indexFileSize = indexFile.readLong();
            root = indexFile.readLong();
        } catch (IOException ex) {
            
        }

    }

    public void close() {
        flush();
        try {
            indexFile.close();
            mainFile.close();
        } catch (IOException ex) {
           
        }
    }

    public void flush() {
        lpage = 0;
        for (int i = 0; i < 3; i++) {
            if (tpages[i].getAddress() != -1 && tpages[i].isDirty()) {
                tpages[i].store(indexFile);
                indexWrites++;
            }
        }
        for (int i = 0; i < getH(); i++) {
            if (lpages.get(i).getAddress() != -1 && lpages.get(i).isDirty()) {
                lpages.get(i).store(indexFile);
                indexWrites++;
            }
        }
        writeHeader();
        if (recordPage.isDirty()) {
            recordPage.store(mainFile);
            mainWrites++;
        }
    }
    
    public void clearCounters(){
        indexWrites = 0;
        mainWrites = 0;
        indexReads = 0;
        mainReads = 0;
    }

    public void printReadsAndWrites() {
        System.out.println("Odczyty pliku indeksowego: " + indexReads);
        System.out.println("Zapisy pliku indeksowego: " + indexWrites);
        System.out.println("Odczyty pliku glownego: " + mainReads);
        System.out.println("Zapisy pliku glownego: " + mainWrites);
        System.out.println("Wysokosc: " + getH());
    }
}
