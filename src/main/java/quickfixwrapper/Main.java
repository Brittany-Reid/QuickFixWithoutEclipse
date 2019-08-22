package quickfixwrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl;

/**
 * Main class
 */

public class Main{

    public static void main(String[] args){
        //set up the eclipse compiler
        JavaCompiler compiler = new EclipseCompilerWrapper();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits;

        //this is the string we want to compile
        String code = "class Main{\n"
        + "int i=0\n"
        + "}\n";

        File file = new File("Main.java");
        Writer w;
        try{
            if (!file.exists()) {
                file.createNewFile();
            }

            w = new FileWriter(file);
            w.write(code);
            w.close();
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        
        //add to comp unit
        compilationUnits = fileManager.getJavaFileObjects(file);
        System.out.print("done");
        //compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();

        //IProblemFactory pf = new EclipseCompilerImpl(null, null, null).getProblemFactory();
    }
}