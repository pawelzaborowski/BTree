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
public 
class KeyToReplace {
    
    private final Key key;
    private final int indexOfKey; //index zamienianego klucza
    private final IndexPage leaf;
    
    public KeyToReplace(Key key, int indexOfKey, IndexPage leaf) {
        this.key = key;
        this.indexOfKey = indexOfKey;
        this.leaf = leaf;
    }
    
    public Key getKey() {
        return this.key;
    }
    
    public int getIndexOfKey() {
        return this.indexOfKey;
    }
    
    public IndexPage getLeaf() {
        return this.leaf;
    }
}