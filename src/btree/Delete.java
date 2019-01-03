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
public abstract class Delete extends Insert {

    public void delete(long key) {
        setLpage(0);
        if (getH() == 0) {//nie znaleziono klucza
            System.out.println("Nie znaleziono klucza: " + key);
            return;
        }
        IndexPage page = getTracePage(getRoot());
        int i;
        Trace trace[] = new Trace[getH()];
        for (int j = 0; j < getH(); j++) {
            trace[j] = new Trace(-1, -1);
        }
        int t = 0;

        while (true) {
            i = locateKey(page, key);
            if (page.i[i].getKey() == key) { // znalezlismy klucz
                if (page.p[0] == -1) { // strona jest lisciem
                    trace[t].page = page.getAddress();
                    trace[t].position = i;
                    t++;
                    deleteRecord(page.i[i].getAddress());
                    System.out.println("Usunięto rekord o kluczu: " + key);
                    deleteIndex(page, trace);
                    break;
                } else { // strona nie jest lisciem
                    // zamienamy z najmniejszym po prawej
                    deleteRecord(page.i[i].getAddress());
                    System.out.println("Usunięto rekord o kluczu: " + key);
                    //swapAndDeleteLeft(page, i, trace, t);
                    swapAndDeleteRight(page, i, trace, t);
                    break;
                }
            } else if (key < page.i[i].getKey()) { // klucz powinien byc po lewej
                if (page.p[0] != -1) { // strona nie jest lisciem, szukamy dalej
                    trace[t].page = page.getAddress();
                    trace[t].position = i;
                    t++;
                    page = getTracePage(page.p[i]);
                } else { //tu tez nie znaleziono klucza
                    System.out.println("Nie znaleziono klucza: " + key);
                    break;
                }
            } else // klucz powinien byc po prawej
             if (page.p[0] != -1) { // strona nie jest lisciem, szukamy dalej
                    trace[t].page = page.getAddress();
                    trace[t].position = i + 1;
                    t++;
                    page = getTracePage(page.p[i + 1]);
                } else {//tu tez nie znaleziono klucza
                    System.out.println("Nie znaleziono klucza: " + key);
                    break;
                }
        }
    }

    private void swapAndDeleteRight(IndexPage sourcepage, int inSourcePosition, Trace trace[], int t) {
        int i = inSourcePosition;
        trace[t].page = sourcepage.getAddress();
        trace[t].position = i + 1;
        t++;
        IndexPage page = getTracePage(sourcepage.p[i + 1]);

        while (true) {
            trace[t].page = page.getAddress();
            trace[t].position = 0;
            t++;
            if (page.p[0] == -1) { // jest lisciem
                swapIndices(sourcepage, inSourcePosition, page, 0);
                sourcepage.setDirty();
                deleteIndex(page, trace);
                break;
            } else { // nie jest lisciem
                page = getTracePage(page.p[0]);
            }
        }
    }

    @Deprecated
    //metoda wykorzystywana tylko, gdy do usuniecia chcemy zamienic
    //z najwiekszym z lewej, a nie z najmniejszym z prawej
    private void swapAndDeleteLeft(IndexPage sourcepage, int inSourcePosition, Trace trace[], int t) {
        int i = inSourcePosition;
        trace[t].page = sourcepage.getAddress();
        trace[t].position = i;
        t++;
        IndexPage page = getTracePage(sourcepage.p[i]);

        while (true) {
            trace[t].page = page.getAddress();
            trace[t].position = page.getIcount();
            t++;
            if (page.p[0] == -1) { // jest lisciem
                swapIndices(sourcepage, inSourcePosition, page, page.getIcount() - 1);
                sourcepage.setDirty();
                deleteIndex(page, trace);
                break;
            } else { // nie jest lisciem
                page = getTracePage(page.p[page.getIcount()]);
            }
        }
    }

    private void deleteIndex(IndexPage leaf, Trace trace[]) {
        int level = getH() - 1;
        IndexPage page = leaf;

        while (true) {
            if (deleteFrom(page, trace[level].position, level == 0)) {
                return;
            } else if (deleteCompensateWithLeft(page, level <= 0 ? -1 : trace[level - 1].page, level <= 0 ? -1 : trace[level - 1].position, trace[level].position)
                    || deleteCompensateWithRight(page, level <= 0 ? -1 : trace[level - 1].page, level <= 0 ? -1 : trace[level - 1].position, trace[level].position)) {
                return;
            } else {//Merge - level > 0
                if (mergeWithLeft(page, trace[level - 1].page, trace[level - 1].position, trace[level].position)) {
                    page = getTracePage(trace[level - 1].page);
                    trace[level - 1].position -= 1; // bo bedziemy usuwac teraz tego co go zamienilismy w mergu
                    level--;
                } else if (mergeWithRight(page, trace[level - 1].page, trace[level - 1].position, trace[level].position)) {
                    page = getTracePage(trace[level - 1].page);
                    level--;
                }
                // ostatni element z korzenia
                if (page.getAddress() == getRoot() && page.getIcount() == 1) {
                    setRoot(page.p[0]);
                    shrink();
                    deletePage(page);
                    page.setDirty();
                    return;
                }
            }
        }
    }

