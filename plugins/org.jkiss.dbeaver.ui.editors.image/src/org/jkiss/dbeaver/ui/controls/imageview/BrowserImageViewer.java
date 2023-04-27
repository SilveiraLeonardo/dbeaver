
private static final Set<String> SUPPORTED_FORMATS = new HashSet<>(Arrays.asList("jpeg", "jpg", "png", "gif"));

private boolean isImageFormatSupported(String format) {
    String formatLower = format.toLowerCase(Locale.ROOT);
    return SUPPORTED_FORMATS.contains(formatLower);
}

public boolean loadImage(@NotNull InputStream inputStream) {
    boolean success = false;
    try {
        clearTempFile();
        try (ImageInputStream stream = ImageIO.createImageInputStream(inputStream)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                throw new IOException("No Image readers");
            } else {
                ImageReader reader = readers.next();
                reader.setInput(stream);
                String formatName = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!isImageFormatSupported(formatName)) {
                    throw new IOException("Unsupported image format: " + formatName);
                }
                tempFile = Files.createTempFile(DBWorkbench.getPlatform()
                        .getTempFolder(new VoidProgressMonitor(), "dbeaver-images"),
                    "image",
                    "." + formatName
                );
                ImageIO.write(ImageIO.read(stream),
                    formatName,
                    tempFile.toFile()
                );
                success = true;
            }
        } catch (IOException exception) {
            if (!exception.getMessage().equals("closed")) {
                log.error("Error reading image data", exception);
                showBinaryTXT(inputStream);
                success = false;
            }
        }
        URL url = tempFile.toUri().toURL();
        browser.setUrl(url.toString());
    } catch (IOException exception) {
        log.error(exception);
    }
    return success;
}
