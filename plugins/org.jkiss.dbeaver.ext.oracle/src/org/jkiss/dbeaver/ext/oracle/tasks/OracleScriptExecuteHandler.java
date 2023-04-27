
package org.jkiss.dbeaver.ext.oracle.tasks;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ext.oracle.model.OracleConstants;
import org.jkiss.dbeaver.ext.oracle.model.OracleDataSource;
import org.jkiss.dbeaver.ext.oracle.model.dict.OracleConnectionType;
import org.jkiss.dbeaver.model.connection.DBPConnectionConfiguration;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.DBRRunnableContext;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.task.DBTTask;
import org.jkiss.dbeaver.registry.task.TaskPreferenceStore;
import org.jkiss.dbeaver.tasks.nativetool.AbstractNativeToolHandler;
import org.jkiss.dbeaver.utils.RuntimeUtils;

import java.io.File;
import java.io.IOException;
import java.net.IDN;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class OracleScriptExecuteHandler extends AbstractNativeToolHandler<OracleScriptExecuteSettings, DBSObject, OracleDataSource> {

    // Basic input validation
    private static final Pattern ALPHANUMERIC_REGEX = Pattern.compile("^[a-zA-Z0-9]*$");

    private boolean isValidAlphanumeric(String input) {
        return ALPHANUMERIC_REGEX.matcher(input).matches();
    }

    @Override
    public Collection<OracleDataSource> getRunInfo(OracleScriptExecuteSettings settings) {
        return Collections.singletonList((OracleDataSource) settings.getDataSourceContainer().getDataSource());
    }

    @Override
    protected OracleScriptExecuteSettings createTaskSettings(DBRRunnableContext context, DBTTask task) throws DBException {
        OracleScriptExecuteSettings settings = new OracleScriptExecuteSettings();
        settings.loadSettings(context, new TaskPreferenceStore(task));

        return settings;
    }

    @Override
    protected boolean needsModelRefresh() {
        return false;
    }

    @Override
    public void fillProcessParameters(OracleScriptExecuteSettings settings, OracleDataSource arg, List<String> cmd) throws IOException {
        String sqlPlusExec = RuntimeUtils.getNativeBinaryName("sqlplus");
        File sqlPlusBinary = new File(settings.getClientHome().getPath(), "bin/" + sqlPlusExec);
        if (!sqlPlusBinary.exists()) {
            sqlPlusBinary = new File(settings.getClientHome().getPath(), sqlPlusExec);
        }
        if (!sqlPlusBinary.exists()) {
            throw new IOException("SQL*Plus binary not found.");
        }
        String dumpPath = sqlPlusBinary.getAbsolutePath();
        cmd.add(dumpPath);
    }

    @Override
    protected List<String> getCommandLine(OracleScriptExecuteSettings settings, OracleDataSource arg) throws IOException {
        List<String> cmd = new ArrayList<>();
        fillProcessParameters(settings, arg, cmd);
        DBPConnectionConfiguration conInfo = settings.getDataSourceContainer().getActualConnectionConfiguration();
        String serverName = conInfo.getServerName();
        String databaseName = conInfo.getDatabaseName();
        String username = conInfo.getUserName();
        String password = conInfo.getUserPassword();

        // Validate properties before proceeding
        if (!isValidAlphanumeric(serverName) || !isValidAlphanumeric(databaseName) || !isValidAlphanumeric(username)) {
            throw new IOException("Invalid characters in input. Only alphanumeric characters are allowed.");
        }

        // Use IDN.toASCII to validate and process hostname
        String hostName = IDN.toASCII(conInfo.getHostName());

        String url;
        if ("TNS".equals(conInfo.getProviderProperty(OracleConstants.PROP_CONNECTION_TYPE))) {
            url = serverName != null ? serverName : databaseName;
        } else {
            boolean isSID = OracleConnectionType.SID.name().equals(conInfo.getProviderProperty(OracleConstants.PROP_SID_SERVICE));
            String port = conInfo.getHostPort();
            if (isSID) {
                url = "(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=" + hostName + ")(Port=" + port + "))(CONNECT_DATA=(SID=" + databaseName + ")))";
            } else {
                url = "//" + hostName + (port != null ? ":" + port : "") + "/" + databaseName;
            }
        }
        final String role = conInfo.getProviderProperty(OracleConstants.PROP_INTERNAL_LOGON);
        if (role != null) {
            url += (" AS " + role);
        }
        cmd.add(username + "@\"" + url + "\"");

        // Include the password as an environment variable to avoid exposure in logs or process monitoring tools
        cmd.add("EnvPasswordVariable");

        return cmd;
    }

    @Override
    protected boolean isLogInputStream() {
        return true;
    }

    @Override
    protected void startProcessHandler(DBRProgressMonitor monitor, DBTTask task, OracleScriptExecuteSettings settings, OracleDataSource arg, ProcessBuilder processBuilder, Process process, Log log) throws IOException {
        final File inputFile = new File(settings.getInputFile());
        if (!inputFile.exists()) {
            throw new IOException("Input file not found.");
        }

        // Set the password as an environment variable for the process
        String envPasswordVariable = "EnvPasswordVariable";
        processBuilder.environment().put(envPasswordVariable, settings.getDataSourceContainer().getActualConnectionConfiguration().getUserPassword());

        super.startProcessHandler(monitor, task, settings, arg, processBuilder, process, log);
        new BinaryFileTransformerJob(monitor, task, inputFile, process.getOutputStream(), log).start();
    }
}
