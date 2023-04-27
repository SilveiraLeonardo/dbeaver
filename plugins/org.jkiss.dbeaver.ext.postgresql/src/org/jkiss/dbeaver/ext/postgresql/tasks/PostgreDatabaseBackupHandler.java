
import org.apache.commons.lang3.StringEscapeUtils;

// ...

if (!CommonUtils.isEmpty(settings.getEncoding())) {
    String escapedEncoding = StringEscapeUtils.escapeJava(settings.getEncoding());
    cmd.add("--encoding=" + escapedEncoding);
}

// ...

if (!dir.exists()) {
    if (!dir.mkdirs()) {
        log.error("Can't create directory '" + dir.getAbsolutePath() + "'");
        return false;
    } else {
        // Check if the directory was created with restricted permissions
        if (dir.setWritable(false, true) && dir.setReadable(false, true)) {
            log.info("Directory created with restricted permissions");
        } else {
            log.warn("Unable to set directory permissions");
        }
    }
}

// ...

if (!dir.exists()) {
    if (!dir.mkdirs()) {
        log.error("Can't create directory '" + dir.getAbsolutePath() + "': check permissions and directory path");
        return false;
    }
}

// ...

private String escapeCLIIdentifier(String identifier) {
    return "\"" + identifier.replace("\"", "\"\"") + "\"";
}

// ...

if (settings.isUseInserts()) {
    log.warn("The 'useInserts' setting is deprecated. Consider using an alternative setting.");
    cmd.add("--inserts");
}
