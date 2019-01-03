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
public class Index {

    private long key;
    private long address;

    public Index(long key, long address) {
        this.key = key;
        this.address = address;
    }
    public void update(long key, long address) {
        this.key = key;
        this.address = address;
    }
    public long getKey(){
        return this.key;
    }
    public long getAddress(){
        return this.address;
    }
}
