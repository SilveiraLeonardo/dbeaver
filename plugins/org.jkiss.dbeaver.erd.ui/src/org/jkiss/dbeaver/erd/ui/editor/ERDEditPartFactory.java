/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on Jul 13, 2004
 */
package org.jkiss.dbeaver.erd.ui.editor;

import org.eclipse.gef3.EditPart;
import org.eclipse.gef3.EditPartFactory;
import org.jkiss.dbeaver.erd.model.ERDAssociation;
import org.jkiss.dbeaver.erd.model.ERDEntity;
import org.jkiss.dbeaver.erd.model.ERDEntityAttribute;
import org.jkiss.dbeaver.erd.model.ERDNote;
import org.jkiss.dbeaver.erd.ui.model.EntityDiagram;
import org.jkiss.dbeaver.erd.ui.part.*;

/**
 * Edit part factory for creating EditPart instances as delegates for model objects
 *
 * @author Serge Rider
 */
public class ERDEditPartFactory implements EditPartFactory
{
    @Override
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart part = null;
        if (model instanceof EntityDiagram) {
            part = new DiagramPart();
        } else if (model instanceof ERDEntity) {
            part = new EntityPart();
        } else if (model instanceof ERDAssociation) {
            part = new AssociationPart();
        } else if (model instanceof ERDEntityAttribute) {
            part = new AttributePart();
        } else if (model instanceof ERDNote) {
            part = new NotePart();
        }
        if (part != null) {
            part.setModel(model);
        }
        return part;
    }
}