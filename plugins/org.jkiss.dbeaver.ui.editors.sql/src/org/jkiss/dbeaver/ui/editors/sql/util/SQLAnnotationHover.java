
private void findAnnotations(int offset, IAnnotationModel model, IDocument document, int lineNumber) {
    annotations.clear();
    if (model == null) {
        if (editor != null) {
            ITextEditor editor = this.editor;
            model = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
        }
    }
    if (model == null) {
        return;
    }
    try {
        for (Iterator<?> it = model.getAnnotationIterator(); it.hasNext();) {
            Annotation annotation = (Annotation) it.next();
            Position position = model.getPosition(annotation);

            //if position is null, just return.
            if (position == null) {
                return;
            }
            try {
                if (position.overlapsWith(offset, 1) || document != null
                    && document.getLineOfOffset(position.offset) == lineNumber) {
                    annotations.add(annotation);
                }
            } catch (BadLocationException e) {
                log.error(e);
            }
        }
    } finally {
        annotations.clear();
    }
}
