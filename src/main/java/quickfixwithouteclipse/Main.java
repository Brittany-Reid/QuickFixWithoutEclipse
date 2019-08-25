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
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnitWrapper;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.corext.fix.UnusedCodeFix;
import org.eclipse.jdt.internal.corext.fix.UnusedCodeFixCore;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;
import org.eclipse.jdt.internal.corext.fix.IProposableFix;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;

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
        String code = "import java.util.List;\n"
        + "class Test{\n"
        + "int i=0;\n"
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
        System.out.println("[" + p.getArguments()[0] + "]");

        ASTParser parser = ASTParser.newParser(AST.JLS11);
        parser.setResolveBindings(true);
        parser.setStatementsRecovery(true);
        parser.setBindingsRecovery(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setEnvironment(null, null, null, true);
        parser.setSource(code.toCharArray());
        parser.setUnitName(null);
        ASTNode a = parser.createAST(null);
        CompilationUnitWrapper cu = new CompilationUnitWrapper(a.getAST());
        //CompilationUnitWrapper cuw = new CompilationUnitWrapper(cu);
        //ICompilationUnit icu = (ICompilationUnit) cu.getJavaElement();


        //convert to problem location
        ProblemLocation problem = new ProblemLocation(p);

        //cannot use unusedcodefix as it relies on making changes to ui objects
        //UnusedCodeFix.createRemoveUnusedImportFix(cu, problem);
        //this is the code utilized in eclipse.jdt.ls, for incorperating within other ides
        IProposableFix fix = UnusedCodeFixCore.createRemoveUnusedImportFix(cu, problem);
        try{
            CompilationUnitChange change = fix.createChange(null);
        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println(cu.toString());


        // //IProblemFactory pf = new EclipseCompilerImpl(null, null, null).getProblemFactory();
    }
}