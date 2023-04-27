
} catch (SQLException ex) {
    if(ex instanceof SQLTimeoutException) {
        throw new DBException("Timeout occurred while finding objects by mask", ex, dataSource);
    } else {
        throw new DBException("Error occurred while finding objects by mask", ex, dataSource);
    }
}
