
package org.jkiss.dbeaver.registry.maven;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.runtime.IVariableResolver;
import org.jkiss.dbeaver.runtime.WebUtils;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.dbeaver.utils.RuntimeUtils;
import org.jkiss.utils.CommonUtils;
import org.jkiss.utils.IOUtils;
import org.jkiss.utils.StandardConstants;
import org.jkiss.utils.xml.XMLException;
import org.jkiss.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Maven artifact version descriptor (POM).
 */
public class MavenArtifactVersion implements IMavenIdentifier {
    private static final Log log = Log.getLog(MavenArtifactVersion.class);

    // ... [Code remains the same, removed for brevity]

    private void cachePOM(File localPOM) throws IOException {
        if (artifact.getRepository().getType() == MavenRepository.RepositoryType.LOCAL) {
            return;
        }
        String pomURL = getRemotePOMLocation();
        try (InputStream is = WebUtils.openConnection(pomURL, artifact.getRepository().getAuthInfo(), null).getInputStream();
             OutputStream os = new FileOutputStream(localPOM)) {
            File folder = localPOM.getParentFile();
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IOException("Can't create cache folder '" + folder.getAbsolutePath() + "'");
            }
            IOUtils.fastCopy(is, os);
        }
    }

    // ... [Code remains the same, removed for brevity]

    private File getLocalPOM() {
        if (artifact.getRepository().getType() == MavenRepository.RepositoryType.LOCAL) {
            try {
                return new File(GeneralUtils.makeURIFromFilePath(getRemotePOMLocation()));
            } catch (URISyntaxException e) {
                log.warn(e);
            }
        }
        return artifact.getRepository().getLocalCacheDir().resolve(
            artifact.getGroupId() + "/" + artifact.getVersionFileName(version, MavenArtifact.FILE_POM)).toFile();
    }

    private String getRemotePOMLocation() {
        return artifact.getFileURL(version, MavenArtifact.FILE_POM);
    }

    // ... [Other methods remain the same, removed for brevity]
}
