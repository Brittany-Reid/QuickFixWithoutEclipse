package org.eclipse.jdt.core.dom;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.core.resources.IFile;
import quickfixwithouteclipse.IFileWrapper;
import org.eclipse.jdt.core.IBuffer;
import quickfixwithouteclipse.*;
import org.eclipse.jdt.core.IJavaProject;

/**
 * An ICompilation Unit that returns a custom IFile.
 */
class ICompilationUnitWrapper extends CompilationUnit{
    IJavaProject project;
    IFileWrapper file;
    IBuffer buffer;

    public ICompilationUnitWrapper(PackageFragment parent, String name, WorkingCopyOwner owner){
        super(parent, name, owner);
        project = new IJavaProjectWrapper();
    }

    public void setSource(String contents){
        file = new IFileWrapper(contents);
        buffer = new IBufferWrapper(file, null, false);
    }

    @Override
    public IFile getResource(){
        return file;
    }

    @Override
    public IBuffer getBuffer(){
        return buffer;
    }

    @Override
    public IJavaProject getJavaProject(){
        return project;
    }
}