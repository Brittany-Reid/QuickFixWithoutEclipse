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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.util.SimpleDocument;
import org.eclipse.jface.text.IDocument;

/**
 * An ICompilation Unit that returns a custom IFile.
 */
public class ICompilationUnitWrapper extends CompilationUnit{
    IJavaProject project;
    IFileWrapper file;
    IBuffer buffer;
    boolean workingCopy = false;
    SimpleDocument doc;

    public ICompilationUnitWrapper(PackageFragment parent, String name, WorkingCopyOwner owner){
        super(parent, name, owner);
        project = new IJavaProjectWrapper();
    }

    public void setSource(String contents){
        file = new IFileWrapper(contents);
        buffer = new IBufferWrapper(file, null, false);
        doc = new SimpleDocument(contents);
    }

    @Override
    public void becomeWorkingCopy(IProgressMonitor monitor) throws JavaModelException {
       workingCopy = true;
    }

    public boolean isWorkingCopy() {
        return workingCopy;
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

    public IDocument getDocument(){
        return doc;
    }

    @Override
    public String toString(){
        return file.toString();
    }
}