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

/**
 * Main class
 */

public class Main{

    public static void main(String[] args) throws Exception{

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
        CompilationUnit c = (CompilationUnit) a;
        CompilationUnitWrapper cu = new CompilationUnitWrapper(a.getAST());

        //code from compilationunit.clone
        cu.setSourceRange(c.getStartPosition(), c.getLength());
        cu.setModule((ModuleDeclaration) ASTNode.copySubtree(a.getAST(), c.getModule()));
        cu.setPackage((PackageDeclaration) ASTNode.copySubtree(a.getAST(), c.getPackage()));
		cu.imports().addAll(ASTNode.copySubtrees(a.getAST(), c.imports()));
        cu.types().addAll(ASTNode.copySubtrees(a.getAST(), c.types()));
        //CompilationUnitWrapper cuw = new CompilationUnitWrapper(cu);
        //ICompilationUnit icu = (ICompilationUnit) cu.getJavaElement();

        //update ifile now we have cloned the compunit
        cu.updateSource();


        //convert to problem location
        ProblemLocation problem = new ProblemLocation(p);

        //cannot use unusedcodefix as it relies on making changes to ui objects
        //UnusedCodeFix.createRemoveUnusedImportFix(cu, problem);
        //this is the code utilized in eclipse.jdt.ls, for incorperating within other ides
        IProposableFix fix = UnusedCodeFixCore.createRemoveUnusedImportFix(cu, problem);

        //lets check all the code here works
        ASTNode s= problem.getCoveringNode(cu);
        //ImportDeclaration node= getImportDeclaration(problem, cu);
        System.out.println(s.toString());
        ASTNode node= ASTNodes.getParent(s, ASTNode.IMPORT_DECLARATION);
        ImportDeclaration im = (ImportDeclaration)node;


        //this is the createchange code
        CompilationUnitRewrite cuRewrite= new CompilationUnitRewrite((ICompilationUnit)cu.getJavaElement(), cu);
        String label = FixMessages.UnusedCodeFix_RemoveImport_description;
        TextEditGroup group = cuRewrite.createCategorizedGroupDescription(label, new GroupCategorySet(new GroupCategory(label, label, label)));
        ASTRewrite fRewrite = ASTRewrite.create(cuRewrite.getRoot().getAST());
        
        fRewrite.remove(im, group);
        // String fDisplayString = fix.getDisplayString();
        // CompilationUnitChange cuChange= new CompilationUnitChange(fDisplayString, cuRewrite.getCu());
        // MultiTextEdit multiEdit= new MultiTextEdit();
        // cuChange.setEdit(multiEdit);
        // TextEdit rewriteEdit;
        // rewriteEdit= fRewrite.rewriteAST();

        try{
            if(fix == null) System.out.println("yeet");
            CompilationUnitChange change = fix.createChange(null);
            //System.out.println(change.getCompilationUnit().getSource());
            CUCorrectionProposal proposal = new CUCorrectionProposal(change.getName(), CodeActionKind.QuickFix, change.getCompilationUnit(), change, IProposalRelevance.REMOVE_UNUSED_IMPORT);
            //proposal.getTextChange();
            //System.out.println(change.getEdit().getChildren().length);
            applyProposal(proposal);
        }catch(Exception e){
            e.printStackTrace();
        }

        //System.out.println(cu.toString());


        // //IProblemFactory pf = new EclipseCompilerImpl(null, null, null).getProblemFactory();
    }

    public static void applyProposal(CUCorrectionProposal proposal){
        CompilationUnitChange change;
        try{
            change = (CompilationUnitChange)proposal.getChange();
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        TextEdit textEdit = change.getEdit();
        ICompilationUnitWrapper icu = (ICompilationUnitWrapper)change.getCompilationUnit();
        IDocument document = icu.getDocument();

        TextEditProcessor processor = new TextEditProcessor(document, textEdit, TextEdit.NONE);
        try{
            processor.performEdits();
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        System.out.println(document.get());
        //TextEditWrapper.traverseUpdate(textEdit, document);

    }


}