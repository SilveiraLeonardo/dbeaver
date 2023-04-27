
package org.jkiss.dbeaver.model.impl.app;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CertificateGenHelper {

    public static TrustManager[] getDefaultTrustManagers() throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((java.security.KeyStore) null);
        return trustManagerFactory.getTrustManagers();
    }

    public static Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm)
            throws GeneralSecurityException, OperatorCreationException {
        Instant from = Instant.now();
        Instant until = from.plus(days, ChronoUnit.DAYS);
        BigInteger sn = new BigInteger(64, new SecureRandom());
        X500Name owner = new X500Name(dn);

        JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(owner, sn, Date.from(from), Date.from(until), owner, pair.getPublic());
        ContentSigner signer = new JcaContentSignerBuilder(algorithm).build(pair.getPrivate());
        X509CertificateHolder holder = builder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(holder);
        cert.verify(pair.getPublic());

        return cert;
    }

    public static Certificate generateCertificate(String dn)
            throws GeneralSecurityException, OperatorCreationException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return generateCertificate(dn, keyPair, 365, "SHA256withRSA");
    }

    public static void main(String[] argv) throws Exception {
        Certificate certificate = generateCertificate("CN=Test, L=New York, S=New York, C=US");
        System.out.println("Certificate:" + certificate);
        
        // Use default trust managers for a secure SSLContext configuration
        TrustManager[] trustManagers = getDefaultTrustManagers();
        // Configure SSLContext with these trust managers as per the application requirements
    }
}
