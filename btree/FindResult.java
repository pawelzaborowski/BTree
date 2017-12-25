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
class FindResult {
    
    final private int type;
    final private int result;
    
    public FindResult(int type, int result) {
        this.type = type;
        this.result = result;
    }
    
    public FindResult(int type) {
        this.type = type;
        this.result = Statics.NULL_POINTER;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getResult() {
        return this.result;
    }
    
}