
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.rmi.MarshalException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.security.KeyStore;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

// ...

private static final RMIClientSocketFactory CSF_SSL = createSslClientSocketFactory();
private static final RMIServerSocketFactory SSF_SSL = createSslServerSocketFactory();

// ...

public static IInstanceController startInstanceServer(CommandLine commandLine, IInstanceController server) {
    try {
        openRmiRegistry();

        {
            IInstanceController stub;
            if (localRMI) {
                stub = (IInstanceController) UnicastRemoteObject.exportObject(server, 0, null, SSF_LOCAL);
            } else {
                // Use SSL RMI socket factory
                stub = (IInstanceController) UnicastRemoteObject.exportObject(server, 0, CSF_SSL, SSF_SSL);
            }

            registry.bind(CONTROLLER_ID, stub);
        }
        // ...

    } catch (Exception e) {
        log.error("Can't start RMI server", e);
        return null;
    }
}

// ...

@Override
public void openExternalFiles(final String[] fileNames) {
    log.debug("Open external file(s) [" + Arrays.toString(fileNames) + "]");

    // File path validation
    for (String filePath : fileNames) {
        // Add your desired conditions in this `if` statement to validate the file path
        if (/* your condition */) {
            throw new IllegalArgumentException("Invalid file path: " + filePath);
        }
    }

    // Rest of the code...
}

private static RMIClientSocketFactory createSslClientSocketFactory() {
    try {
        SSLContext ctx = createSslContext();
        return (host, port) -> {
            SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket(host, port);
            socket.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_CBC_SHA"});
            socket.startHandshake();
            return socket;
        };
    } catch (Exception e) {
        log.error("Cannot create SSL client socket factory", e);
        return null;
    }
}

private static RMIServerSocketFactory createSslServerSocketFactory() {
    try {
        SSLContext ctx = createSslContext();
        ServerSocketFactory serverSocketFactory = ctx.getServerSocketFactory();
        return port -> {
            SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port, 50, InetAddress.getLoopbackAddress());
            serverSocket.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_CBC_SHA"});
            serverSocket.setNeedClientAuth(true);
            return serverSocket;
        };
    } catch (Exception e) {
        log.error("Cannot create SSL server socket factory", e);
        return null;
    }
}

private static SSLContext createSslContext() throws Exception {
    // Load your keystore and truststore here, typically from a .jks file
    // Replace these placeholders with the path and password of your keystore and truststore
    String keystorePath = "path/to/your/keystore.jks";
    String truststorePath = "path/to/your/truststore.jks";
    String keystorePassword = "your_keystore_password";
    String truststorePassword = "your_truststore_password";

    KeyStore keyStore = KeyStore.getInstance("JKS");
    try (InputStream in = new FileInputStream(keystorePath)) {
        keyStore.load(in, keystorePassword.toCharArray());
    }

    KeyStore trustStore = KeyStore.getInstance("JKS");
    try (InputStream in = new FileInputStream(truststorePath)) {
        trustStore.load(in, truststorePassword.toCharArray());
    }

    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(trustStore);

    SSLContext ctx = SSLContext.getInstance("TLS");
    ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

    return ctx;
}
