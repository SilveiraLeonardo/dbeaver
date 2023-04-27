
private void addSequenceOptions(GenericSequence sequence, StringBuilder ddl, Map<Object, Object> options) {
    if (options.containsKey("incrementBy")) {
        ddl.append("\n\tINCREMENT BY ").append(SQLUtils.escapeString(String.valueOf(options.get("incrementBy"))));
    }
    if (options.containsKey("minValue")) {
        ddl.append("\n\tMINVALUE ").append(SQLUtils.escapeString(String.valueOf(options.get("minValue"))));
    }
    if (options.containsKey("maxValue")) {
        ddl.append("\n\tMAXVALUE ").append(SQLUtils.escapeString(String.valueOf(options.get("maxValue"))));
    }
    if (options.containsKey("lastValue")) {
        if (!sequence.isPersisted()) {
            ddl.append("\n\tSTART WITH ").append(SQLUtils.escapeString(String.valueOf(options.get("lastValue"))));
        } else {
            ddl.append("\n\tRESTART WITH ").append(SQLUtils.escapeString(String.valueOf(options.get("lastValue"))));
        }
    }
    if (options.containsKey("cacheCount")) {
        ddl.append("\n\tCACHE ").append(SQLUtils.escapeString(String.valueOf(options.get("cacheCount"))));
    }
    if (options.containsKey("cycle")) {
        ddl.append("\n\t");
        if (!CommonUtils.toBoolean(options.get("cycle"))) {
            ddl.append("NO ");
        }
        ddl.append("CYCLE");
    }
}
