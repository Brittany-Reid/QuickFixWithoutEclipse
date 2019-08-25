# Quick Fix Without Eclipse

This is a work in progress to enable the running of quickfix in the background vs only on an open file in Eclipse. The main goal is to detangle Eclipse Quick Fix functionality from the Eclipse IDE. Eclipse Quick Fixes are heavily embedded within UI, and operate on an open file in the workspace. This is not optimal for any large-scale code correction.

A hypothetical implementation of large-scale code correction through an eclipse plug-in would require inserting code into a file in eclipse, running quickfix programatically somehow, then saving the new, corrected code into a String before repeating this process for all code we want to correct.

The proposed idea is instead to run quickfix on non-workspace objects, which should mean being able to call this functionality even outside of eclipse.

## Current Goal
In order to test the viability of this project, the current goal is to be able to run a single quickfix on a file. We must be able to generate the compatible objects for eclipse quickfix through compilation. 

I have successfully been able to extract IProblems from the Eclipse Compiler for Java, which is a crucial first step. Unfortunately the only viable method for this is modifying the eclipse libraries. In the process of modifying EclipseCompilerImpl and EclipseCompiler, I have also removed some code that prevents in-memory compilation.

Looking into a specific fix, it appears that the code for regular quickfix works on workspace objects, and that detangling this would be a large amount of work. However, there is quickfix functionality called from eclipse.jdt.ls, the language server used by other IDE plug-ins that doesn't appear to require workspace objects in a way that can't be avoided. This is the current path of investigation.

Currently in order to use the functionality from eclipse.jdt.ls, I have to create extended CompilationUnit, ICompilationUnit and IFile objects. Some of these objects need to be in the same package as their super classes. It looks like getting this working is going to be extremely hacky. Currently the benefits of getting this working outweigh the downsides of the necessary implementation, but if things get too complicated it's probably less effort to just rewrite a couple of the important fixes based on the eclipse code.

If this refuses to work, maybe the code here can be useful to someone else.

## Resouces

Quick Fix Without Eclipse utilizes milestone builds of eclipse packages, the jars are currently provided because I'm lazy and want to be able to run this out of the box on different computers. I believe eclipse also doesn't maintain these milestone packages, so for the sake of making sure I have continual access to these specific versions, they'll stay. If this ever goes anywhere I'll clean the project up.

