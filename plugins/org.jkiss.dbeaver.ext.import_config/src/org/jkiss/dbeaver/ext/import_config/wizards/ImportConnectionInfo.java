
package org.jkiss.dbeaver.ext.import_config.wizards;

import org.jkiss.dbeaver.registry.driver.DriverDescriptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ImportConnectionInfo {

    private DriverDescriptor driver;
    private ImportDriverInfo driverInfo;
    private String id;
    private String alias;
    private String url;
    private String host;
    private String port;
    private String database;
    private String user;
    private char[] password;
    private Map<String, String> properties = new HashMap<>();
    private Map<String, String> providerProperties = new HashMap<>();
    private boolean checked = false;

    @Override
    public String toString() {
        // Do not include password in the toString output
        StringBuilder sb = new StringBuilder();
        sb.append("alias:").append(alias);
        if (url != null) {
            sb.append(" url:").append(url);
        } else {
            sb.append(" host:").append(host);
            sb.append(" port:").append(port);
            sb.append(" database:").append(database);
        }
        return sb.toString();
    }

    public ImportConnectionInfo(ImportDriverInfo driverInfo, String id, String alias, String url, String host, String port, String database, String user, char[] password)
    {
        this.driverInfo = driverInfo;
        this.id = id;
        this.alias = alias;
        this.url = url;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        setPassword(password);
    }

    public DriverDescriptor getDriver()
    {
        return driver;
    }

    public void setDriver(DriverDescriptor driver)
    {
        this.driver = driver;
    }

    public ImportDriverInfo getDriverInfo()
    {
        return driverInfo;
    }

    public String getId()
    {
        return id;
    }

    public String getAlias()
    {
        return alias;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost()
    {
        return host;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getDatabase()
    {
        return database;
    }

    public void setDatabase(String database)
    {
        this.database = database;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public char[] getPassword()
    {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = new char[password.length];
        System.arraycopy(password, 0, this.password, 0, password.length);
    }

    public void clearPassword() {
        Arrays.fill(password, (char) 0);
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperty(String name, String value)
    {
        properties.put(name, value);
    }

    public Map<String, String> getProviderProperties() {
        return providerProperties;
    }

    public void setProviderProperty(String name, String value)
    {
        properties.put(name, value);
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }
}
