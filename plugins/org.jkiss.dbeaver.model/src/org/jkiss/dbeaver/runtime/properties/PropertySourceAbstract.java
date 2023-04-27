
import org.eclipse.core.runtime.jobs.*;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.*;
import org.jkiss.dbeaver.model.*;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.impl.PropertyDescriptor;
import org.jkiss.dbeaver.model.preferences.DBPPropertyDescriptor;
import org.jkiss.dbeaver.model.preferences.DBPPropertyManager;
import org.jkiss.dbeaver.model.preferences.DBPPropertySource;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.load.AbstractLoadService;
import org.jkiss.dbeaver.model.runtime.load.ILoadVisualizer;
import org.jkiss.dbeaver.model.struct.DBSObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PropertySourceAbstract implements DBPPropertyManager, IPropertySourceMulti {
    private static final Log log = Log.getLog(PropertySourceAbstract.class);

   	private ConcurrentHashMap<String, DBPPropertyDescriptor> props = new ConcurrentHashMap<>();
    private Map<DBPPropertyDescriptor, Object> changedPropertiesValues = new HashMap<>();
    private ConcurrentHashMap<Object, Object> propValues = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Object, Object> lazyValues = new ConcurrentHashMap<>();
    private List<ObjectPropertyDescriptor> lazyProps = Collections.synchronizedList(new ArrayList<>());

    // (other existing class properties and methods remain unchanged)

    public synchronized boolean hasProperty(ObjectPropertyDescriptor prop) {
        return props.containsValue(prop);
    }

    public synchronized boolean isEmpty() {
        return props.isEmpty();
    }

    public DBPPropertyDescriptor getProperty(String id) {
        return props.get(id);
    }

    @Override
    public Object getPropertyValue(@Nullable DBRProgressMonitor monitor, final Object object, final ObjectPropertyDescriptor prop, boolean formatValue) {
    	// (unchanged code)
        try {
          	// (unchanged code)
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException) e).getTargetException();
            }
            log.error("Error reading property '" + prop.getId() + "' from " + object, e);
            return null; // Return null instead of the error message
        }
    }

    // Other unchanged methods

    private class PropertyValueLoadService extends AbstractLoadService<Map<ObjectPropertyDescriptor, Object>> {
        public static final String TEXT_LOADING = "...";

        // (unchanged code)

        @Override
        public Map<ObjectPropertyDescriptor, Object> evaluate(DBRProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException
        {
            try {
              	// (unchanged code)
            } catch (Throwable ex) {
                if (ex instanceof InterruptedException) {
                    log.debug("Loading of property values interrupted", ex);
                    return Collections.emptyMap(); // Return an empty map when interrupted
                } else if (ex instanceof InvocationTargetException) {
                    throw (InvocationTargetException)ex;
                } else {
                    throw new InvocationTargetException(ex);
                }
            }
        }

        // (unchanged code)
    }

    // (unchanged code for other nested classes)
}
