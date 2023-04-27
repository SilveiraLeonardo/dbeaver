
package org.jkiss.dbeaver.ui.resources.bookmarks;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPImage;
import org.jkiss.dbeaver.ui.DBIconBinary;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.Base64;
import org.jkiss.utils.xml.XMLBuilder;
import org.jkiss.fw.String.TextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Bookmark storage
 */
public class BookmarkStorage {

    // ... (other code ommited for brevity)

    public BookmarkStorage(IFile file, boolean loadImage) throws DBException, CoreException
    {
        this.title = file.getFullPath().removeFileExtension().lastSegment();
        try (InputStream contents = file.getContents(true)) {
            // Secure XML parsing
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(contents);
            
            final Element root = document.getDocumentElement();
            // ... (Other code related to reading bookmark attributes)
        } catch (ParserConfigurationException | SAXException e) {
            throw new DBException("Error reading bookmarks storage", e);
        } catch (IOException e) {
            throw new DBException("IO Error reading bookmarks storage", e);
        }
    }

    // ... (remainder of the original code)
}
