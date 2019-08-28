package quickfixwithouteclipse.quickfix;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jdt.internal.corext.fix.IProposableFix;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.CUCorrectionProposal;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.ChangeCorrectionProposal;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.UnresolvedElementsSubProcessor;
import org.eclipse.jdt.internal.ui.text.correction.IProposalRelevance;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.jdt.internal.corext.fix.UnusedCodeFixCore;
import org.eclipse.jdt.core.compiler.IProblem;
import java.util.ArrayList;
import java.util.Collections;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ls.core.internal.corrections.InnovationContext;

/**
 * Handles QuickFixes, based on org.eclipse.jdt.ls.core.internal.corrections.QuickFixProcessor.
 */
public class QuickFixProcessor{
    public static List<ChangeCorrectionProposal> getCorrections(List<ProblemLocation> locations, InnovationContext cu) throws CoreException {
        //problems exist
        if (locations == null || locations.size() == 0) {
			return Collections.emptyList();
        }

        List<ChangeCorrectionProposal> results = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            ProblemLocation problem = locations.get(i);
            process(cu, problem, results);
        }
        
        return results;
    }

    /**
     * Function to process current working quickfixes.
     */
    private static void process(InnovationContext cu, ProblemLocation problem, List<ChangeCorrectionProposal> proposals) throws CoreException{
        int id = problem.getProblemId();
        if (id == 0) return;
        switch (id) {
            case IProblem.UndefinedType:
                UnresolvedElementsSubProcessor.getTypeProposals(cu, problem, proposals);
                break;
            case IProblem.UnusedImport:
                // bug with getting import declarations, second dec returns the entire statment?
                IProposableFix fix = UnusedCodeFixCore.createRemoveUnusedImportFix(cu.getASTRoot(), problem);
                //ASTNode selected = problem.getCoveredNode(cu.getASTRoot());
                //System.out.println(selected.toString());
                CompilationUnitChange change = null;
                try{
                    change = fix.createChange(null);
                } catch(CoreException e){
                    //
                }
                CUCorrectionProposal proposal = new CUCorrectionProposal(change.getName(), CodeActionKind.QuickFix, change.getCompilationUnit(), change, IProposalRelevance.REMOVE_UNUSED_IMPORT);
                proposals.add(proposal);
                break;
            default:
                //non handled
                break;
        }
    }
}