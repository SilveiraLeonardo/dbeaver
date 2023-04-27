
private static DashboardRegistry instance = null;

import javax.xml.parsers.DocumentBuilderFactory;

// ...

private void loadConfigFromFile() throws XMLException, DBException {
    String configContent = DBWorkbench.getPlatform()
        .getPluginConfigurationController(UIDashboardActivator.PLUGIN_ID)
        .loadConfigurationFile(CONFIG_FILE_NAME);

    if (CommonUtils.isNotEmpty(configContent)) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        
        DocumentBuilder builder = dbFactory.newDocumentBuilder();
        Document dbDocument = builder.parse(new InputSource(new StringReader(configContent)));
        for (Element dbElement : XMLUtils.getChildElementList(dbDocument.getDocumentElement(), "dashboard")) {
            DashboardDescriptor dashboard = new DashboardDescriptor(this, dbElement);
            dashboardList.put(dashboard.getId(), dashboard);
        }
    }
}
