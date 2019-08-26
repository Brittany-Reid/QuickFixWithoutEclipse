package quickfixwithouteclipse;

import org.eclipse.jdt.internal.core.JavaProject;
import java.util.Map;
import org.eclipse.jdt.core.JavaCore;

public class IJavaProjectWrapper extends JavaProject{

    public IJavaProjectWrapper(){
        super();
    }

    @Override
    public Map<String, String> getOptions(boolean inheritJavaCoreOptions) {
        Map<String, String> options = JavaCore.getOptions();
        return options;
    }

}