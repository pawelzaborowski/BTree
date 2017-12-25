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

class RemovedElement {
    
    private final Key key;
    private final Integer pointer;
    
    public RemovedElement(Key key, Integer pointer) {
        this.key = key;
        this.pointer = pointer;
    }
    
    public Key getKey() {
        return this.key;
    }
    
    public Integer getPointer() {
        return this.pointer;
    }
    
}