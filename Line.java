package Bin;

public class Line {
    public int[][] iv; // input variables
    public char f; // type function
    public String fn; // function details
    public SubLine[] internalFuncs; // case f = (',' or '{') other smaller lines
    public Var[] v; // variables output

    public Line() {
    }

    public Line(int[][] iv, char f, String fn, SubLine[] internalFuncs, Var[] v) {
        this.iv = iv;
        this.f = f;
        this.fn = fn;
        this.internalFuncs = internalFuncs;
        this.v = v;
    }
    public void setLine(SubLine sb) {
        this.f = sb.getF();
        this.fn = sb.getFn();
        this.v = sb.getV();
    }
    public int[][] getIv() {
        return iv;
    }

    public void setIv(int[][] iv) {
        this.iv = iv;
    }
    public void setIv(int v) {
        for (int i = 0; i < iv.length; i++) {
            for (int j = 0; j < iv[0].length; j++) {
                iv[i][j] = v;
            }
        }
    }
    public void setIv(int v,int i,int j) {
        this.iv[i][j] = v;
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

    public SubLine[] getInternalFuncs() {
        return internalFuncs;
    }

    public void setInternalFuncs(SubLine[] internalFuncs) {
        this.internalFuncs = internalFuncs;
    }
    public void setInternalFuncs(SubLine internalFunc, int i) {
        this.internalFuncs[i] = internalFunc;
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
