
private void readIncludedTemplates(
    String contributorId,
    Collection<TemplatePersistenceData> templates,
    String file,
    String translations) throws IOException {
    if (file != null) {
        Bundle plugin = Platform.getBundle(contributorId);
        URL url = FileLocator.find(plugin, org.eclipse.core.runtime.Path.fromOSString(file), null);
        if (url != null) {
            ResourceBundle bundle = null;
            if (translations != null) {
                URL bundleURL = FileLocator.find(plugin, org.eclipse.core.runtime.Path.fromOSString(translations), null);
                if (bundleURL != null) {
                    try (InputStream bundleStream = bundleURL.openStream()) {
                        bundle = new PropertyResourceBundle(bundleStream);
                    }
                }
            }
            try (InputStream stream = new BufferedInputStream(url.openStream())) {
                TemplateReaderWriter reader = new TemplateReaderWriter();
                TemplatePersistenceData[] datas = reader.read(stream, bundle);
                for (TemplatePersistenceData data : datas) {
                    if (data.isCustom()) {
                        if (data.getId() == null)
                            log.error("No template id specified");
                        else
                            log.error("Template " + data.getTemplate().getName() + " deleted");
                    } else if (validateTemplate(data.getTemplate())) {
                        templates.add(data);
                    }
                }
            }
        }
    }
}
