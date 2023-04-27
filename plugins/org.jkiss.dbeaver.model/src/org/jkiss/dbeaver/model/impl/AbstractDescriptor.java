
import org.apache.commons.jexl3.*;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBIcon;
import org.jkiss.dbeaver.model.DBPImage;
import org.jkiss.utils.CommonUtils;
import org.osgi.framework.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDescriptor {

    private static final Log log = Log.getLog(AbstractDescriptor.class);
    private static final JexlEngine jexlEngine;
    static {
        jexlEngine = new JexlBuilder().cache(100).create();
    }

    public static JexlExpression parseExpression(String exprString) {
        try {
            return jexlEngine.createExpression(exprString);
        } catch (JexlException e) {
            log.error("Bad expression" + exprString, e);
            return null;
        }
    }

    public static JexlContext makeContext(final Object object, final Object context) {
        return new JexlContext() {
            @Override
            public Object get(String name) {
                return name.equals(VAR_OBJECT) ? object :
                        (name.equals(VAR_CONTEXT) ? context : null);
            }

            @Override
            public void set(String name, Object value) {
                log.warn("Set is not implemented");
            }

            @Override
            public boolean has(String name) {
                return
                        name.equals(VAR_OBJECT) && object != null ||
                                name.equals(VAR_CONTEXT) && context != null;
            }
        };
    }

    public static Object evalExpression(String exprString, Object object, Object context) {
        JexlExpression expression = AbstractDescriptor.parseExpression(exprString);
        if (expression == null) {
            return null;
        }
        return expression.evaluate(AbstractDescriptor.makeContext(object, context));
    }

    // ... Rest of the class
}
