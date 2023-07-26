package Bin;

public class Pair{
    public String v0;
    public boolean[] v1;

    public Pair(String v0) {
        this.v0 = v0;
        this.v1 = new boolean[]{false}; // one bit - 0
    }

    public Pair(String v0, boolean[] v1){
        this.v0 = v0;
        this.v1 = v1;
    }
    public Pair copy(){
        return new Pair(v0,v1.clone());
    }

    @Override
    public String toString() {
        return v0+'\u00BB'+BinInterpreter.b2s(v1);
    }
}
