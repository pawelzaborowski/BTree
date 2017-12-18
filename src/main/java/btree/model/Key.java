package btree.model;

public class Key {

    private int value;
    private int recPonint;

    public Key(int value, int pointer){
        this.value = value;
        this.recPonint = pointer;
    }

    public int getValue(){
        return this.value;
    }

    public int getRecPonint(){
        return this.recPonint;
    }
}
