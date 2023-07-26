package Bin;

public class Var {

    public int[] vi;
    public boolean[] vb;

    public Var(boolean[] vb) {
        this.vb = vb;
    }
    public Var(int[] vi) {
        this.vi = vi;
    }

    public int[] getVi() {
        return vi;
    }

    public void setVi(int[] vi) {
        this.vi = vi;
    }

    public boolean[] getVb() {
        return vb;
    }

    public void setVb(boolean[] vb) {
        this.vb = vb;
    }

}
