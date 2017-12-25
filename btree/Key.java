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
class Key {
    
    private final int value;
    private final int recordPointer;
    
    public Key(int value, int pointer) {
        this.value = value;
        this.recordPointer = pointer;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int getRecordPointer() {
        return this.recordPointer;
    }
    
}