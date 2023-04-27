
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

// ...

private void fetchDashboardMapData(DBRProgressMonitor monitor, DashboardContainer dashboard) {
    MapQueryInfo mqi = getMapQueryData(dashboard);
    if (mqi == null) {
        return;
    }
    Map<String, Object> mapValue = mqi.mapValue;
    if (mapValue != null) {
        String[] mapKeys = dashboard.getMapKeys();
        String[] mapLabels = dashboard.getMapLabels();
        if (!ArrayUtils.isEmpty(mapKeys)) {
            if (ArrayUtils.isEmpty(mapLabels)) {
                mapLabels = mapKeys;
            }
            DashboardDataset dataset = new DashboardDataset(mapLabels);
            Object[] mapValues = new Object[mapKeys.length];
            for (int i = 0; i < mapKeys.length; i++) {
                String sanitizedKey = sanitizeString(mapKeys[i]);
                Object value = mapValue.get(sanitizedKey);
                Number numValue;
                if (value instanceof Number) {
                    numValue = (Number) value;
                } else {
                    numValue = CommonUtils.toDouble(value);
                }
                mapValues[i] = numValue;
            }
            Date timestamp = mqi.timestamp;
            if (timestamp == null) {
                timestamp = new Date();
            }
            dataset.addRow(new DashboardDatasetRow(timestamp, mapValues));
            dashboard.updateDashboardData(dataset);
        } else if (dashboard.getMapFormula() != null) {
            // Add policy to sanitize map keys
            PolicyFactory policy = new HtmlPolicyBuilder()
                .allowElements("a")
                .allowUrlProtocols("https")
                .requireRelNofollowOnLinks()
                .toFactory();

            Map<String, Object> ciMap = new HashMap<>(mapValue.size());
            for (Map.Entry<String, Object> me : mapValue.entrySet()) {
                String sanitizedKey = sanitizeString(me.getKey());
                ciMap.put(sanitizedKey.toLowerCase(Locale.ENGLISH), me.getValue());
            }

            // ...

        }
    }
}

// Add a method to sanitize input strings
private String sanitizeString(String input) {
    // Use a basic policy to sanitize the input string
    PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
    return policy.sanitize(input);
}
