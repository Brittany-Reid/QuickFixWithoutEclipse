package org.eclipse.jdt.core.dom;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.core.resources.IFile;
import quickfixwithouteclipse.IFileWrapper;

/**
 * An ICompilation Unit that returns a custom IFile.
 */
class ICompilationUnitWrapper extends CompilationUnit{
    IFileWrapper file;

    public ICompilationUnitWrapper(PackageFragment parent, String name, WorkingCopyOwner owner){
        super(parent, name, owner);
    }

    public void setSource(String contents){
        file = new IFileWrapper(contents);
    }

    @Override
    public IFile getResource(){
        return null;
    }
}