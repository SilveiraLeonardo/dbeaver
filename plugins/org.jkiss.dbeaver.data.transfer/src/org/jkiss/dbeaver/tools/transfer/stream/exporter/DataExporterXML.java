
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.XMLConstants;

// ...

public Document safeParseXML(String xmlString) {
    try {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Prevent XXE attack by disabling external entities
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);

        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        try (StringReader strReader = new StringReader(xmlString)) {
            InputSource inputSrc = new InputSource(strReader);
            Document doc = docBuilder.parse(inputSrc);
            return doc;
        }

    } catch (Exception e) {
        // Handle the exception appropriately
        e.printStackTrace();
    }
    return null;
}
