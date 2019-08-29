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

    /**
     * Default constructor, creates an empty ICompilationUnit. 
     * Use the copy constructor instead.
     * @param ast
     */
    public CompilationUnitWrapper(AST ast){
        super(ast);
        icu = new ICompilationUnitWrapper(null, null, null);
    }

    /**
     * Copy Constructor
     */
    public CompilationUnitWrapper(CompilationUnit original, String source){
        this(original.getAST());
        copy(original, source);
    }

    /**
     * Function to copy from a CompilationUnit.
     * Copied from CompilationUnit.clone0
     * @param original The original CompilationUnit to copy.
     * @param source The string source, so we can maintain comments.
     */
    public void copy(CompilationUnit original, String source){
        setSourceRange(original.getStartPosition(), original.getLength());
        setModule((ModuleDeclaration) ASTNode.copySubtree(original.getAST(), original.getModule()));
        setPackage((PackageDeclaration) ASTNode.copySubtree(original.getAST(), original.getPackage()));
		imports().addAll(ASTNode.copySubtrees(original.getAST(), original.imports()));
        types().addAll(ASTNode.copySubtrees(original.getAST(), original.types()));
        setProblems(original.getProblems());
        updateSource(source); //now the ast has been copied, update all source connections
    }

    public void updateSource(String source){
        icu.setSource(source);
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