    private boolean deleteFrom(IndexPage page, int position, boolean isroot) {
        if (page.getIcount() > getD() || isroot) {
            for (int i = position; i < page.getIcount() - 1; i++) {
                page.i[i] = page.i[i + 1];
            }

            for (int i = position; i < page.getIcount(); i++) {
                page.p[i] = page.p[i + 1];
            }

            page.setIcount(page.getIcount() - 1);
            if (page.getIcount() == 0) {
                // strona jest rootem i zostala pusta
                deletePage(page);
                shrink();
                setRoot(-1);
            }
            page.setDirty();
            return true;
        } else {
            return false;
        }
    }

//COMPENSATE WITH LEFT
    private Index deleteLeftCompensateSourceIndex(IndexPage parent, IndexPage left, IndexPage right, int x, int inParentPosition, int inChildPosition) {
        if (x < left.getIcount()) {
            return left.i[x];
        } else if (x > left.getIcount()) {
            if (x - left.getIcount() - 1 < inChildPosition) {
                return right.i[x - left.getIcount() - 1];
            } else {
                return right.i[x - left.getIcount()];
            }
        } else {
            return parent.i[inParentPosition - 1];
        }
    }

    private long deleteLeftCompensateSourcePointer(IndexPage parent, IndexPage left, IndexPage right, int x, int inParentPosition, int inChildPosition) {
        if (x <= left.getIcount()) {
            return left.p[x];
        } else if (x - left.getIcount() - 1 < inChildPosition) {
            return right.p[x - left.getIcount() - 1];
        } else {
            return right.p[x - left.getIcount()];
        }
    }

    private boolean deleteCompensateWithLeft(IndexPage page, long parentPage, int inParentPosition, int inChildPosition) {
        if (parentPage == -1) {
            // nie ma rodzica - nie ma rodzenstwa
            return false;
        }

        if (inParentPosition - 1 < 0) {
            // nie ma lewego brata
            return false;
        }

        IndexPage parent = getTracePage(parentPage);
        IndexPage right = page;
        IndexPage left = getTempPage(parent.p[inParentPosition - 1]);

        if (left.getIcount() > getD()) {
            int n = left.getIcount() + right.getIcount() + 1 - 1;
            int mid = n / 2;
            for (int i = n - 1; i >= mid; i--) {
                if (i < mid) {
                    left.i[i] = deleteLeftCompensateSourceIndex(parent, left, right, i, inParentPosition, inChildPosition);
                } else if (i > mid) {
                    right.i[i - mid - 1] = deleteLeftCompensateSourceIndex(parent, left, right, i, inParentPosition, inChildPosition);
                } else {
                    parent.i[inParentPosition - 1] = deleteLeftCompensateSourceIndex(parent, left, right, i, inParentPosition, inChildPosition);
                }
            }

            for (int i = n; i >= mid; i--) {
                if (i <= mid) {
                    left.p[i] = deleteLeftCompensateSourcePointer(parent, left, right, i, inParentPosition, inChildPosition);
                } else {
                    right.p[i - mid - 1] = deleteLeftCompensateSourcePointer(parent, left, right, i, inParentPosition, inChildPosition);
                }
            }

            left.setIcount(mid);
            right.setIcount(n - mid - 1);

            parent.setDirty();
            left.setDirty();
            right.setDirty();
            return true;
        } else {
            return false;
        }
    }

//COMPENSATE WITH RIGHT
    private Index deleteRightCompensateSourceIndex(IndexPage parent, IndexPage left, IndexPage right, int x, int inParentPosition, int inChildPosition) {
        if (x < left.getIcount() - 1) {
            if (x < inChildPosition) {
                return left.i[x];
            } else {
                return left.i[x + 1];
            }
        } else if (x >= left.getIcount()) {
            return right.i[x - left.getIcount()];
        } else {
            return parent.i[inParentPosition];
        }
    }

    private long deleteRightCompensateSourcePointer(IndexPage parent, IndexPage left, IndexPage right, int x, int inParentPosition, int inChildPosition) {
        if (x <= left.getIcount() - 1) {
            if (x < inChildPosition) {
                return left.p[x];
            } else {
                return left.p[x + 1];
            }
        } else {
            return right.p[x - left.getIcount()];
        }
    }

