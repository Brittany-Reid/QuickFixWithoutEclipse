package quickfixwithouteclipse.quickfix;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnitWrapper;
import org.eclipse.jdt.core.dom.ICompilationUnitWrapper;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AST;
import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.CUCorrectionProposal;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.ChangeCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditProcessor;
import org.eclipse.jdt.ls.core.internal.corrections.DiagnosticsHelper;
import org.eclipse.jdt.ls.core.internal.corrections.InnovationContext;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaCompiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.Iterable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.lsp4j.Range;
import org.eclipse.jdt.ls.core.internal.JDTUtils;

/**
 * Public class containing functions to preform quickfix.
 */
public class QuickFix{
    public static String classpath = "";
    public static String fileName = "default.java";

    /**
     * Attempt to fix all errors.
     * @return The fixed string.
     */
    public static String fixAll(String code){
        int runs = 1;
        boolean setRuns = false;

        //parsing removes comments, so lets pre-parse
        //even using the parser to create iproblems, this persists
        CompilationUnit cu;
        //CompilationUnit cu = constructCompilationUnit(code);
        //code = cu.toString();

        for(int i=0; i<runs; i++){

            System.out.println(code);

            //construct our ast
            cu = constructCompilationUnit(code);

            //get iproblems from parser
            List<IProblem> problems = Arrays.asList(cu.getProblems());
            
            //on first run, set the number of runs
            if(!setRuns){
                setRuns = true;
                runs = problems.size();
            }

            //attempt to get a correction
            String newContents = run(code, problems, cu);
            if(newContents != null){
                code = newContents;
            }
        }

        return code;
    }  

    /**
     * Runs quickfix on a given string given a list of IProblems.
     * @param code The String code to fix
     * @param problems A String Array of problems.
     * @return The fixed String.
     * @throws JavaModelException
     */
    public static String run(String code, List<IProblem> problems, CompilationUnit compilationUnit){

        code = processProblem(problems.get(0), compilationUnit);

        return code;
    }

    public static String processProblem(IProblem problem, CompilationUnit compilationUnit){
        List<ProblemLocation> locations = new ArrayList<>();
        locations.add(new ProblemLocation(problem));

        InnovationContext context = null;
        try{
            Range range = JDTUtils.toRange((IOpenable)compilationUnit.getJavaElement(), problem.getSourceStart(), 0);
            int start = DiagnosticsHelper.getStartOffset((ICompilationUnit) compilationUnit.getJavaElement(), range);
            int end = DiagnosticsHelper.getEndOffset((ICompilationUnit) compilationUnit.getJavaElement(), range);
            context = new InnovationContext((ICompilationUnit) compilationUnit.getJavaElement(), start, end - start);
            context.setASTRoot(compilationUnit);
        } catch(Exception e){
            e.printStackTrace();
        }
        List<ChangeCorrectionProposal> proposals = CodeActionHandler.getProposals(locations, context);
        if(proposals == null || proposals.size() < 1) return null;
        IDocument document = applyProposal(proposals.get(0));
        return document.get();
    }

    /**
     *  Constructs a CompilationUnitWrapper using the Eclipse ASTParser.
     */
    private static CompilationUnitWrapper constructCompilationUnit(String code){
        //set up our parser
        ASTParser parser = ASTParser.newParser(AST.JLS11);
        parser.setSource(code.toCharArray());
        Map<String, String> options = JavaCore.getOptions();
        options.put("org.eclipse.jdt.core.compiler.source", "1.11");
        parser.setCompilerOptions(options);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setUnitName(fileName);
        parser.setEnvironment(new String[] { classpath != null? classpath : "" },
                new String[] { "" }, new String[] { "UTF-8" }, true);
        parser.setResolveBindings(true);
        CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
        CompilationUnitWrapper cuWrapper = new CompilationUnitWrapper(compilationUnit, code);
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
}