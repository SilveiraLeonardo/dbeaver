
package org.jkiss.dbeaver.erd.ui.dnd;

import org.eclipse.gef3.requests.CreationFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory for creating instances of new objects from a palette
 * @author Serge Rider
 */
public class DataElementFactory implements CreationFactory {

    private Object template;
    private static final Set<String> allowedClasses = new HashSet<>(Arrays.asList("com.example.Class1", "com.example.Class2", "com.example.Class3"));

    /**
     * Creates a new DataElementFactory with the given template object
     *
     * @param o
     *            the template
     */
    public DataElementFactory(Object o) {
        if (allowedClasses.contains(o.getClass().getName())) {
            template = o;
        } else {
            throw new IllegalArgumentException("Invalid template class: " + o.getClass().getName());
        }
    }

    /**
     * @see org.eclipse.gef3.requests.CreationFactory#getNewObject()
     */
    @Override
    public Object getNewObject() {
        try {
            return ((Class<?>) template).getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @see org.eclipse.gef3.requests.CreationFactory#getObjectType()
     */
    @Override
    public Object getObjectType() {
        return template;
    }

}
