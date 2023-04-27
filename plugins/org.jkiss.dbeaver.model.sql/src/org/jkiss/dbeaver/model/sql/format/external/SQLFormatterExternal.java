
import org.apache.commons.exec.*;
import java.util.HashSet;
import java.util.Set;

public class SQLFormatterExternal implements SQLFormatter {
    // ...
    
    public SQLFormatterExternal() {
        allowedFormatters.add("formatter1");
        allowedFormatters.add("formatter2");
        // ...
    }

    private final Set<String> allowedFormatters = new HashSet<>();

    private boolean isCommandAllowed(String command) {
        for (String allowedCommand : allowedFormatters) {
            if (command.startsWith(allowedCommand)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String format(String source, SQLFormatterConfiguration configuration) {
        // ...
        if (CommonUtils.isEmpty(command) || !isCommandAllowed(command)) {
            // Nothing to format or invalid formatter command
            return source;
        }
        
        // ...

        try {
            final FormatJob formatJob = new FormatJob(configuration, command, source, useFile);
            // ...
        // ...
    }

    private static class FormatJob extends AbstractJob {
        // ...

        @Override
        protected IStatus run(DBRProgressMonitor monitor) {
            // ...

            try {
                if (CommonUtils.isEmpty(command) || !isCommandAllowed(command)) {
                    throw new IOException("Invalid command specified for external formatter");
                }

                // ...
                DefaultExecutor executor = new DefaultExecutor();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
                CommandLine commandLine = CommandLine.parse(command);
                executor.setStreamHandler(streamHandler);

                if (tmpFile != null) {
                    commandLine.addArgument(tmpFile.getAbsolutePath());
                }

                try {
                    int exitValue = executor.execute(commandLine);
                    result = outputStream.toString(sourceEncoding);
                } catch (IOException e) {
                    // Handle exception during command execution
                }

            } catch (Exception e) {
                // ...
            } finally {
                // ...
            }
            finished = true;
            return Status.OK_STATUS;
        }

        void stop() {
            // ...
        }

    }
}
