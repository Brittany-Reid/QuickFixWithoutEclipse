package quickfixwrapper;

import java.io.PrintWriter;
import java.util.Map;

import javax.tools.JavaFileObject;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl;

/**
**/

class EclipseCompilerImplWrapper extends EclipseCompilerImpl{
    EclipseCompilerImpl inner;
    Iterable<? extends JavaFileObject> compilationUnits;

    /**
     * Constructor, calls super.
     */
    EclipseCompilerImplWrapper(PrintWriter out, PrintWriter err, boolean systemExitWhenFinished){
        super(out, err, systemExitWhenFinished);
        inner = new EclipseCompilerImpl(out, err, systemExitWhenFinished);
    }

    /**
     * EclipseCompilerWrapper requires access to this protected function.
     */
    @Override
	protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress) {
		super.initialize(outWriter, errWriter, systemExit, customDefaultOptions, compilationProgress);;
    }
    
    public Iterable<? extends JavaFileObject> getCompilationUnit(){
        return compilationUnits;
    }
}