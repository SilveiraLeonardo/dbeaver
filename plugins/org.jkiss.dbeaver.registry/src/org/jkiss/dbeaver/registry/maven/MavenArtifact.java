
package org.jkiss.dbeaver.registry.maven;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.registry.VersionUtils;
import org.jkiss.dbeaver.registry.maven.versioning.DefaultArtifactVersion;
import org.jkiss.dbeaver.registry.maven.versioning.VersionRange;
import org.jkiss.dbeaver.runtime.WebUtils;
import org.jkiss.utils.CommonUtils;
import org.jkiss.utils.IOUtils;
import org.jkiss.utils.xml.SAXListener;
import org.jkiss.utils.xml.XMLException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ... Rest of the code ...

private void parseMetadata(InputStream mdStream) throws IOException, XMLException {
    try {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        
        // Disable external entities and external DTDs
        xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        // Create a new SAXListener and set it as the content handler
        SAXListener saxListener = new SAXListener() {
            // ... Existing SAXListener implementation ...
        };
        xmlReader.setContentHandler(saxListener);

        // Parse the input stream
        xmlReader.parse(new InputSource(mdStream));
    } catch (SAXException e) {
        log.warn("Error configuring XML parser for security", e);
        throw new XMLException("Unable to configure XML parser.", e);
    }
}

// ... Rest of the code ...