    private boolean deleteCompensateWithRight(IndexPage page, long parentPage, int inParentPosition, int inChildPosition) {
        if (parentPage == -1) {
            // nie ma rodzica - nie ma rodzenstwa
            return false;
        }

        IndexPage parent = getTracePage(parentPage);
        if (inParentPosition + 1 > parent.getIcount()) {
            return false;
        }

        IndexPage left = page;
        IndexPage right = getTempPage(parent.p[inParentPosition + 1]);

        if (right.getIcount() > getD()) {
            int n = left.getIcount() + right.getIcount() + 1 - 1;
            int mid = n / 2;

            for (int i = inChildPosition; i < n; i++) {
                if (i < mid) {
                    left.i[i] = deleteRightCompensateSourceIndex(parent, left, right, i, inParentPosition, inChildPosition);
                } else if (i > mid) {
                    right.i[i - mid - 1] = deleteRightCompensateSourceIndex(parent, left, right, i, inParentPosition, inChildPosition);
                } else {
                    parent.i[inParentPosition] = deleteRightCompensateSourceIndex(parent, left, right, i, inParentPosition, inChildPosition);
                }
            }

            for (int i = inChildPosition; i < n + 1; i++) {
                if (i <= mid) {
                    left.p[i] = deleteRightCompensateSourcePointer(parent, left, right, i, inParentPosition, inChildPosition);
                } else {
                    right.p[i - mid - 1] = deleteRightCompensateSourcePointer(parent, left, right, i, inParentPosition, inChildPosition);
                }
            }

            left.setIcount(mid);
            right.setIcount(n - mid - 1);

            parent.setDirty();
            left.setDirty();
            right.setDirty();
            return true;
        } else {
            return false;
        }
    }

//MERGE
    private boolean mergeWithLeft(IndexPage page, long parentPage, int inParentPosition, int inChildPosition) {
        if (parentPage == -1) {
            // nie ma rodzica - nie ma rodzenstwa
            return false;
        }

        if (inParentPosition - 1 < 0) {
            // nie ma lewego brata
            return false;
        }

        IndexPage parent = getTracePage(parentPage);
        IndexPage right = page;
        IndexPage left = getTempPage(parent.p[inParentPosition - 1]);

        if (left.getIcount() <= getD()) {
            int n = left.getIcount() + right.getIcount() + 1 - 1;
            for (int i = left.getIcount(); i < n; i++) {
                left.i[i] = deleteLeftCompensateSourceIndex(parent, left, right, i, inParentPosition, inChildPosition);
            }

            for (int i = left.getIcount(); i < n + 1; i++) {
                left.p[i] = deleteLeftCompensateSourcePointer(parent, left, right, i, inParentPosition, inChildPosition);
            }
            parent.p[inParentPosition] = left.getAddress();
            left.setIcount(n);
            deletePage(right);

            parent.setDirty();
            left.setDirty();
            right.setDirty();
            return true;
        } else {
            return false;
        }
    }

    private boolean mergeWithRight(IndexPage page, long parentPage, int inParentPosition, int inChildPosition) {
        if (parentPage == -1) {
            // nie ma rodzica - nie ma rodzenstwa
            return false;
        }

        IndexPage parent = getTracePage(parentPage);
        if (inParentPosition + 1 > parent.getIcount()) {
            return false;
        }

        IndexPage left = page;
        IndexPage right = getTempPage(parent.p[inParentPosition + 1]);

        if (right.getIcount() <= getD()) {
            int n = left.getIcount() + right.getIcount() + 1 - 1;
            for (int i = inChildPosition; i < n; i++) {
                left.i[i] = deleteRightCompensateSourceIndex(parent, left, right, i, inParentPosition, inChildPosition);
            }

            for (int i = inChildPosition; i < n + 1; i++) {
                left.p[i] = deleteRightCompensateSourcePointer(parent, left, right, i, inParentPosition, inChildPosition);
            }

            parent.p[inParentPosition + 1] = left.getAddress();
            left.setIcount(n);
            deletePage(right);

            parent.setDirty();
            left.setDirty();
            right.setDirty();
            return true;
        } else {
            return false;
        }
    }

    private void swapIndices(IndexPage page1, int pos1, IndexPage page2, int pos2) {
        Index i = page1.i[pos1];
        page1.i[pos1] = page2.i[pos2];
        page2.i[pos2] = i;
    }

    private void deletePage(IndexPage page) {
        page.setAvailable(indexAvailableChain);
        indexAvailableChain = page.getAddress();
    }

    private void deleteRecord(long address) {
        setRecordAtAddress(new Record(recordAvailableChain), address, true);
        recordAvailableChain = address;
    }
}
