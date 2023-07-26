package Bin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/*
* important regex :
* \(.*\)|\{.*\}|\[.*\] -> for removing everything that's in between the brackets
* (?<=\).*),|(?<!\(.*), -> for only looking at commas that are not in brackets
* : (?<=[\)\}\]].*),|(?<![\(\{\[].*), -> all types of brackets
* [,\(\)] -> splitting func name and inner arguments
* */
public class BinInterpreter {
    public static int clamp(int m, int v, int M) {
        return Math.min(Math.max(v,m),M);
    }
    public static File getFile(String filename) {
        // the function gets a file and returns a file object
        File prog = new File(filename);
        if(!prog.exists()) {
            Error.warningMsg(13);
            return null;
        }
        if(!prog.canRead()) {
            Error.warningMsg(14);
            return null;
        }
        return prog;
    }
    // binary functions
    public static boolean[] and(boolean[] v0, boolean[] v1) {
        int mlen = Math.max(v0.length,v1.length);
        boolean[] out = new boolean[mlen];
        for (int i = 0; i < mlen; i++) {
            if(v0.length <= i | v1.length <= i)
                return out;
            else
                out[i] = v0[i] & v1[i];
        }
        return out;
    }
    public static boolean[] or(boolean[] v0, boolean[] v1) {
        int mlen = Math.max(v0.length,v1.length);
        boolean[] out = new boolean[mlen];
        for (int i = 0; i < mlen; i++) {
            if(v0.length <= i)
                out[i] = v1[i];
            else if(v1.length <= i)
                out[i] = v0[i];
            else
                out[i] = v0[i] | v1[i];
        }
        return out;
    }
    public static boolean[] not(boolean[] v0) {
        boolean[] out = new boolean[v0.length];
        for (int i = 0; i < v0.length; i++) {
            out[i] = !v0[i];
        }
        return out;
    }
    // useful functions
    public static boolean[] s2b (String s){
        boolean[] out = new boolean[s.length()];
        int i = 0;
        for (char c : s.toCharArray()) {
            out[i ++] = c == '1';
        }
        return out;
    }
    public static String b2s (boolean[] b){
        char[] c = new char[b.length];
        for (int i = 0; i < b.length; i++) {
            c[i] = b[i]?'1':'0';
        }
        return new String(c);
    }
    public static int wholeSize(boolean[][] arr) {
        int n = 0;
        for (boolean[] booleans : arr) {
            n += booleans.length;
        }
        return n;
    }
    public static boolean[] concatb (boolean[][] arr) {
        // concats 2d boolean into 1d boolean array based on rows
        boolean[] narr = new boolean[wholeSize(arr)];
        int n = 0;
        for (boolean[] booleans : arr) {
            System.arraycopy(booleans, 0, narr, n , booleans.length);
            n += booleans.length;
        }
        return narr;
    }
    public static int getVar(String varN, Pair[] vars) {
        if(vars == null)
            return -1;
        for (int j = 0; j < vars.length; j++) {
            if(vars[j] == null) // got to the end of the array
                return -1;
            if(vars[j].v0.equals(varN))
                return j;
        }
        return -1; // value doesn't exist
    }
    public static void setVar(int[][] iv, Pair[] vars, boolean[] val, int i) {
        // set or print specific value
        if(iv[i][0] == -1) // if asked to print
            System.out.println(b2s(val));
        else { // if set
            if(iv[i][1] == -1) { // set all array
                vars[iv[i][0]].v1 = val.clone();
            }
            else { // set specific index
                if(iv[i][1] > vars[iv[i][0]].v1.length || iv[i][1] < 0) { // index out of bounds
                    Error.warningMsg(8);
                    vars[iv[i][0]].v1[0] = val[0]; // automatically takes first index
                }
                else
                    vars[iv[i][0]].v1[iv[i][1]] = val[0];
            }
        }
    }
    public static boolean[][] getVarArr(int[] ind, Pair[] vars) {
        boolean[][] out = new boolean[ind.length][];
        for (int i = 0; i < ind.length; i++) {
            out[i] = vars[ind[i]].v1.clone();
        }
        return out;
    }
    // node functions
    public static boolean[][] n2b(Node<boolean[]> n, int size) {
        // from boolean[] node to 2d boolean array
        boolean[][] out = new boolean[size][1]; // automatic setting to false everything that won't be initiated
        for (int i = 0; i < size; i++) {
            if(n == null || n.val == null) // if none left
                break;
            out[i] = n.val;
            n = n.getNext();
        }
        return out;
    }
    public static Function findF(Node<Function> func, String s) {
        if(func == null)
            return null;
        while(func != null) {
            if(func.val == null) {
                func = func.getNext();
                continue;
            }
            if (func.val.name.equals(s)) {
                return func.val; // returns function "template"
            }
            func = func.getNext();
        }
        Error.errMsg(2);
        return null;
    }
    public static Node<String> addUsed(Node<String> used, String s, int err) { // can't be used for includes because it shouldn't throw error
        // err in case already exists
        if(used == null)
            return null;
        Node<String> n,nused = new Node<>(used.val);
        n = nused; // header
        while(used.getNext() != null) {
            if(used.val == null) {
                used = used.getNext();
                continue;
            }
            if (used.val.equals(s)) {
                Error.errMsg(err);
            }
            nused.setNext(new Node<>(used.val));
            nused = nused.getNext();
            used = used.getNext();
        }
        if(used.val != null && !used.val.equals(s)) { // adds new function to the end
            nused.setNext(new Node<>(used.val));
            nused = nused.getNext();
        }
        nused.setNext(new Node<>(s));
        return n;
    }
    public static boolean addFunction(Function f, Node<Function> nds) {
        // adding function if unique
        if(nds == null)
            return false;
        while(nds.getNext() != null) { // runs through all parsed functions
            if(nds.val == null) {
                nds = nds.getNext();
                continue;
            }
            if(nds.val.name.equals(f.name)) // if already exists
                return false;
            nds = nds.getNext();
        }
        if(nds.val == null || !nds.val.name.equals(f.name))
            nds.setNext(new Node<>(f)); // adds new function to the end
        return true;
    }
    public static boolean addIncludes(String s, Node<String> incs) {
        // adding includes if unique
        if(incs == null)
            return false;
        while(incs.getNext() != null) { // runs through all parsed includes
            if(incs.val == null) {
                incs = incs.getNext();
                continue;
            }
            if(incs.val.equals(s)) // if already exists
                return false;
            incs = incs.getNext();
        }
        if(incs.val == null || !incs.val.equals(s))
            incs.setNext(new Node<>(s)); // adds new includes to the end

        return true;
    }
    // parse functions
    public static Var varParse(String exp, Pair[] vars) {
        int not = 1;
        char c = exp.replace("!", "").replace("[", "").charAt(0);
        if(c == '0' | c == '1' ) {// the variable is binary
            if(exp.contains("!") || exp.contains("[") || exp.contains("]"))
                Error.errMsg(0);
            return new Var(s2b(exp));
        }
        else {
            if (exp.charAt(0) == '!') {
                not = -1;
            }
            exp = exp.replace("!", "");
            int[] arr = new int[2];
            if (exp.contains("[")) {
                arr[0] = not*(getVar(exp.substring(0,exp.indexOf('[')),vars)+1); // getting variable
                arr[1] = Integer.parseInt(exp.substring(exp.indexOf('[')+1,exp.indexOf(']'))); // getting variable index
            } else {
                arr[0] = (getVar(exp,vars)+1)*not;
                arr[1] = -1; // full array
            }
            if(arr[0]*not-1 == -1)
                Error.errMsg(1); // variable doesnt exist
            return new Var(arr);
        }
    }
    public static int[] inVarParse(String exp, Pair[] vars) {
        if(exp.isEmpty() || exp.contains("-"))
            return new int[]{-1};
        char c = exp.replace("!", "").replace("[", "").charAt(0);
        if(c == '0' | c == '1') {// the variable is binary
            Error.errMsg(0);
        }
        int[] arr = new int[2];
        if (exp.contains("[")) {
            arr[0] = getVar(exp.substring(0,exp.indexOf('[')),vars);
            arr[1] = Integer.parseInt(exp.substring(exp.indexOf('[')+1,exp.indexOf(']')));
        }
        else {
            arr[0] = getVar(exp,vars);
            arr[1] = -1;
        }
        return arr;
    }
    public static SubLine fParse(String exp, Pair[] vars) {
        SubLine sl = new SubLine();
        String[] s;
        if(exp.contains("$")) {
            sl.setF('$');
            s = exp.replaceAll("\\$.+\\(","").replaceAll("\\).*","").replaceAll(",{2,}",",").replaceAll("^,|,$","").split("[,()]");
            sl.setFn(exp.replaceAll("\\$|(\\(.*\\))",""));
            if(!s[0].isEmpty()) {
                sl.setV(new Var[s.length]);
                for (int i = 0; i < s.length; i++) {
                    sl.setV(varParse(s[i],vars), i);
                }
            }
        }
        else if(exp.contains("&")) {
            sl.setF('&');
            s = exp.split("&");
            sl.setV(new Var[2]);
            for (int i = 0; i < 2; i++) {
                sl.setV(varParse(s[i],vars),i);
            }
        }
        else if(exp.contains("|")) {
            sl.setF('|');
            s = exp.split("\\|");
            sl.setV(new Var[2]);
            for (int i = 0; i < 2; i++) {
                sl.setV(varParse(s[i],vars),i);
            }
        }
        else {
            sl.setF('=');
            sl.setV(new Var[]{varParse(exp,vars)});
        }
        return sl;
    }
    public static Line parseLine(String in, Pair[] vars) {
        String[] val,inP = in.split("=");
        if (inP.length > 2) // error message unnecessary characters
            Error.errMsg(0);
        else if(inP.length <= 0)
            Error.errMsg(3);
        String exp = inP[inP.length - 1];
        Line nline = new Line();
        if(exp.contains("$"))
            val = exp.replaceAll("[{}]","").split(",(?=\\$)|(?<=\\)),");
        else
            val = exp.replaceAll("[{}]","").split(",");
        // after splitting based on external commas -- now parsing input expression
        if(val.length <= 0)
            Error.errMsg(3);
        if (exp.charAt(0) == '{') {
            nline.setF('{');
            nline.setInternalFuncs(new SubLine[val.length]);
            for (int i = 0; i < val.length; i++) {
                nline.setInternalFuncs(fParse(val[i],vars),i);
            }
        } // concat boolean
        else if(val.length > 1) {
            nline.setF(',');
            nline.setInternalFuncs(new SubLine[val.length]);
            for (int i = 0; i < val.length; i++) {
                nline.setInternalFuncs(fParse(val[i],vars),i);
            }
        } // ,
        else {
            nline.setLine(fParse(val[0],vars));
        }
        // now parsing the output variables
        if(inP.length == 1) {
            nline.setIv(new int[val.length][1]);
            nline.setIv(-1);
        }
        else {
            int[] n;
            val = inP[0].split(",");
            nline.setIv(new int[val.length][2]);
            for (int i = 0; i < val.length; i++) {
                n = inVarParse(val[i],vars);
                if(n[0] == -1){
                    nline.setIv(-1,i,0);
                } else {
                    nline.setIv(n[0],i,0);
                    nline.setIv(n[1],i,1);
                }
            }
        }

        return nline;
    }
    public static Function parseFunction(String f) {
        Function nFunc = new Function();
        String fDec = f.replaceAll("[\\s]","").replaceAll("}(?!;)","~");
        // getting function parts
        String[] vi,vo,v = null,head,body = fDec.substring(0,fDec.indexOf('~')).replaceFirst("\\{","~").split("~");
        if(body.length != 2)
            Error.errMsg(4);
        fDec = body[0].replaceAll(",{2,}",",");
        head = fDec.replaceAll("[\\[)]","").split("[](]");
        if(head.length != 3)
            Error.errMsg(4);
        // removing unnecessary characters
        head[0] = head[0].replaceAll("^,|,$","");
        head[2] = head[2].replaceAll("^,|,$","");
        int j = 0,s,varCount,n,ind;
        body = body[1].split(";");
        for (int i = 0; i < body.length; i++) {
            n = body[i].indexOf("//");
            if(n != -1) // getting rid of comments
                body[i] = body[i].substring(0,n);
        }
        vi = head[2].split(",");
        vo = head[0].split(",");
        nFunc.inLength = vi.length;
        nFunc.outInd = new int[vo.length];
        if(body[0].charAt(0) == '@') {// checking if variables declared
            n = 1;
            v = body[0].replace("@","").split(",");
        } else
            n = 0;
        varCount = nFunc.inLength + nFunc.outInd.length + ((n == 1)?v.length:0);
        nFunc.lines = new Line[body.length+((n == 1)?-1:0)];
        nFunc.vars = new Pair[varCount];
        // head parsing + variable parsing:
        nFunc.name = head[1];
        for (int i = 0;i < vi.length; i++) { // input variables setting
            s = getVar(vi[i],nFunc.vars);
            if(s != -1)
                Error.errMsg(7);
            nFunc.vars[i] = new Pair(vi[i]);
        } ind = vi.length;
        for (int i = 0; i < vo.length; i++) { // output variables setting
            s = getVar(vo[i],nFunc.vars);
            if(s != -1) // if var exists --> if both in output allowed
                nFunc.outInd[i] = s;
            else {
                nFunc.vars[ind + j] = new Pair(vo[i]);
                nFunc.outInd[i] = j + ind;
                j ++;
            }
        }ind += j;
        if(n == 1) { // variable declaration parsing if @ exists
            j = 0;
            for (String value : v) {
                s = getVar(value, nFunc.vars);
                if (s != -1)
                    Error.warningMsg(6);
                else {
                    nFunc.vars[ind + j] = new Pair(value);
                    j++;
                }
            }
        }
        // body parsing:
        for (int i = 0; i < nFunc.lines.length; i++) {
            nFunc.lines[i] = parseLine(body[i + n], nFunc.vars);
        }
        return nFunc;
    } // already knows that its a function based on [?
    public static void parseIncludes(String dir, String inc, String f) throws FileNotFoundException {
        // look over already included file names
        inc = inc.replaceAll("inc\\{|}","");
        if(inc.equals(f))
            return;
        String[] fNames = inc.split(",");
        for (String file:fNames) {
            parseFile(dir,file);
        }
    }
    public static void parseFile(String dir, String file) throws FileNotFoundException {
        /* reading file */
        File prog = getFile(dir + file + ".txt");
        if(prog == null) // in case file doesn't exist
            return;
        Scanner fileStr = new Scanner(prog);
        System.out.print('.');
        /* check includes (if exists break out of parse file)*/
        if(!addIncludes(file,includes)) // already exists
            return;
        /* making string */
        StringBuilder str = new StringBuilder();
        while (fileStr.hasNextLine())
            str.append(fileStr.nextLine());
        fileStr.close();
        String[] progs = str.toString().replaceAll("\\s","").split("#"); // converting taking out all #-> line starters
        for (String p: progs) {
            if(p.replaceFirst("\\[.*].+\\(.*\\)","^~").contains("^~")){ // in case function
                if(!addFunction(parseFunction(p),funcs))
                    Error.warningMsg(15);
            }
            else if(p.contains("inc")) { // in case include
                parseIncludes(dir,p,file);
            }
        }
        System.out.println("fin");
    }
    // calc --> running functions
    public static boolean[] runVar(Line l, Pair[] vars, int i) {
        boolean[] out = new boolean[1];
        if(l.v[i].vi == null) { // boolean array
            out = l.v[i].vb;
        }
        else { // variable
            if(l.v[i].vi[0] < 0) { // not
                if(l.v[i].vi[1] == -1) // all the array
                    out = vars[-1*(l.v[i].vi[0])-1].v1;
                else { // specific index
                    if(l.v[i].vi[1] > vars[-1*(l.v[i].vi[0])-1].v1.length || l.v[i].vi[1] < 0) {
                        Error.warningMsg(8);
                        out[0] = vars[l.v[i].vi[0]-1].v1[0];
                    }
                    else
                        out[0] = vars[-1*(l.v[i].vi[0])-1].v1[l.v[i].vi[1]]; // getting specific index
                }
                out = not(out); // setting everything to not
            }
            else { // regular variable
                if(l.v[i].vi[1] == -1) // all the array
                    out = vars[l.v[i].vi[0]-1].v1;
                else { // specific index
                    if(l.v[i].vi[1] > vars[l.v[i].vi[0]-1].v1.length || l.v[i].vi[1] < 0) {
                        Error.warningMsg(8);
                        out[0] = vars[l.v[i].vi[0]-1].v1[0];
                    }
                    else
                        out[0] = vars[l.v[i].vi[0]-1].v1[l.v[i].vi[1]]; // getting specific index
                }
            }
        }
        return out;
    }
    public static boolean[] runVar(SubLine l, Pair[] vars, int i) {
        boolean[] out = new boolean[1];
        if(l.v[i].vi == null) { // boolean array
            out = l.v[i].vb;
        }
        else { // variable
            if(l.v[i].vi[0] < 0) { // not
                if(l.v[i].vi[1] == -1) // all the array
                    out = vars[-1*(l.v[i].vi[0])-1].v1;
                else { // specific index
                    if(l.v[i].vi[1] > vars[-1*(l.v[i].vi[0])-1].v1.length || l.v[i].vi[1] < 0) {
                        Error.warningMsg(8);
                        out[0] = vars[l.v[i].vi[0]-1].v1[0];
                    }
                    else
                        out[0] = vars[-1*(l.v[i].vi[0])-1].v1[l.v[i].vi[1]]; // getting specific index
                }
                out = not(out); // setting everything to not
            }
            else { // regular variable
                if(l.v[i].vi[1] == -1) // all the array
                    out = vars[l.v[i].vi[0]-1].v1;
                else { // specific index
                    if(l.v[i].vi[1] > vars[l.v[i].vi[0]-1].v1.length || l.v[i].vi[1] < 0) {
                        Error.warningMsg(8);
                        out[0] = vars[l.v[i].vi[0]-1].v1[0];
                    }
                    else
                        out[0] = vars[l.v[i].vi[0]-1].v1[l.v[i].vi[1]]; // getting specific index
                }
            }
        }
        return out;
    }
    public static boolean[][] calcSLine(SubLine l, Pair[] vars, Node<String> ffs){
        char c = l.getF();
        boolean[][] out = new boolean[1][1];
        if(c == '=') { // if just equals
            out[0] = runVar(l,vars,0);
        }
        else if (c == '&') { // and
            out[0] = and(runVar(l,vars,0),runVar(l,vars,1));
        }
        else if (c == '|') { // or
            out[0] = or(runVar(l,vars,0),runVar(l,vars,1));
        }
        else if (c == '$') { // custom function
            out = function(l,vars,l.fn,ffs);
        }
        return out;
    }
    public static void calcLine(Line l, Pair[] vars, Node<String> ffs){
        char c = l.getF();
        boolean[][] out = new boolean[1][1];
        if(c == '=') { // if just equals
            out[0] = runVar(l,vars,0);
        }
        else if (c == '&') { // and
            out[0] = and(runVar(l,vars,0),runVar(l,vars,1));
        }
        else if (c == '|') { // or
            out[0] = or(runVar(l,vars,0),runVar(l,vars,1));
        }
        else if (c == '$') { // custom function
            out = function(l,vars,l.fn,ffs);
        }
        else if (c == '{') { // concat
            Node<boolean[]> sn,node = new Node<>(); // temporary array
            int n = 0; // amount of nodes
            sn = node;
            for (SubLine sl : l.internalFuncs) { // getting all data
                out = calcSLine(sl,vars,ffs);
                for (boolean[] booleans : out) {
                    n ++;
                    node.setNext(new Node<>(booleans));
                    node = node.getNext();
                }
            }
            out[0] = concatb(n2b(sn.getNext(),n));
        }
        else if (c == ',') { // variable breakUp
            Node<boolean[]> sn,node = new Node<>(); // temporary array
            int n = 0; // length of array
            sn = node;
            for (SubLine sl : l.internalFuncs) { // getting all data
                out = calcSLine(sl,vars,ffs);
                for (boolean[] booleans : out) {
                    n++;
                    node.setNext(new Node<>(booleans));
                    node = node.getNext();
                }
            }
            out = n2b(sn.getNext(),n);
        }
        for (int i = 0; i < l.iv.length; i++) {
            setVar(l.iv,vars,out[(c == ',' || c == '$')?i:0],i);
        }
    }
    public static boolean[][] function(Line ls, Pair[] vars ,String fName, Node<String> funcsUntilNow) {
        String[] s = fName.split("\\*"); // splitting for a loop
        funcsUntilNow = addUsed(funcsUntilNow,s[0],12); // preventing recursion
        Function f = new Function(findF(funcs,s[0]));
        int ini = f.inLength,n = 0;
        if(s.length == 1)
            n = 1;
        else if(s.length > 2)
            Error.errMsg(0);
        else {
            n = Math.abs(Integer.parseInt(s[1]));
        }
        /* setting input arguments */
        if(f.inLength < ls.v.length) // to many arguments
            Error.warningMsg(11);
        else if(f.inLength > ls.v.length) {// not enough warning
            ini = ls.v.length;
        }
        for (int i = 0; i < ini; i++) {
            f.vars[i].v1 = runVar(ls,vars,i).clone(); // TODO: might be problematic
        }
        /* running function */
        for (int i = 0; i < n; i++) { // run n times
            for (Line l : f.lines) { // foreach line in the function
                calcLine(l,f.vars,funcsUntilNow);
            }
        }
        return getVarArr(f.outInd,f.vars);
    }
    public static boolean[][] function(SubLine ls, Pair[] vars ,String fName, Node<String> funcsUntilNow) {
        String[] s = fName.split("\\*"); // splitting for a loop
        funcsUntilNow = addUsed(funcsUntilNow,s[0],12); // preventing recursion
        Function f = new Function(findF(funcs,s[0]));
        int ini = f.inLength,n = 0;
        if(s.length == 1)
            n = 1;
        else if(s.length > 2)
            Error.errMsg(0);
        else {
            n = Math.abs(Integer.parseInt(s[1]));
        }
        /* setting input arguments */
        if(f.inLength < ls.v.length) // to many arguments
            Error.warningMsg(11);
        else if(f.inLength > ls.v.length) {// not enough warning
            ini = ls.v.length;
        }
        for (int i = 0; i < ini; i++) {
            f.vars[i].v1 = runVar(ls,vars,i).clone(); // TODO: might be problematic
        }
        /* running function */
        for (int i = 0; i < n; i++) { // run n times
            for (Line l : f.lines) { // foreach line in the function
                calcLine(l,f.vars,funcsUntilNow);
            }
        }
        return getVarArr(f.outInd,f.vars);
    }
    // print functions
    public static void variables() {
        for (int i = 0; i < indx; i++) {
            System.out.println(i+" |"+varz[i].v0+":"+b2s(varz[i].v1));
        }
    }
    public static void functions(Node<Function> f) {
        while (f != null) {
            System.out.println(f.val);
            f = f.getNext();
        }
    }
    public static void includes(Node<String> f) {
        while (f != null) {
            System.out.println(f.val);
            f = f.getNext();
        }
    }
    // static values
    public static Node<Function> funcs = new Node<>(); // function templates
    public static Pair[] varz;
    public static Node<String> includes = new Node<>();
    public static int indx;
    public static String dirf;
    public static Scanner sc = new Scanner(System.in);
    public static boolean conLine = false;
    public static String[][] functionList = {
            {"?e","ends run"},
            {"?v","prints all variables in workspace"},
            {"?f","prints all available functions"},
            {"?cv","clear all local variables"},
            {"?ov","overwrite local variables"},
            {"?i","print all included function files"},
            {"?i: ...","include function file (... -> function name)"},
            {"?l","continuous lang lines i.e. like lines in a function"},
            {"@ ...","declares local variables (... -> variable name)"},
            {": ...","single lang line run (...->like lines in function)"}
    };
    // main - console + input parser
    public static void addLocalVar(String name) {
        if(getVar(name,varz) != -1) // doesn't exist
            return;
        if(indx < varz.length)
            varz[indx ++] = new Pair(name);
        else
            System.out.println("not enough space in local memory");
    }
    public static void parseInput(String in) throws FileNotFoundException {
        if(in.charAt(0) == '?') { // if first character is ? -? meaning system command
            if(conLine) {
                conLine = false;
                return;
            }
            in = in.replaceFirst("\\?","");
            if(in.equals("v")) // print vars
                variables();
            else if(in.equals("f")) // print functions
                functions(funcs);
            else if(in.equals("cv")) { // clear variables
                System.out.println("are you sure you want to reset mem : (y/n)");
                if(!sc.next().equals("n")) {
                    System.out.println("enter new size, 0 - for current size");
                    int n = sc.nextInt();
                    indx = 0;
                    if(n < 1)
                        varz = new Pair[varz.length];
                    else
                        varz = new Pair[n];
                }
            }
            else if(in.equals("ov")) // overwrite variables
                indx = 0;
            else if(in.equals("i")) // print includes
                includes(includes);
            else if(in.contains("i:")) { // include files ---- path from content root
                in = in.replaceFirst("i:","");
                parseIncludes(dirf,in,null);
            }
            else if(in.contains("l")) { // continuous lines
                conLine = true;
            }
            else {
                System.out.println(SysC.WHITE_UNDERLINED+SysC.WHITE_BOLD+" list of commands:"+SysC.RESET);
                for (String[] strings : functionList) {
                    System.out.println("\t" + strings[0] + " - " + strings[1]);
                }
            }
        }
        else if(in.charAt(0) == '@') { // declaring variables
            in = in.replaceFirst("@","");
            String[] sin = in.split(",");
            for (String s: sin) {
                addLocalVar(s);
            }
        }
        else if(in.charAt(0) == ':') { // run line
            in = in.replaceFirst(":","");
            calcLine(parseLine(in.replaceAll("\\s",""),varz),varz,new Node<>()); // run line
        }
        else if(conLine) {
            calcLine(parseLine(in.replaceAll("\\s",""),varz),varz,new Node<>()); // run line
        }
        else  // actual function calls
            System.out.println("other function");
    }
    public static void console() throws FileNotFoundException {
        String in;

        /* getting dir folder */
        System.out.println("enter working directory");
        dirf = sc.nextLine().trim()+'/';

        /* initial mem alloc: */
        System.out.println("how many vars do you want to alloc: ");
        int n = sc.nextInt();
        if(n < 1) // defaults to 100
            varz = new Pair[100];
        else
            varz = new Pair[n];
        indx = 0;
        sc.nextLine();

        /* start program */
        System.out.println("enter ? for list of functions");
        System.out.print(">> ");
        in = sc.nextLine();
        while (!in.equals("?e")) {// ?e exit program
            if(!in.isEmpty())
                parseInput(in.trim());
            if(conLine)
                System.out.print("- ");
            else
                System.out.print(">> ");
            in = sc.nextLine();
        }
        System.out.println(SysC.BLUE_BOLD_BRIGHT+"!thanks for running bin!");
        System.exit(0);
    }
    public static void main(String[] args) throws FileNotFoundException {
        console();
    }


}
