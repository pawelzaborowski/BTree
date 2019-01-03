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
public abstract class Insert extends BtreeBase {

    public void insert(long key, Record record) {
        setLpage(0);

        IndexPage page;
        Index index;
        if (getH() == 0) { // pierwszy rekord - root
            long address = insertRecord(record, key);
            index = new Index(key, address);
            page = newPage();
            page.setIcount(1);
            page.i[0] = index;
            page.p[0] = -1;
            page.p[1] = -1;
            grow();
            setRoot(page.getAddress());
            page.setDirty();
            return;
        }
        page = getTracePage(getRoot());
        Trace trace[] = new Trace[getH()];
        for (int j = 0; j < getH(); j++) {
            trace[j] = new Trace(-1, -1);
        }
        int t = 0;
        int i;
        while (true) {
            i = locateKey(page, key);//
            if (page.i[i].getKey() == key) {
                System.out.println("Rekord o podanym kluczu już istnieje!");
                break;
            } else if (key < page.i[i].getKey()) {
                trace[t].page = page.getAddress();
                trace[t].position = i;
                t++;
                if (page.p[i] == -1) {//lisc
                    long address = insertRecord(record, key);
                    index = new Index(key, address);
                    insertIndex(page, index, trace);
                    break;
                } else {//poziom nizej
                    page = getTracePage(page.p[i]);
                }
            } else {
                trace[t].page = page.getAddress();
                trace[t].position = i + 1;
                t++;
                if (page.p[i + 1] == -1) {//lisc
                    long address = insertRecord(record, key);
                    index = new Index(key, address);
                    insertIndex(page, index, trace);
                    break;
                } else {
                    page = getTracePage(page.p[i + 1]);
                }
            }
        }
    }

    private void insertIndex(IndexPage leaf, Index index, Trace trace[]) {
        int level = getH() - 1;
        IndexPage page = leaf;
        IndexInfo indexinfo = new IndexInfo(index, -1, -1);
        while (true) {
            //wstawienie w aktulaną strone
            if (insertInto(page, indexinfo, trace[level].position)) {
                return;
            }//kompensacja
            else if (insertCompensateWithLeft(page, indexinfo, level <= 0 ? -1 : trace[level - 1].page, level <= 0 ? -1 : trace[level - 1].position, trace[level].position)
                    || insertCompensateWithRight(page, indexinfo, level <= 0 ? -1 : trace[level - 1].page, level <= 0 ? -1 : trace[level - 1].position, trace[level].position)) {
                return;
            }//split
            else {
                indexinfo = split(page, indexinfo, trace[level].position);
                // split roota
                if (level == 0) {
                    page = newPage();
                    setRoot(page.getAddress());

                    page.i[0] = indexinfo.getIndex();
                    page.p[0] = indexinfo.getLeftChild();
                    page.p[1] = indexinfo.getRightChild();
                    page.setIcount(1);
                    page.setDirty();
                    grow();
                    return;
                } else {
                    level--;
                    page = getTracePage(trace[level].page);
                }
            }
        }

    }

    private boolean insertInto(IndexPage page, IndexInfo ii, int position) {
        if (page.getIcount() < 2 * getD()) {

            for (int i = page.getIcount(); i > position; i--) {
                page.i[i] = page.i[i - 1];
            }
            page.i[position] = ii.getIndex();
            for (int i = page.getIcount() + 1; i > position; i--) {
                page.p[i] = page.p[i - 1];
            }
            page.p[position] = ii.getLeftChild();
            page.p[position + 1] = ii.getRightChild();
            page.setIcount(page.getIcount() + 1);

            page.setDirty();
            return true;
        } else {
            return false;
        }
    }

