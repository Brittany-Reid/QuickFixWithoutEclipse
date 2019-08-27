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
import org.eclipse.jdt.internal.core.util.SimpleDocument;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnitWrapper;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ModuleDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.corext.fix.UnusedCodeFix;
import org.eclipse.jdt.internal.corext.fix.UnusedCodeFixCore;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;
import org.eclipse.jdt.internal.corext.fix.IProposableFix;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.IProposalRelevance;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.CUCorrectionProposal;
import org.eclipse.jdt.internal.corext.refactoring.structure.CompilationUnitRewrite;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.fix.FixMessages;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.text.edits.TextEditProcessor;
import org.eclipse.ltk.core.refactoring.GroupCategorySet;
import org.eclipse.ltk.core.refactoring.GroupCategory;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jdt.core.dom.ICompilationUnitWrapper;
import quickfixwithouteclipse.quickfix.*;

/**
 * Main class
 */

public class Main{

    public static void main(String[] args) throws Exception{

        //this is the string we want to compile
        String code = "import java.util.List;\n"
        //+ "import java.util.ArrayList;\n"
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

        code = QuickFix.run(file);
        
        System.out.println(code);
    }
}