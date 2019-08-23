package quickfixwithouteclipse;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler.CompilationTask;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl;

/**
 * Main class
 */

public class Main{

    public static void main(String[] args){

        //set up the eclipse compiler
        EclipseCompiler compiler = new EclipseCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits;

        //this is the string we want to compile
        String code = "class Test{\n"
        + "int i=0\n"
        + "}\n";

        File file = new File("Test.java");
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
        
        // //add to comp unit
        compilationUnits = fileManager.getJavaFileObjects(file);
        CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
        task.call();
        // System.out.print("done");

        List<IProblem> problems = compiler.getIProblems();
        IProblem p = problems.get(0);
        System.out.println("[" + p.getArguments()[1] + "]");

        // //IProblemFactory pf = new EclipseCompilerImpl(null, null, null).getProblemFactory();
    }
}