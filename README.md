# Quick Fix Without Eclipse

This is a work in progress to enable the running of Eclipse quickfix and similar code correction tools outside of active workspace objects. For large-scale code quality improvement, it is not optimal to be restriced to successive calls on a UI. 

By removing this dependency, quickfix functionality should be able to run even outside of Eclipse. This project exists solely for my own purposes so if I can run these on strings in the background of an eclipse plug-in, I am happy (so this may end up being less Quick Fix without Eclipse, and more Programmatic Quickfix). 

# Quick Fixes

Removing unused import statements has been successfully implemented. 

I would really like to get undefined Types working, or at least some access to how Eclipse finds potentional import statements.

# Current Issues

Getting IProblems has been resolved, for some undisclosed reason the current parser code can be used to extract IProblems were previously this would fail. This means no more hacked-on getProblems() function in ECJ, which 50% of the time raised an error in gradle during build, and 100% of the time raised an error in the Eclipse Plug-in I want to use this in.

Constructing a dummy JavaProject that will allow for finding imports seems difficult, for my own purposes it might just be enough to get a real JavaProject within my plug-in.

I've had to do terrible things to get this much working. Even if this goes no where for me, I hope this code can help someone else! 

## Resouces

Quick Fix Without Eclipse utilizes milestone builds of eclipse packages, the jars are currently provided because I'm lazy and want to be able to run this out of the box on different computers. I believe eclipse also doesn't maintain these milestone packages, so for the sake of making sure I have continual access to these specific versions, they'll stay. If this ever goes anywhere I'll clean the project up.

