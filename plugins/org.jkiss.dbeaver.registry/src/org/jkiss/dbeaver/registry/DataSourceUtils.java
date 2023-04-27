
private boolean isInputValid(String paramName, String paramValue) {
    switch (paramName) {
        case PARAM_PORT:
            return Pattern.matches("\\d{1,5}", paramValue);
        // Add case statements for other supported parameters:
        // ...
    }
}

for (String cp : conParams) {
    int divPos = cp.indexOf('=');
    if (divPos == -1) {
        continue;
    }
    String paramName = cp.substring(0, divPos);
    String paramValue = cp.substring(divPos + 1);
    
    if (!isInputValid(paramName, paramValue)) {
        log.error("Invalid value for parameter '" + paramName + "': " + paramValue);
        continue;
    }

    // ... (rest of the switch case)
}
