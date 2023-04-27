
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.Display;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ui.SimpleByteArrayTransfer;
import org.jkiss.dbeaver.utils.ContentUtils;
import org.jkiss.utils.StandardConstants;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A clipboard for binary content. Data up to 4Mbytes is made available as text as well
 *
 * @author Jordi
 */
public class BinaryClipboard {

    private static final Log log = Log.getLog(HexEditControl.class);

    static class FileByteArrayTransfer extends ByteArrayTransfer {

        static final String FORMAT_NAME = "BinaryFileByteArrayTypeName";
        static final int FORMAT_ID = registerType(FORMAT_NAME);

        static final FileByteArrayTransfer instance = new FileByteArrayTransfer();

        private FileByteArrayTransfer()
        {
        }

        static FileByteArrayTransfer getInstance()
        {
            return instance;
        }

        @Override
        public void javaToNative(Object object, TransferData transferData)
        {
            if (object == null || !(object instanceof File)) return;

            if (isSupportedType(transferData)) {
                File myType = (File) object;
                try {
                    // write data to a byte array and then ask super to convert to pMedium
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    DataOutputStream writeOut = new DataOutputStream(out);
                    byte[] buffer = myType.getAbsolutePath().getBytes(StandardCharsets.UTF_8);
                    writeOut.writeInt(buffer.length);
                    writeOut.write(buffer);
                    buffer = out.toByteArray();
                    writeOut.close();

                    super.javaToNative(buffer, transferData);

                }
                catch (IOException e) {
                    log.warn(e);
                }  // copy nothing then
            }
        }

        @Override
        public Object nativeToJava(TransferData transferData)
        {
            if (!isSupportedType(transferData)) return null;

            byte[] buffer = (byte[]) super.nativeToJava(transferData);
            if (buffer == null) {
                return null;
            }

            DataInputStream readIn = new DataInputStream(new ByteArrayInputStream(buffer));
            try {
                int size = readIn.readInt();
                if (size <= 0) {
                    return null;
                }
                byte[] nameBytes = new byte[size];
                if (readIn.read(nameBytes) < size){
                    return null;
                }
                return new File(new String(nameBytes, StandardCharsets.UTF_8));
            }
            catch (IOException ex) {
                log.warn(ex);
                return null;
            }
        }

        @Override
        protected String[] getTypeNames()
        {
            return new String[]{FORMAT_NAME};
        }

        @Override
        protected int[] getTypeIds()
        {
            return new int[]{FORMAT_ID};
        }
    }

    private static final File clipboardDir = new File(System.getProperty(StandardConstants.ENV_TMP_DIR, "."));
    private static final File clipboardFile = new File(clipboardDir, "dbeaver-binary-clipboard.tmp");
    private static final long maxClipboardDataInMemory = 4 * 1024 * 1024;  // 4 Megs for byte[], 4 Megs for text

    private final Map<File, Integer> filesReferencesCounter = new HashMap<>();
    private final Clipboard clipboard;

    /**
     * Init system resources for the clipboard
     */
    public BinaryClipboard(Display aDisplay)
    {
        clipboard = new Clipboard(aDisplay);
    }

    // ...

    /**
     * Set the clipboard contents with a BinaryContent
     */
    public void setContents(BinaryContent content, long start, long length)
    {
        if (length < 1L) return;

        Object[] data;
        Transfer[] transfers;
        try {
            if (length <= maxClipboardDataInMemory) {
                byte[] byteArrayData = new byte[(int) length];
                content.get(ByteBuffer.wrap(byteArrayData), start);
                String textData = new String(byteArrayData, StandardCharsets.UTF_8);
                transfers =
                    new Transfer[]{SimpleByteArrayTransfer.getInstance(), TextTransfer.getInstance()};
                data = new Object[]{byteArrayData, textData};
            } else {
                content.get(clipboardFile, start, length);
                transfers = new Transfer[]{FileByteArrayTransfer.getInstance()};
                data = new Object[]{clipboardFile};
            }
        }
        catch (IOException e) {
            clipboard.setContents(new Object[]{new byte[1]},
                                    new Transfer[]{SimpleByteArrayTransfer.getInstance()});
            clipboard.clearContents();
            emptyClipboardFile();
            return;  // copy nothing then
        }
        clipboard.setContents(data, transfers);
    }

    // ...

    long tryGettingMemoryByteArray(BinaryContent content, long start, boolean insert)
    {
        byte[] byteArray = (byte[]) clipboard.getContents(SimpleByteArrayTransfer.getInstance());
        if (byteArray == null) {
            String text = (String) clipboard.getContents(TextTransfer.getInstance());
            if (text != null) {
                byteArray = text.getBytes(StandardCharsets.UTF_8);
            }
        }
        if (byteArray == null)
            return -1L;

        long total = byteArray.length;
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        if (insert) {
            content.insert(buffer, start);
        } else if (total <= content.length() - start) {
            content.overwrite(buffer, start);
        } else {
            total = 0L;
        }

        return total;
    }

    // ...
}
