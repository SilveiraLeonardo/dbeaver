
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;

import java.io.*;
import java.net.ServerSocket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class IOUtils {
    // ...

    private static final Logger LOGGER = Logger.getLogger(IOUtils.class.getName());
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close the Closeable resource", e);
        }
    }

    public static void close(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to close the AutoCloseable resource", e);
        }
    }

    // ...

    public static int findFreePort(int minPort, int maxPort) {
        int portRange = Math.abs(maxPort - minPort);
        while (true) {
            int portNum = minPort + SECURE_RANDOM.nextInt(portRange);
            try (ServerSocket socket = new ServerSocket(portNum)) {
                return portNum;
            } catch (IOException e) {
                // Port is busy
            }
        }
    }

    // ...

    public static void extractZipArchive(InputStream stream, Path targetFolder) throws IOException {
        try (ZipInputStream zipStream = new ZipInputStream(stream)) {
            targetFolder = targetFolder.toRealPath();
            for (; ; ) {
                ZipEntry zipEntry = zipStream.getNextEntry();
                if (zipEntry == null) {
                    break;
                }
                try {
                    if (!zipEntry.isDirectory()) {
                        String zipEntryName = zipEntry.getName();
                        checkAndExtractEntry(zipStream, zipEntry, targetFolder);
                    }
                } finally {
                    zipStream.closeEntry();
                }
            }
        }
    }

    // ...

    private static void checkAndExtractEntry(InputStream zipStream, ZipEntry zipEntry, Path targetFolder) throws IOException {
        if (!Files.exists(targetFolder)) {
            try {
                Files.createDirectories(targetFolder);
            } catch (IOException e) {
                throw new IOException("Can't create local cache folder '" + targetFolder.toAbsolutePath() + "'", e);
            }
        }
        Path localFile = targetFolder.resolve(zipEntry.getName()).normalize().toRealPath();
        if (!localFile.startsWith(targetFolder)) {
            throw new IOException("Zip entry is outside of the target directory");
        }
        if (Files.exists(localFile)) {
            // Already extracted?
            return;
        }
        Path localDir = localFile.getParent();
        if (!Files.exists(localDir)) { // in case of localFile located in subdirectory inside zip archive
            try {
                Files.createDirectories(localDir);
            } catch (IOException e) {
                throw new IOException("Can't create local file directory in the cache '" + localDir.toAbsolutePath() + "'", e);
            }
        }
        try (OutputStream os = Files.newOutputStream(localFile)) {
            copyZipStream(zipStream, os);
        }
    }

    // ...
}
