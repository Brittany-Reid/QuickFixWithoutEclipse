package quickfixwithouteclipse.quickfix;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnitWrapper;
import org.eclipse.jdt.core.dom.ICompilationUnitWrapper;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.CUCorrectionProposal;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.ChangeCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditProcessor;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler.CompilationTask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Iterable;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

/**
 * Public class containing functions to preform quickfix.
 */
public class QuickFix{
    
    /**
     * Runs quickfix on a given File. The File will be compiled on disk.
     * Use the other run function if you have a custom compiler.
     * @param code The File containing code to fix.
     * @return The fixed String.
     */
    public static String run(File code){
        //set up compiler
        EclipseCompiler compiler = new EclipseCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(code);
        CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

        //compile
        task.call();
        //get iproblems
        List<IProblem> problems = compiler.getIProblems();

        //get string
        String contents = readFile(code);
        if(contents == null) return null;

        //defer to other run function
        return run(contents, problems);
    }

    /**
     * Runs quickfix on a given string given a list of IProblems.
     * Allows IProblems to be provided from previous compiles/custom compiler setup.
     * @param code The String code to fix
     * @param problems A String Array of problems.
     * @return The fixed String.
     */
    public static String run(String code, List<IProblem> problems){
        //construct our compilationunit
        CompilationUnitWrapper compilationUnit = constructCompilationUnit(code);

        //get a list of problemLocations
        List<ProblemLocation> locations = new ArrayList<>();
        for(IProblem problem : problems){
            locations.add(new ProblemLocation(problem));
        }

        List<ChangeCorrectionProposal> proposals = CodeActionHandler.getProposals(locations, compilationUnit);

        for(ChangeCorrectionProposal proposal : proposals){
            IDocument document = applyProposal(proposal);
            code = document.get();
        }

        return code;
    }

    /**
     *  Constructs a CompilationUnitWrapper using the Eclipse ASTParser.
     */
    private static CompilationUnitWrapper constructCompilationUnit(String code){
        //settings
        ASTParser parser = ASTParser.newParser(AST.JLS11);
        parser.setResolveBindings(true);
        parser.setStatementsRecovery(true);
        parser.setBindingsRecovery(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setEnvironment(null, null, null, true);
        parser.setSource(code.toCharArray());
        parser.setUnitName(null);

        //parse
        ASTNode astNode = parser.createAST(null);

        //convert to compilation unit
        CompilationUnit compilationUnit = (CompilationUnit) astNode;

        //then to our custom object
        CompilationUnitWrapper cuWrapper = new CompilationUnitWrapper(compilationUnit);

        return cuWrapper;
    }

    /**
     * Function to apply a given proposal. Returns the modifed IDocument.
     * @param proposal The Proposal to apply.
     * @return The modified IDocument.
     */
    private static IDocument applyProposal(ChangeCorrectionProposal proposal){
        CompilationUnitChange change;
        try{
            change = (CompilationUnitChange)proposal.getChange();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        TextEdit textEdit = change.getEdit();
        ICompilationUnitWrapper icu = (ICompilationUnitWrapper)change.getCompilationUnit();
        IDocument document = icu.getDocument();

        TextEditProcessor processor = new TextEditProcessor(document, textEdit, TextEdit.NONE);
        try{
            processor.performEdits();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return document;
    }

    /**
     * Private utility function to read a file into a string.
     * @param file The file to read from.
     * @return String contents of the file. Null if there was an exception.
     */
    private static String readFile(File file){
        String contents = "";
        BufferedReader input = null;

        try {
            input = new BufferedReader(new FileReader(file));

            String line = null;
            while((line = input.readLine()) != null){
                contents += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return contents;
    }
}