
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import java.util.HashSet;
import java.util.Set;

// ...

private static void setupHostKeyVerification(
    @NotNull SSHClient client,
    @NotNull DBWHandlerConfiguration configuration,
    @NotNull SSHHostConfiguration actualHostConfiguration
) throws IOException, JSchException {
    if (DBWorkbench.getPlatform().getApplication().isHeadlessMode() ||
        configuration.getBooleanProperty(SSHConstants.PROP_BYPASS_HOST_VERIFICATION)
    ) {
        JSch jsch = new JSch();
        HostKeyRepository hostKeyRepo = jsch.getHostKeyRepository();
        Set<String> hostKeyFingerprints = new HashSet<>();

        // TODO: Initialize hostKeyFingerprints with known host key fingerprints
        // Example: hostKeyFingerprints.add("12:34:56:78:90:ab:cd:ef:12:34:56:78:90:ab:cd:ef");

        client.addHostKeyVerifier((hostname, port, keyType, keyValue) -> {
            try {
                HostKey hostKey = new HostKey("dummy", keyValue);
                String fingerprint = hostKey.getFingerPrint(jsch);
                return hostKeyFingerprints.contains(fingerprint);
            } catch (JSchException e) {
                log.error("Error while verifying host key", e);
                return false;
            }
        });
    } else {
        client.addHostKeyVerifier(new KnownHostsVerifier(SSHUtils.getKnownSshHostsFileOrDefault(), actualHostConfiguration));
    }

    client.loadKnownHosts();
}
