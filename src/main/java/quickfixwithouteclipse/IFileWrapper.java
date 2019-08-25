package quickfixwithouteclipse;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.internal.resources.Workspace;
import java.io.InputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.app.EclipseAppContainer;
import org.eclipse.core.runtime.CoreException;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.internal.utils.Policy;
import java.nio.charset.StandardCharsets;
import org.eclipse.core.runtime.Path;

/**
 * An IFileWrapper is an extension of IFile that is actually just a string.
 */
public class IFileWrapper extends File{
    String contents;

    protected IFileWrapper(IPath path, Workspace container) {
		super(path, container);
    }
    
    public IFileWrapper(String contents){
        super(new Path(""), null);
        this.contents = contents;
    }

    @Override
	public void appendContents(InputStream content, int updateFlags, IProgressMonitor monitor) throws CoreException {
        monitor = Policy.monitorFor(monitor);
        try{
            this.contents += IOUtils.toString(content, StandardCharsets.UTF_8);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}