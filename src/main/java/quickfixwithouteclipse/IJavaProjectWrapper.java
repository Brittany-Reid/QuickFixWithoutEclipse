package quickfixwithouteclipse;

import org.eclipse.jdt.internal.core.JavaProject;
import java.util.Map;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class IJavaProjectWrapper extends JavaProject{

    public IJavaProjectWrapper(){
        super();
    }

    @Override
    public Map<String, String> getOptions(boolean inheritJavaCoreOptions) {
        Map<String, String> options = JavaCore.getOptions();
        return options;
    }

    @Override
    public String getOption(String optionName, boolean inheritJavaCoreOptions){
        return JavaCore.getOption(JavaCore.COMPILER_SOURCE);
    }

    @Override
    public IClasspathEntry[] getResolvedClasspath() throws JavaModelException {
        return new IClasspathEntry[0];
    }

    @Override
    public IClasspathEntry[] getResolvedClasspath(boolean ignoreUnresolvedEntry) throws JavaModelException {
        return new IClasspathEntry[0];
    }

}