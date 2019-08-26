package org.eclipse.jdt.core.dom;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;

/**
 * CompilationUnitWrapper is an extension of Compilation Unit to return
 * a custom ICompilationUnit object.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CompilationUnitWrapper extends CompilationUnit{
    ICompilationUnitWrapper icu = null;
    CompilationUnit inner;

    public CompilationUnitWrapper(AST ast){
        super(ast);
        icu = new ICompilationUnitWrapper(null, null, null);
    }

    public CompilationUnitWrapper(CompilationUnit cu){
        super(cu.getAST());
        inner = cu;
        icu = new ICompilationUnitWrapper(null, null, null);
        icu.setSource(inner.toString());
    }

    public void updateSource(){
        icu.setSource(toString());
    }

    @Override
    public IJavaElement getJavaElement() {
		return icu;
    }

    @Override
    public ITypeRoot getTypeRoot(){
        return icu;
    }

}