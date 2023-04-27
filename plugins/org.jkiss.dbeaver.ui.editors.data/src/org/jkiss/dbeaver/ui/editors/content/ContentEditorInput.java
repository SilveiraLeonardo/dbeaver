
private void updateStringValueFromFile(File extFile) throws DBException {
    try (FileReader is = new FileReader(extFile)) {
        String str = IOUtils.readToString(is);
        stringStorage.setString(str);
        valueController.updateValue(str, false);

    } catch (IOException e) {
        throw new DBException("Error reading content from file", e);
    }
}
