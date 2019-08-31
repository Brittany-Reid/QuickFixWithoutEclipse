package quickfixwithouteclipse;

import quickfixwithouteclipse.quickfix.*;

/**
 * Main class
 */

public class Main{

    public static void main(String[] args) throws Exception{

        //this is the string we want to fix
        String code = "/**class Test*/\n"
        + "import java.util.List;\n"
        + "class Test{\n"
        + "int i=0;\n"
        + "//File file;\n"
        + "}\n";

        code = QuickFix.fixAll(code);
        
        System.out.println(code);
    }
}