    private boolean insertCompensateWithLeft(IndexPage page, IndexInfo ii, long parentPage, int inParentPosition, int inChildPosition) {
        if (parentPage == -1) {
            // nie ma rodzica, nie ma rodzenstwa
            return false;
        }

        if (inParentPosition - 1 < 0) {
            // nie ma lewego brata
            return false;
        }

        IndexPage parent = getTracePage(parentPage);
        IndexPage left = getTempPage(parent.p[inParentPosition - 1]);
        IndexPage right = page;

        if (left.getIcount() < 2 * getD()) {
            int n = left.getIcount() + right.getIcount() + 1 + 1;
            int mid = n / 2;

            for (int j = left.getIcount(); j < n; j++) {
                if (j < mid) {
                    left.i[j] = insertLeftCompensateSourceIndex(parent, left, right, ii, j, inParentPosition, inChildPosition);
                } else if (j > mid) {
                    right.i[j - mid - 1] = insertLeftCompensateSourceIndex(parent, left, right, ii, j, inParentPosition, inChildPosition);
                } else {
                    parent.i[inParentPosition - 1] = insertLeftCompensateSourceIndex(parent, left, right, ii, j, inParentPosition, inChildPosition);
                }
            }
            for (int j = left.getIcount(); j < n + 1; j++) {
                if (j <= mid) {
                    left.p[j] = insertLeftCompensateSourcePointer(parent, left, right, ii, j, inParentPosition, inChildPosition);
                } else {
                    right.p[j - mid - 1] = insertLeftCompensateSourcePointer(parent, left, right, ii, j, inParentPosition, inChildPosition);
                }
            }

            left.setIcount(mid);
            right.setIcount(n - mid - 1);

            parent.setDirty();
            left.setDirty();
            right.setDirty();
            return true;
        }
        return false;
    }

    private boolean insertCompensateWithRight(IndexPage page, IndexInfo ii, long parentPage, int inParentPosition, int inChildPosition) {
        if (parentPage == -1) {
            // nie ma rodzica - nie ma rodzenstwa
            return false;
        }

        IndexPage parent = getTracePage(parentPage);
        if (inParentPosition + 1 > parent.getIcount()) {//bedzie za duzo w rodzicu, przechodzimy do splita
            return false;
        }

        IndexPage left = page; //lewa wzgledem rodzica = my
        IndexPage right = getTempPage(parent.p[inParentPosition + 1]);

        if (right.getIcount() < 2 * getD()) {
            int n = left.getIcount() + right.getIcount() + 1 + 1;
            int mid = n / 2;
            int p = inChildPosition == left.getIcount() ? inChildPosition - 1 : inChildPosition;
            for (int i = n - 1; i >= p; i--) {
                if (i < mid) {
                    left.i[i] = insertRightCompensateSourceIndex(parent, left, right, ii, i, inParentPosition, inChildPosition);
                } else if (i > mid) {
                    right.i[i - mid - 1] = insertRightCompensateSourceIndex(parent, left, right, ii, i, inParentPosition, inChildPosition);
                } else {
                    parent.i[inParentPosition] = insertRightCompensateSourceIndex(parent, left, right, ii, i, inParentPosition, inChildPosition);
                }
            }
            for (int i = n; i > inChildPosition - 1; i--) {
                if (i <= mid) {
                    left.p[i] = insertRightCompensateSourcePointer(parent, left, right, ii, i, inParentPosition, inChildPosition);
                } else {
                    right.p[i - mid - 1] = insertRightCompensateSourcePointer(parent, left, right, ii, i, inParentPosition, inChildPosition);
                }
            }
            left.setIcount(mid);
            right.setIcount(n - mid - 1);

            parent.setDirty();
            left.setDirty();
            right.setDirty();
            return true;
        }
        return false;
    }

    private IndexInfo split(IndexPage page, IndexInfo ii, int position) {
        IndexPage npage = newPage();
        IndexInfo iiret = new IndexInfo();

        int n = 2 * getD() + 1;
        if (position < getD()) {
            for (int i = n - 1; i >= position; i--) {
                if (i < getD()) {
                    page.i[i] = splitSourceIndex(page, ii, position, i);
                } else if (i > getD()) {
                    npage.i[i - getD() - 1] = splitSourceIndex(page, ii, position, i);
                } else {
                    iiret.setIndex(splitSourceIndex(page, ii, position, i));
                }
            }
            for (int i = n; i >= position; i--) {
                if (i <= getD()) {
                    page.p[i] = splitSourcePointer(page, ii, position, i);
                } else {
                    npage.p[i - getD() - 1] = splitSourcePointer(page, ii, position, i);
                }
            }
        } else {
            for (int i = getD(); i < n; i++) {
                if (i < getD()) {
                    page.i[i] = splitSourceIndex(page, ii, position, i);
                } else if (i > getD()) {
                    npage.i[i - getD() - 1] = splitSourceIndex(page, ii, position, i);
                } else {
                    iiret.setIndex(splitSourceIndex(page, ii, position, i));
                }
            }
            for (int i = getD(); i < n + 1; i++) {
                if (i <= getD()) {
                    page.p[i] = splitSourcePointer(page, ii, position, i);
                } else {
                    npage.p[i - getD() - 1] = splitSourcePointer(page, ii, position, i);
                }
            }
        }
        page.setIcount(getD());
        npage.setIcount(getD());
        iiret.setLeftChild(page.getAddress());
        iiret.setRightChild(npage.getAddress());

        page.setDirty();
        npage.setDirty();
        return iiret;
    }

