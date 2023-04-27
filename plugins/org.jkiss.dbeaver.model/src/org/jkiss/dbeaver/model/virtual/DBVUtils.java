
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public static Object evaluateDataExpression(DBDAttributeBinding[] allAttributes, Object[] row, String expression, String attributeName) {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("nashorn");
    
    bindAttributesToEngine(engine, allAttributes, row, attributeName);

    try {
        return engine.eval(expression);
    } catch (ScriptException e) {
        return GeneralUtils.getExpressionParseMessage(e);
    }
}

private static void bindAttributesToEngine(ScriptEngine engine, DBDAttributeBinding[] allAttributes, Object[] row, String attributeName) {
    Bindings bindings = engine.createBindings();
    for (DBDAttributeBinding attr : allAttributes) {
        Object value = DBUtils.getAttributeValue(attr, allAttributes, row);
        String label = attr.getLabel();
        bindings.put(label, value);
    }
    bindings.put(attributeName, null);
    engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
}

public static Object executeExpression(DBVEntityAttribute attribute, DBDAttributeBinding[] allAttributes, Object[] row) {
    String exprString = attribute.getExpression();
    if (CommonUtils.isEmpty(exprString)) {
        return null;
    }

    return evaluateDataExpression(allAttributes, row, exprString, attribute.getName());
}
