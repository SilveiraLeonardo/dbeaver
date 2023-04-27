
package org.jkiss.dbeaver.ext.wmi.model;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBPCloseableObject;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.meta.Association;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.DBSObjectContainer;
import org.jkiss.utils.CommonUtils;
import org.jkiss.wmi.service.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WMINamespace extends WMIContainer implements DBSObjectContainer, DBPCloseableObject {

    private static final Log log = Log.getLog(WMINamespace.class);

    protected WMIDataSource dataSource;
    private String name;
    protected WMIService service;
    private volatile List<WMINamespace> namespaces;
    private volatile List<WMIClass> rootClasses;
    private volatile List<WMIClass> associations;
    private volatile List<WMIClass> allClasses;
    
    // Add lock object for synchronization
    private final Object lock = new Object();

    public WMINamespace(WMINamespace parent, WMIDataSource dataSource, String name, WMIService service) {
        super(parent);
        this.dataSource = dataSource;
        this.name = name;
        this.service = service;
    }

    // All other methods remain unchanged

    List<WMINamespace> loadNamespaces(DBRProgressMonitor monitor)
        throws DBException
    {
        synchronized (lock) {
            try {
                WMIObjectCollectorSink sink = new WMIObjectCollectorSink(monitor, getService());
                getService().enumInstances("__NAMESPACE", sink, WMIConstants.WBEM_FLAG_SHALLOW);
                sink.waitForFinish();
                List<WMINamespace> children = new ArrayList<>();
                for (WMIObject object : sink.getObjectList()) {
                    String nsName = CommonUtils.toString(object.getValue("Name"));
                    children.add(new WMINamespace(this, dataSource, nsName, null));
                    object.release();
                }
                DBUtils.orderObjects(children);
                return children;
            } catch (WMIException e) {
                throw new DBException(e, getDataSource());
            }
        }
    }

    void loadClasses(DBRProgressMonitor monitor)
        throws DBException
    {
        synchronized (lock) {
            boolean showSystemObjects = getDataSource().getContainer().getNavigatorSettings().isShowSystemObjects();

            try {
                WMIObjectCollectorSink sink = new WMIObjectCollectorSink(monitor, getService());

                getService().enumClasses(null, sink, WMIConstants.WBEM_FLAG_DEEP);
                sink.waitForFinish();
                List<WMIClass> allClasses = new ArrayList<>();
                List<WMIClass> allAssociations = new ArrayList<>();
                List<WMIClass> rootClasses = new ArrayList<>();
                for (WMIObject object : sink.getObjectList()) {
                    WMIClass superClass = null;
                    String superClassName = (String)object.getValue(WMIConstants.CLASS_PROP_SUPER_CLASS);
                    if (superClassName != null) {
                        for (WMIClass c : allClasses) {
                            if (c.getName().equals(superClassName)) {
                                superClass = c;
                                break;
                            }
                        }
                        if (superClass == null) {
                            for (WMIClass c : allAssociations) {
                                if (c.getName().equals(superClassName)) {
                                    superClass = c;
                                    break;
                                }
                            }
                            if (superClass == null) {
                                log.warn("Super class '" + superClassName + "' not found");
                            }
                        }
                    }
                    WMIClass wmiClass = new WMIClass(this, superClass, object);
                    if (wmiClass.isAssociation()) {
                        allAssociations.add(wmiClass);
                    } else {
                        allClasses.add(wmiClass);
                        if (superClass == null) {
                            rootClasses.add(wmiClass);
                        }
                    }
                    if (superClass != null) {
                        superClass.addSubClass(wmiClass);
                    }
                }

                // filter out system classes
                if (!showSystemObjects) {
                    for (Iterator<WMIClass> iter = allClasses.iterator(); iter.hasNext(); ) {
                        WMIClass wmiClass = iter.next();
                        if (wmiClass.isSystem()) {
                            iter.remove();
                        }
                    }
                }

                DBUtils.orderObjects(rootClasses);
                DBUtils.orderObjects(allClasses);
                DBUtils.orderObjects(allAssociations);

                this.rootClasses = rootClasses;
                this.allClasses = allClasses;
                this.associations = allAssociations;
            } catch (WMIException e) {
                throw new DBException(e, getDataSource());
            }
        }
    }

    // All other methods remain unchanged
}
