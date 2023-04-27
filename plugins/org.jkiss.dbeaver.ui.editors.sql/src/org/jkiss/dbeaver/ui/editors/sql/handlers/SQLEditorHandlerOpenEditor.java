
import org.apache.commons.io.FilenameUtils;

private static IFolder getCurrentScriptFolder(ISelection selection) {
    IFolder folder = null;
    if (selection != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
        final Object element = ((IStructuredSelection) selection).getFirstElement();
        if (element instanceof IFolder) {
            folder = (IFolder) element;
        } else if (element instanceof DBNResource && ((DBNResource) element).getResource() instanceof IFolder) {
            folder = (IFolder) ((DBNResource) element).getResource();
        }
    }

    if (folder != null && isValidPath(folder.getLocation().toString())) {
        return folder;
    } else {
        return null;
    }
}

private static boolean isValidPath(String path) {
    // Ensure paths are canonicalized to avoid path traversal attacks
    String canonicalPath = FilenameUtils.normalize(path);
    return canonicalPath != null && canonicalPath.equals(path);
}

public static IFile openNewEditor(@NotNull SQLNavigatorContext editorContext, ISelection selection) throws CoreException {
    DBPProject project = editorContext.getProject();
    checkProjectIsOpen(project);
    IFolder folder = getCurrentScriptFolder(selection);
    if (folder == null) {
        throw new CoreException(GeneralUtils.makeExceptionStatus(new IllegalStateException("Invalid script folder")));
    }
    
    IFile scriptFile = SQLEditorUtils.createNewScript(project, folder, editorContext);

    if (isValidPath(scriptFile.getLocation().toString())) {
        openResource(scriptFile, editorContext);
        return scriptFile;
    } else {
        throw new CoreException(GeneralUtils.makeExceptionStatus(new IllegalStateException("Invalid script file path")));
    }
}
