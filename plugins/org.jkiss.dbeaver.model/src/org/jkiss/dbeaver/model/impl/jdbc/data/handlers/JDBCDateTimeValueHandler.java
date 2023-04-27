
import java.time.format.DateTimeFormatter;

public static final DateTimeFormatter DEFAULT_DATETIME_FORMAT = DateTimeFormatter.ofPattern("''" + DBConstants.DEFAULT_TIMESTAMP_FORMAT + "''");
public static final DateTimeFormatter DEFAULT_DATETIME_TZ_FORMAT = DateTimeFormatter.ofPattern("''" + DBConstants.DEFAULT_TIMESTAMP_TZ_FORMAT + "''");
public static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("''" + DBConstants.DEFAULT_DATE_FORMAT + "''");
public static final DateTimeFormatter DEFAULT_TIME_FORMAT = DateTimeFormatter.ofPattern("''" + DBConstants.DEFAULT_TIME_FORMAT + "''");
public static final DateTimeFormatter DEFAULT_TIME_TZ_FORMAT = DateTimeFormatter.ofPattern("''" + DBConstants.DEFAULT_TIME_TZ_FORMAT + "''");

@Override
public String getValueDisplayString(@NotNull DBSTypedObject column, Object value, @NotNull DBDDisplayFormat format) {
    if (format == DBDDisplayFormat.NATIVE) {
        if (value instanceof Date) {
            DateTimeFormatter nativeFormat = getNativeValueFormat(column);
            if (nativeFormat != null) {
                try {
                    return nativeFormat.format(((Date) value).toInstant());
                } catch (Exception e) {
                    log.error("Error formatting date", e);
                }
            }
        } else if (value instanceof String) {
            String strValue = (String) value;
            if (!strValue.startsWith("'") && !strValue.endsWith("'")) {
                strValue = "'" + strValue + "'";
            }
            return super.getValueDisplayString(column, strValue, format);
        }
    }
    return super.getValueDisplayString(column, value, format);
}

@Nullable
protected DateTimeFormatter getNativeValueFormat(DBSTypedObject type) {
    switch (type.getTypeID()) {
        case Types.TIMESTAMP:
            return DEFAULT_DATETIME_FORMAT;
        case Types.TIMESTAMP_WITH_TIMEZONE:
            return DEFAULT_DATETIME_FORMAT;
        case Types.TIME:
            return DEFAULT_TIME_FORMAT;
        case Types.TIME_WITH_TIMEZONE:
            return DEFAULT_TIME_TZ_FORMAT;
        case Types.DATE:
            return DEFAULT_DATE_FORMAT;
    }
    return null;
}

@Override
public Object fetchValueObject(@NotNull DBCSession session, @NotNull DBCResultSet resultSet, @NotNull DBSTypedObject type, int index) throws DBCException {
    try {
        // ...
    } catch (SQLException e) {
        try {
            // ...
        } catch (SQLException e1) {
            throw new DBCException("Can't retrieve datetime object", e1, session.getExecutionContext());
        }
        throw new DBCException(e, session.getExecutionContext());
    }
}