    private long insertRecord(Record record, long key) {
        long address = -1;
        if (recordAvailableChain == -1) {//nie ma żadnych dziur w pliku
            address = getRecordBound();
            setRecordBound(getRecordBound() + getRecordSize());

        } else {
            address = recordAvailableChain;
            recordAvailableChain = (long) getRecordFromAddress(address, false).getTime();
        }
        setRecordAtAddress(record, address, true);
        System.out.println("Umieszczono rekord o kluczu: " + key);

        return address;
    }

    //SPLIT 
    private Index splitSourceIndex(IndexPage page, IndexInfo ii, int position, int x) {
        if (x < position) {
            return page.i[x];
        } else if (x > position) {
            return page.i[x - 1];
        } else {
            return ii.getIndex();
        }
    }

    private long splitSourcePointer(IndexPage page, IndexInfo ii, int position, int x) {
        if (x < position) {
            return page.p[x];
        } else if (x > position + 1) {
            return page.p[x - 1];
        } else if (x == position) {
            return ii.getLeftChild();
        } else {
            return ii.getRightChild();
        }
    }

    //COMPENSATE WITH LEFT
    private long insertLeftCompensateSourcePointer(IndexPage parent, IndexPage left, IndexPage right, IndexInfo ii, int x, int inParentPosition, int inChildPosition) {
        if (x <= left.getIcount()) {
            return left.p[x];
        } else if (x - left.getIcount() - 1 < inChildPosition) {
            return right.p[x - left.getIcount() - 1];
        } else if (x - left.getIcount() - 1 > inChildPosition + 1) {
            return right.p[x - left.getIcount() - 2];
        } else if (x - left.getIcount() - 1 == inChildPosition) {
            return ii.getLeftChild();
        } else {
            return ii.getRightChild();
        }
    }

    private Index insertLeftCompensateSourceIndex(IndexPage parent, IndexPage left, IndexPage right, IndexInfo ii, int x, int inParentPosition, int inChildPosition) {
        if (x < left.getIcount()) {
            return left.i[x];
        } else if (x > left.getIcount()) {
            if (x - left.getIcount() - 1 < inChildPosition) {
                return right.i[x - left.getIcount() - 1];
            } else if (x - left.getIcount() - 1 > inChildPosition) {
                return right.i[x - left.getIcount() - 2];
            } else {
                return ii.getIndex();
            }
        } else {
            return parent.i[inParentPosition - 1];
        }
    }

//COMPENSATE WITH RIGHT
    private long insertRightCompensateSourcePointer(IndexPage parent, IndexPage left, IndexPage right, IndexInfo ii, int x, int inParentPosition, int inChildPosition) {
        if (x <= left.getIcount() + 1) {
            if (x < inChildPosition) {
                return left.p[x];
            } else if (x > inChildPosition + 1) {
                return left.p[x - 1];
            } else if (x == inChildPosition) {
                return ii.getLeftChild();
            } else {
                return ii.getRightChild();
            }
        } else {
            return right.p[x - left.getIcount() - 2];
        }
    }

    private Index insertRightCompensateSourceIndex(IndexPage parent, IndexPage left, IndexPage right, IndexInfo ii, int x, int inParentPosition, int inChildPosition) {
        if (x < left.getIcount() + 1) {
            if (x < inChildPosition) {
                return left.i[x];
            } else if (x > inChildPosition) {
                return left.i[x - 1];
            } else {
                return ii.getIndex();
            }
        } else if (x > left.getIcount() + 1) {
            return right.i[x - left.getIcount() - 2];
        } else {
            return parent.i[inParentPosition];
        }
    }
}
