package org.eclipse.jdt.core.dom;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.IJavaElement;

/**
 * CompilationUnitWrapper is an extension of Compilation Unit to return
 * a custom ICompilationUnit object.
 */
public class CompilationUnitWrapper extends CompilationUnit{
    ICompilationUnitWrapper icu = null;

    public CompilationUnitWrapper(AST ast){
        super(ast);
        icu = new ICompilationUnitWrapper(null, null, null);
        icu.setSource(this.toString());
    }

    @Override
    public IJavaElement getJavaElement() {
		return icu;
	}

}