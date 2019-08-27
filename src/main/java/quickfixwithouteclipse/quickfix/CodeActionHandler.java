package quickfixwithouteclipse.quickfix;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ls.core.internal.corrections.proposals.ChangeCorrectionProposal;
import org.eclipse.core.runtime.CoreException;
import java.util.ArrayList;

/**
 * This class handles code actions. Based on org.eclipse.jdt.ls.core.internal.handlers.codeActionHandler
 * 
 */
public class CodeActionHandler{

    /**
     * Returns a list of proposals.
     * @param locations Problem Locations to handle.
     * @param cu The Compilation Unit to handle.
     * @return A list of ChangeCorrectionProposals.
     */
    public static List<ChangeCorrectionProposal> getProposals(List<ProblemLocation> locations, CompilationUnit cu){
        List<ChangeCorrectionProposal> candidates = new ArrayList<>();
        try {
			List<ChangeCorrectionProposal> corrections = QuickFixProcessor.getCorrections(locations, cu);
			candidates.addAll(corrections);
		} catch (CoreException e) {
            e.printStackTrace();
		}

        return candidates;
    }
}