package quickfixwithouteclipse;

import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jface.text.IDocument;

public final class TextEditWrapper extends MultiTextEdit{
    public TextEditWrapper(int offset, int length) {
		super(offset, length);
    }
    
   
    public static void traverseUpdate(TextEdit edit, IDocument document){
        TextEdit[] children = edit.getChildren();
        if (children != null) {
            for (int i= children.length - 1; i >= 0; i--) {
                TextEdit child = children[i];
            }
        }
    }
    
}