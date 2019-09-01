# Quick Fix Without Eclipse

This was a work in progress to enable the running of Eclipse quickfix and similar code correction tools outside of active workspace objects. For large-scale code quality improvement, it is not optimal to be restriced to successive calls on a UI. 

By removing this dependency, quickfix functionality should be able to run even outside of Eclipse. This project exists solely for my own purposes so if I can run these on strings in the background of an eclipse plug-in, I am happy (so this may end up being less Quick Fix without Eclipse, and more Programmatic Quickfix). 

Unfortunately, the workarounds in this project do not actually work in an Eclipse plug-in. The way eclipse loads required bundles means that eclipse.jdt.core packages will be in a seperate classloader from the patched classes. We can't specify eclipse.jdt.core as an external jar that we just overwrite because then the plug-in won't initialize correctly and calls to JavaCore.create() fail (which are needed to get projects). Ultimately using IJavaProject.getPackageFragmentRoots() to find possible packages for an undefined Type took me an half an hour of work vs the week I spent on this to get unused import removal working, and this just doesn't seem like a worthwhile use of time. 

We cried, we learnt some things, I guess. Feel free to do what you want with this, and maybe some of the code here can be useful to someone else.

## Quick Fixes

Removing unused import statements has been successfully implemented. 

## Resouces

Quick Fix Without Eclipse utilizes milestone builds of eclipse packages, the jars are currently provided because I'm lazy and want to be able to run this out of the box on different computers. I believe eclipse also doesn't maintain these milestone packages, so for the sake of making sure I have continual access to these specific versions, they'll stay. If this ever goes anywhere I'll clean the project up.

