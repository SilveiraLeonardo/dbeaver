
} else if (object instanceof InputStream) {
    // Some weird drivers return InputStream instead of Xlob.
    // Copy stream to byte array
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try (InputStream stream = (InputStream) object) {
        IOUtils.copyStream(stream, buffer);
    } catch (Exception e) {
        throw new DBCException("Error reading content stream", e);
    }
    return new JDBCContentBytes(session.getExecutionContext(), buffer.toByteArray());
} else if (object instanceof Reader) {
    // Copy reader to string
    StringWriter buffer = new StringWriter();
    try (Reader reader = (Reader) object) {
        IOUtils.copyText(reader, buffer);
    } catch (Exception e) {
        throw new DBCException("Error reading content reader", e);
    }
    return new JDBCContentChars(session.getExecutionContext(), buffer.toString());
}
