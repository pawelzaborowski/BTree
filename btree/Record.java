/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
/**
 *
 * @author Pawel
 */
public class Record {
    
    private final double time;
    
    private static final NumberFormat formatter = new DecimalFormat("0.######E0");
    
    public Record(double time) {
        this.time = time;
    }

    public double getTime() {
        return this.time;
    }
    
    @Override
    public String toString() {
        return String.format("time: " + formatter.format(time));
    }
}
