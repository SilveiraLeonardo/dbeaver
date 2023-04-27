
package org.jkiss.dbeaver.model.impl.data.formatters;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.data.DBDDataFormatter;
import org.jkiss.dbeaver.model.struct.DBSTypedObject;
import org.jkiss.utils.CommonUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Map;

public class DateTimeDataFormatter implements DBDDataFormatter {

    public static final String PROP_PATTERN = "pattern";
    public static final String PROP_TIMEZONE = "timezone";

    private String pattern;
    private DateTimeFormatter dateTimeFormatter;
    private ZoneId zone;

    @Override
    public void init(DBSTypedObject type, Locale locale, Map<String, Object> properties) {
        pattern = CommonUtils.toString(properties.get(PROP_PATTERN));
        final String timezone = CommonUtils.toString(properties.get(PROP_TIMEZONE));

        validatePattern(pattern);
        zone = CommonUtils.isEmptyTrimmed(timezone) ? null : ZoneId.of(timezone);
        validateTimeZone(zone);

        dateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withLocale(locale);;
    }

    private void validatePattern(String pattern) {
        if (CommonUtils.isEmptyTrimmed(pattern)) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }
    }

    private void validateTimeZone(ZoneId timezone) {
        if (timezone == null) {
            throw new IllegalArgumentException("Timezone must not be null");
        }
    }

    @Nullable
    public ZoneId getZone() {
        return zone;
    }

    @NotNull
    public DateTimeFormatter getDateFormatter() {
        return dateTimeFormatter;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public String formatValue(Object value) {
        if (value instanceof TemporalAccessor) {
            if (zone != null) {
                if (value instanceof LocalDateTime) {
                    return dateTimeFormatter.format(((LocalDateTime) value).atZone(zone));
                }
                if (value instanceof ZonedDateTime) {
                    return dateTimeFormatter.format(((ZonedDateTime) value).withZoneSameInstant(zone));
                }
                if (value instanceof OffsetDateTime) {
                    return dateTimeFormatter.format(((OffsetDateTime) value).atZoneSameInstant(zone));
                }
            }
            return dateTimeFormatter.format((TemporalAccessor) value);
        }
        return null;
    }

    @Override
    public Object parseValue(String value, Class<?> typeHint) throws DateTimeParseException {
        if (typeHint != null && TemporalAccessor.class.isAssignableFrom(typeHint)) {
            return LocalDateTime.parse(value, dateTimeFormatter);
        }
        throw new IllegalArgumentException("Unsupported type for parsing: " + typeHint);
    }

}
