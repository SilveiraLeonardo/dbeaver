
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// Other imports...

private void importNCX(ImportData importData, ImportDriverInfo driver, Reader reader) throws XMLException
{
    // Create a DocumentBuilderFactory and disable loading external entities
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

    try {
        // Create a DocumentBuilder and parse the document
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(new InputSource(reader));

        for (Element conElement : XMLUtils.getChildElementList(document.getDocumentElement())) {
            Map<String, String> conProps = new HashMap<>();
            NamedNodeMap attrs = conElement.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Attr attr = (Attr) attrs.item(i);
                conProps.put(attr.getName(), attr.getValue());
            }
            makeConnectionFromProps(importData, driver, conProps);
        }
    } catch (Exception e) {
        throw new XMLException("Error parsing document: " + e.getMessage(), e);
    }
}

private String decryptPassword(String encryptedPassword)
{
    try {
        return decryptor.decrypt(encryptedPassword);
    } catch (Exception e) {
        return null;
    }
}

private void makeConnectionFromProps(ImportData importData, ImportDriverInfo driver, Map<String, String> conProps)
{
    String name = conProps.get("ConnectionName");
    String password = decryptPassword(conProps.get("Password"));

    if (CommonUtils.isEmpty(name) || password == null) {
        return;
    }
    importData.addConnection(new ImportConnectionInfo(
        driver, 
        name, 
        name, 
        "",
        conProps.get("Host"), 
        conProps.get("Port"), 
        conProps.get("Database"), 
        conProps.get("UserName"), 
        password
     ));
}
