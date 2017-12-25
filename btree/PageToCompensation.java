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
public class PageToCompensation {
    
    private final IndexPage page;
    private final int type;
    
    public PageToCompensation(IndexPage page, int type) {
        this.page = page;
        this.type = type;
    }
    
    public IndexPage getPage() {
        return this.page;
    }
    
    public int getType() {
        return this.type;
    }
    
}
