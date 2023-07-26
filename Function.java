package Bin;

public class Function {
    public String name;
    public Pair[] vars;
    public int inLength;
    public int[] outInd;
    public Line[] lines;

    public Function() {
    }

    public Function(String name) {
        this.name = name;
    }

    public Function(String name, Pair[] vars, int[] outInd, int inLength, Line[] lines) {
        this.name = name;
        this.vars = vars;
        this.outInd = outInd;
        this.inLength = inLength;
        this.lines = lines;
    }

    public Function (Function f) {
        this.name = f.name;
        this.outInd = f.outInd;
        this.lines = f.lines;
        this.inLength = f.inLength;
        this.vars = new Pair[f.vars.length];
        for (int i = 0; i < vars.length; i++) {
            this.vars[i] = (f.vars[i] == null)? null :f.vars[i].copy();
        }
    }

    String pairs() {
        StringBuilder s = new StringBuilder();
        for (Pair p : vars) {
            s.append(p).append(", ");
        }
        return s.toString();
    }
    @Override
    public String toString() {
        return "func: "+SysC.WHITE_UNDERLINED+SysC.WHITE_BOLD+name+SysC.RESET+'\n'+
                " \u21AA vars: "+pairs()+'\n'+
                " \u21AA inLength: "+inLength+'\n'+
                " \u21AA outInd: ... "+outInd.length+'\n'+
                " \u21AA Lines: ... "+lines.length;
    }
}
