package quickfixwithouteclipse;

import org.eclipse.jdt.internal.core.Buffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IOpenable;
import java.lang.Character;

/**
 * Dummy extension of IBuffer/Buffer that is not connected to a workspace.
 */
public class IBufferWrapper extends Buffer{
    IFile file;
    protected char[] contents;

    public IBufferWrapper(IFile file, IOpenable owner, boolean readOnly){
        super(file, owner, readOnly);
        this.file = file;
        contents = file.toString().toCharArray();
    }

    @Override
    public char getChar(int position) {
        if (this.contents == null) return Character.MIN_VALUE;
        return contents[position];
    }

    @Override
    public char[] getCharacters() {
        if (this.contents == null) return null;
        return this.contents;
    }

}