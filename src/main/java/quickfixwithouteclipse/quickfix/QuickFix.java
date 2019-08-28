package quickfixwithouteclipse.quickfix;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnitWrapper;
import org.eclipse.jdt.core.dom.ICompilationUnitWrapper;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AST;
import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
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
import org.eclipse.lsp4j.Range;
import org.eclipse.jdt.ls.core.internal.JDTUtils;

/**
 * Public class containing functions to preform quickfix.
 */
public class QuickFix{

    /**
     * This is an example of how to run multiple quickfixes using a given compiler.
     * Successive compiles like this are better suited to in-memory compilation.
     * The compiler needs to be an EclipseCompiler in order to extract
     * IProblems.
     * @return The fixed string.
     */
    public static String fixAll(File code, JavaCompiler compiler){
        int runs = 1;
        if(!(compiler instanceof EclipseCompiler)) return null;
        String contents = null;

        for(int i=0; i<runs; i++){
            contents = readFile(code);
            if(contents == null) return null;

            //parsing removes comments, for now we have to parse before compiling so the offsets dont change
            CompilationUnit cu = constructCompilationUnit(contents);
            contents = cu.toString();

            System.out.println(contents);
            code = writeTo(code, contents);

            //compile
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(code);
            //eclipse compiler does not respect null writer
            Writer out = new OutputStreamWriter(new NullOutputStream());
            CompilationTask task = compiler.getTask(out, fileManager, diagnostics, null, null, compilationUnits);
            task.call();

            //get iproblems
            EclipseCompiler ec = (EclipseCompiler)compiler;
            List<IProblem> problems = ec.getIProblems();
            runs = problems.size();

            String newContents = run(contents, problems);
            if(newContents != null){
                contents = newContents;
                code = writeTo(code, contents);
            }
        }

        return contents;
    }  

    /**
     * Runs quickfix on a given string given a list of IProblems.
     * @param code The String code to fix
     * @param problems A String Array of problems.
     * @return The fixed String.
     * @throws JavaModelException
     */
    public static String run(String code, List<IProblem> problems){
        //construct our compilationunit
        CompilationUnitWrapper compilationUnit = constructCompilationUnit(code);

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
        //settings
        ASTParser parser = ASTParser.newParser(AST.JLS11);
        parser.setResolveBindings(true);
        parser.setStatementsRecovery(true);
        parser.setBindingsRecovery(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setEnvironment(null, null, null, true);
        parser.setSource(code.toCharArray());
        parser.setUnitName(null);
        Map options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
        parser.setCompilerOptions(options);
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
                contents += line +"\n";
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

    private static File writeTo(File file, String contents){
        Writer w;
        try{
            if (!file.exists()) {
                file.createNewFile();
            }

            w = new FileWriter(file);
            w.write(contents);
            w.close();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return file;
    }   
}