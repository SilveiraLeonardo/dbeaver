
package org.jkiss.dbeaver.model.data.storage;

import org.jkiss.dbeaver.model.data.DBDContentStorage;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Stream content storage
 */
public class StreamContentStorage implements DBDContentStorage {

    private final InputStream stream;

    public StreamContentStorage(InputStream stream) {
        this.stream = Objects.requireNonNull(stream, "Stream must not be null.");
    }

    @Override
    public InputStream getContentStream() throws IOException {
        return copyStream(this.stream);
    }

    @Override
    public Reader getContentReader() throws IOException {
        return new InputStreamReader(getContentStream(), getCharset());
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public String getCharset() {
        return GeneralUtils.DEFAULT_ENCODING;
    }

    @Override
    public DBDContentStorage cloneStorage(DBRProgressMonitor monitor) throws IOException {
        return new StreamContentStorage(copyStream(this.stream));
    }

    @Override
    public void release() {
        IOUtils.close(stream);
    }

    private InputStream copyStream(InputStream original) throws IOException {
        byte[] content = IOUtils.readStreamContent(original, StandardCharsets.UTF_8);
        return new ByteArrayInputStream(content);
    }
}
