/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

/**
 *
 * @author Pawel
 */

public class Btree extends Delete {

    public void update(long key, Record record) {
        setLpage(0);
        long address = searchKey(key, true);
        if (address == -1) {
            System.out.println("Nie znaleziono klucza!");
        } else {
            setRecordAtAddress(record, address, true);
            System.out.println("Uaktualniono rekord o kluczu: " + key);
        }
    }

    public void search(long key) {
        setLpage(0);
        long address = searchKey(key, false);
        if (address == -1) {
            System.out.println("Nie znaleziono klucza!");
        } else {
            Record t = getRecordFromAddress(address, false);
            System.out.println("Klucz: "+key + " time: " + String.format("%.2f", t.getTime()));
        }
    }

    public long searchKey(long key, boolean dirty) {
        IndexPage page = getTracePage(getRoot());
        if (page.getIcount() <= 0) {
            return -1;
        }

        int i;
        while (true) {
            i = locateKey(page, key);
            if (page.i[i].getKey() == key) { // znalezlismy klucz
                if (dirty) {
                     page.setDirty();
                }
                return page.i[i].getAddress();
            } else if (key < page.i[i].getKey()) { // klucz powinien byc po lewej
                if (page.p[0] != -1) { //nie jest lisciem
                    page = getTracePage(page.p[i]);
                } else {
                    return -1;
                }
            } else // klucz powinien byc po prawej
            {
                if (page.p[0] != -1) { //nie jest lisciem
                    page = getTracePage(page.p[i + 1]);
                } else {
                    return -1;
                }
            }
        }
    }

    public void readEntireFile() {
        setLpage(0);
        if (getH() == 0) {
            return;
        }
        IndexPage page = getTracePage(getRoot());
        Trace trace[] = new Trace[getH()];
        for (int i = 0; i < getH(); i++) {
            trace[i] = new Trace(-1, -1);
        }

        trace[0].position = -1;
        int level = 0;

        Record t;
        while (true) {
            if (level == getH() - 1) { // lisc
                for (int i = 0; i < page.getIcount(); i++) {
                    t = getRecordFromAddress(page.i[i].getAddress(), false);
                    System.out.println("K: " + page.i[i].getKey() + " : time: " + String.format("%.2f", t.getTime()));
                }
                if (level == 0) { //root
                    break;
                } else {// poziom wyzej
                    page = getTracePage(trace[level - 1].page);
                    level--;
                }

            } else // nie lisc
                if (trace[level].position == -1) {
                    for (int i = level + 1; i < getH(); i++) {
                        trace[i].position = -1;
                    }
                    trace[level].page = page.getAddress();
                    trace[level].position = 0;
                    page = getTracePage(page.p[0]);
                    level++;
                } else if (trace[level].position < page.getIcount()) {
                    t = getRecordFromAddress(page.i[trace[level].position].getAddress(), false);
                    for (int k = getH() - 1; k > level; k--) {
                        System.out.print(String.format("%-3s", "--"));
                    }
                    System.out.println("K: " + page.i[trace[level].position].getKey() + " : time: " + String.format("%.2f", t.getTime()));

                    for (int i = level + 1; i < getH(); i++) {
                        trace[i].position = -1;
                    }
                    trace[level].position++;
                    page = getTracePage(page.p[trace[level].position]);
                    level++;
                } else if (level == 0) { // root, koniec wyswietlania
                    break;
                } else { // krok w gore
                    page = getTracePage(trace[level - 1].page);
                    level--;
                }
        }
    }
}
