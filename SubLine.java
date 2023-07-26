package Bin;

public class SubLine {
    public char f;
    public String fn;
    public Var[] v;

    public SubLine() {
    }

    public SubLine(char f, String fn, Var[] v) {
        this.f = f;
        this.fn = fn;
        this.v = v;
    }

    public char getF() {
        return f;
    }

    public void setF(char f) {
        this.f = f;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public Var[] getV() {
        return v;
    }

    public void setV(Var[] v) {
        this.v = v;
    }
    public void setV(Var v,int i) {
        this.v[i] = v;
    }
}
