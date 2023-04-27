
private String sanitize(String input) {
    return input.replaceAll("[<>:\"/\\\\|?*\u0000-\u001F]|[<>:\"/\\\\|?*\u007F-\uD7FF\uE000-\uFFFD]|[<>:\"/\\\\|?*\u10000-\u10FFFF]", "_");
}

String sanitizedDirectory = sanitize(directoryText.getText());
and
String sanitizedFileName = sanitize(fileNameText.getText());

private boolean isPathAccessible(String path) {
    File file = new File(path);
    return file.exists() && file.canWrite();
}

private void updateState() {
    if (!fileNameEdited) {
        final String sanitizedFileName = sanitize(getArchiveFileName(getProjectsToExport()));
        fileNameText.setText(sanitizedFileName);
    }
    
    if (!isPathAccessible(directoryText.getText())) {
        setMessage("Output directory is not accessible", IMessageProvider.ERROR);
        return;
    }
    
    getContainer().updateButtons();
}
