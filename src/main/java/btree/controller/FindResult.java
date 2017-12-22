package btree.controller;

public class FindResult {       // moze samo result?

    private int type;
    private int result;

     public FindResult(int type, int result) {
        this.type = type;
        this.result = result;
    }

    public FindResult(int type) {
        this.type = type;
        this.result = -6;//Statics.NULL_POINTER;
    }

    public int getType() {
        return this.type;
    }

    public int getResult() {
        return this.result;
    }

}
