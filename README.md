# Quick Fix Without Eclipse

This is a work in progress to enable the running of quickfix in the background vs only on an open file in Eclipse. The main goal is to detangle Eclipse Quick Fix functionality from the Eclipse IDE. Eclipse Quick Fixes are heavily embedded within UI, and operate on an open file in the workspace. For efficient large-scale code correction, we need to be able to run quick fix on Files or Strings.

## Current Goal
Running a single quickfix on a file. 

I have successfully been able to extract IProblems from the Eclipse Compiler for Java, which is a crucial first step. Unfortunately the only viable method for this is modifying the eclipse libraries. In the process of modifying EclipseCompilerImpl and EclipseCompiler, I have also removed some code that prevents in-memory compilation.

If this refuses to work, maybe the code here can be useful to someone else.

##Run instructions:

Currently ecjExtended.jar has no changes, for some reason the overwriting classes aren't giving me a security error like they did the first time I tried this so I've just been running with them loose. You may have to build twice.