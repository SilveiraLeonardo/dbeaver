
import org.apache.commons.io.FilenameUtils;

@Nullable
public static IFile getFileFromInput(IEditorInput editorInput) {
    if (editorInput == null) {
        return null;
    } else if (editorInput instanceof IFileEditorInput) {
        return ((IFileEditorInput) editorInput).getFile();
    } else if (editorInput instanceof IPathEditorInput) {
        final IPath path = ((IPathEditorInput) editorInput).getPath();
        final IPath sanitizedPath = path == null ? null : new Path(FilenameUtils.normalizeNoEndSeparator(path.toOSString()));
        return sanitizedPath == null ? null : ResourceUtils.convertPathToWorkspaceFile(sanitizedPath);
    } else if (editorInput instanceof INonPersistentEditorInput) {
        IFile file = (IFile) ((INonPersistentEditorInput) editorInput).getProperty(PROP_INPUT_FILE);
        if (file != null) {
            return file;
        }
    } else if (editorInput instanceof IURIEditorInput) {
        // Most likely it is an external file
        return null;
    }
    // ...
}
