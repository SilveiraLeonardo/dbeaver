
package org.jkiss.dbeaver.sql.pragma;

// ... other imports ...

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SQLPragmaExport implements SQLPragmaHandler {
    // ... other code ...

    @NotNull
    private static Map<String, Object> createProducerSettings(@NotNull Map<String, Object> parameters) {
        // Validate input data and make sure it's a valid map
        if (!(parameters.get("producer") instanceof Map)) {
            return Collections.emptyMap();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> sanitizedMap = new HashMap<>((Map<String, Object>) parameters.get("producer"));

        // Add your producer settings sanitation logic here
        // For example, you can do this:
        Set<String> allowedKeys = new HashSet<>(Arrays.asList("key1", "key2")); // Replace with the allowed keys for producer settings
        sanitizedMap.keySet().retainAll(allowedKeys);

        return sanitizedMap;
    }

    @NotNull
    private static Map<String, Object> createConsumerSettings(@NotNull Map<String, Object> parameters) {
        // Validate input data and make sure it's a valid map
        if (!(parameters.get("consumer") instanceof Map)) {
            return Collections.emptyMap();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> sanitizedMap = new HashMap<>((Map<String, Object>) parameters.get("consumer"));

        // Add your consumer settings sanitation logic here
        // For example, you can do this:
        Set<String> allowedKeys = new HashSet<>(Arrays.asList("key1", "key2")); // Replace with the allowed keys for consumer settings
        sanitizedMap.keySet().retainAll(allowedKeys);

        return sanitizedMap;
    }

    @NotNull
    private static Map<String, Object> createProcessorSettings(@NotNull DataTransferProcessorDescriptor processor, @NotNull Map<String, Object> parameters) {
        // ... rest of the code ...

        final Map<String, Object> configProperties = JSONUtils.getObject(parameters, "processor");
        if (!(configProperties instanceof Map)) {
            return properties;
        }

        for (Map.Entry<String, Object> property : configProperties.entrySet()) {
            // ... rest of the code ...
        }

        // ... rest of the code ...

        return properties;
    }
}
