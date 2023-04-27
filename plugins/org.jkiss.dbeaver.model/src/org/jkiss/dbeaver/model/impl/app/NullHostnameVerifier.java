
/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.model.impl.app;

import org.jkiss.dbeaver.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Custom HostnameVerifier for SSL
 */
public class CustomHostnameVerifier implements HostnameVerifier {
    public static final CustomHostnameVerifier INSTANCE = new CustomHostnameVerifier();

    private static final Log log = Log.getLog(CustomHostnameVerifier.class);

    private CustomHostnameVerifier() {
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        // Use default HostnameVerifier
        HostnameVerifier defaultVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

        boolean verificationStatus = defaultVerifier.verify(hostname, session);
        
        if (verificationStatus) {
            log.debug("Host verification passed for domain " + hostname);
        } else {
            log.warn("Host verification failed for domain " + hostname);
        }
        return verificationStatus;
    }
}
