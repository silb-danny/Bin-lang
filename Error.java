package Bin;

public class Error {
    public static final String[] err = {
            "unnecessary characters", // 0
            "variable doesn't exist", // 1
            "function doesn't exist", // 2
            "no expression", // 3
            "faulty function declaration", // 4
            "faulty variable declaration", // 5
            "unnecessary variable declaration", // 6
            "duplicate variable declaration in input", // 7
            "index out of bounds ... first index taken instead", // 8
            "more than one index ... first index taken instead", // 9
            "not enough arguments in function", // 10 -- manly warning just default other values to 0
            "too many arguments in function", // 11 -- same thing as 10
            "no recursion allowed --> endless loop", // 12
            "file doesn't exist", // 13
            "unreadable file", // 14
            "function already exists" // 15
    };
    public static final String WARNING = "--#--";
    public static void errMsg(int ind) {
        System.out.println(SysC.RED_BOLD+err[ind]);
        System.exit(0);
    }
    public static void errMsg(int ind, int line) {
        System.out.println(SysC.RED_BOLD+err[ind]+" in line: "+line);
        System.exit(0);
    }
    public static void errMsg(int ind, String func) {
        System.out.println(SysC.RED_BOLD+err[ind]+" in function: "+func);
        System.exit(0);
    }
    public static void errMsg(int ind, String func, String var) {
        System.out.println(SysC.RED_BOLD+var+" "+err[ind]+" in function: "+func);
        System.exit(0);
    }
    public static void errMsg(int ind, int line, String func) {
        System.out.println(SysC.RED_BOLD+err[ind]+" in function: "+func+" in line: "+line);
        System.exit(0);
    }
    public static void warningMsg(int ind) {
        System.out.println(SysC.YELLOW_BOLD_BRIGHT+SysC.YELLOW_UNDERLINED+WARNING+" "+err[ind]+" "+WARNING+SysC.RESET);
    }
}
