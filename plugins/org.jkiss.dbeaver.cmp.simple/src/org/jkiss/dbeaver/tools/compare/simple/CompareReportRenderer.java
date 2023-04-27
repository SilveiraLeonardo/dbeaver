
package org.jkiss.dbeaver.tools.compare.simple;

import org.apache.commons.text.StringEscapeUtils;
import org.jkiss.dbeaver.model.navigator.DBNDatabaseFolder;
import org.jkiss.dbeaver.model.navigator.DBNDatabaseNode;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;
import org.jkiss.utils.xml.XMLBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class CompareReportRenderer {

    private CompareReport report;
    private CompareObjectsSettings settings;

    public void renderReport(DBRProgressMonitor monitor, CompareReport report, CompareObjectsSettings settings, OutputStream outputStream) throws IOException {
        this.report = report;
        this.settings = settings;
        
        try (XMLBuilder xml = new XMLBuilder(outputStream, GeneralUtils.UTF8_ENCODING, true)) {
            xml.setButify(true);
            xml.addContent(
                    "<!DOCTYPE html \n" +
                    "     PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" +
                    "    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");

            // ...rest of the code...

            xml.flush();
    }

    // ...other methods...

    private void renderBody(DBRProgressMonitor monitor) throws IOException {
        // ...other parts of the method...

        for (int k = 0; k < objectCount; k++) {
            xml.startElement("td");
            String stringValue = "";
            if (reportProperty.values[k] != null) {
                stringValue = StringEscapeUtils.escapeXml10(reportProperty.values[k].toString());
            }
            if (CommonUtils.isEmpty(stringValue)) {
                xml.addText("&nbsp;", false);
            } else {
                xml.addText(stringValue);
            }

            xml.endElement();
        }

        // ...rest of the method...
    }
}
