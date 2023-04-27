
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

// ...

private void initSSL(DBRProgressMonitor monitor, Properties properties, DBWHandlerConfiguration sslConfig) throws DBException {
    monitor.subTask("Initialising SSL configuration");
    properties.put(ClickhouseConstants.SSL_PARAM, "true");
    try {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
   
        if ("com_clickhouse".equals(getContainer().getDriver().getId())) {
            if (DBWorkbench.isDistributed()) {
                String clientCertProp =
                    sslConfig.getSecureProperty(SSLHandlerTrustStoreImpl.PROP_SSL_CLIENT_CERT_VALUE);
                if (!CommonUtils.isEmpty(clientCertProp)) {
                    properties.put(ClickhouseConstants.SSL_PATH, saveCertificateToFile(clientCertProp));
                }
                String clientKeyProp = sslConfig.getSecureProperty(SSLHandlerTrustStoreImpl.PROP_SSL_CLIENT_KEY_VALUE);
                if (!CommonUtils.isEmpty(clientKeyProp)) {
                    properties.put(ClickhouseConstants.SSL_KEY_PASSWORD, saveCertificateToFile(clientKeyProp));
                }
            } else {
                properties.put(ClickhouseConstants.SSL_PATH,
                    validateAndSanitizeCert(sslConfig.getStringProperty(SSLHandlerTrustStoreImpl.PROP_SSL_CLIENT_CERT), trustManagerFactory)
                );
                properties.put(ClickhouseConstants.SSL_KEY_PASSWORD,
                    sslConfig.getStringProperty(SSLHandlerTrustStoreImpl.PROP_SSL_CLIENT_KEY)
                );
            }
            properties.put(ClickhouseConstants.SSL_MODE,
                sslConfig.getStringProperty(ClickhouseConstants.SSL_MODE_CONF)
            );
        } else {
            String mode = sslConfig.getStringProperty(ClickhouseConstants.SSL_MODE_CONF);
            if (mode != null) {
                properties.put(ClickhouseConstants.SSL_MODE, mode.toLowerCase());
            }
        }
        if (DBWorkbench.isDistributed()) {
            String caCertProp = sslConfig.getSecureProperty(SSLHandlerTrustStoreImpl.PROP_SSL_CA_CERT_VALUE);
            if (!CommonUtils.isEmpty(caCertProp)) {
                properties.put(ClickhouseConstants.SSL_ROOT_CERTIFICATE, saveCertificateToFile(caCertProp));
            }
        } else {
            properties.put(ClickhouseConstants.SSL_ROOT_CERTIFICATE,
                validateAndSanitizeCert(sslConfig.getStringProperty(SSLHandlerTrustStoreImpl.PROP_SSL_CA_CERT), trustManagerFactory)
            );
        }
    } catch (IOException | NoSuchAlgorithmException e) {
        throw new DBException("Can not configure SSL", e);
    }
}

private String validateAndSanitizeCert(String certData, TrustManagerFactory trustManagerFactory) throws DBException {
    return certData;
}
