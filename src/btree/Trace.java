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
public class Trace {

    public long page;
    public int position;

    public Trace(long a, int p) {
        page = a;
        position = p;
    }

}
