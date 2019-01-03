/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import static java.lang.Math.sqrt;

/**
 *
 * @author Pawel
 */
public class Record implements Comparable<Record> , java.io.Serializable{

    private double time;

    public double a0;
    public double a1;
    public double a2;

    private double delta;
    private double result;

    public Record(double a0, double a1, double a2)
    {
        this.a0 = a0;
        this.a1 = a1;
        this.a2 = a2;

        this.delta = (a1 * a1) - (4 * a0 * a2);
        this.result = ((-a1 - sqrt(delta)) / 2 / a2) + ((-a1 + sqrt(delta)) / 2 / a2);
    }

//    public Record() {
//        time = 0;
//    }
//
//    public Record(double t) {
//        this.time = t;
//    }

    public void set(Record other) {
        this.time = other.time;
    }

    public void setTime(double t) {
        this.time = t;
    }

    public double getResult(){
        return this.result;
    }

    public double getTime(){
        return time;
    }

    @Override
    public int compareTo(Record o) {
        return ((Double) this.getResult()).compareTo(o.getResult());
    }
}

