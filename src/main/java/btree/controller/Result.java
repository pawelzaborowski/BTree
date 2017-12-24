package btree.controller;

public class Result {       // moze samo result?

    private int type;
    private int result;

     public Result(int type, int result) {
        this.type = type;
        this.result = result;
    }

    public Result(int type) {
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
