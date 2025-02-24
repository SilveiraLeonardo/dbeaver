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
package org.jkiss.dbeaver.erd.ui.action;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.gef3.ui.actions.DeleteAction;
import org.eclipse.ui.IWorkbenchPart;
import org.jkiss.dbeaver.erd.ui.editor.ERDEditorPart;

/**
 * DatabaseEditorPropertyTester
 */
public class ERDEditorPropertyTester extends PropertyTester
{
    public static final String NAMESPACE = "org.jkiss.dbeaver.erd.ui.editor";
    public static final String PROP_CAN_UNDO = "canUndo";
    public static final String PROP_CAN_REDO = "canRedo";
    public static final String PROP_CAN_DELETE = "canDelete";
    public static final String PROP_EDITABLE = "editable";
    public static final String PROP_EDIT_MODE = "editMode";
    public static final String SUPPORTS_STRUCTURE_EDIT = "supportsStructureEdit";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (!(receiver instanceof ERDEditorPart)) {
            return false;
        }
        ERDEditorPart erdEditor = (ERDEditorPart) receiver;
        switch (property) {
            case PROP_CAN_UNDO:
                return erdEditor.getCommandStack().canUndo();
            case PROP_CAN_REDO:
                return erdEditor.getCommandStack().canRedo();
            case PROP_EDITABLE:
                return !erdEditor.isReadOnly();
            case PROP_CAN_DELETE:
                DeleteAction deleteAction = new DeleteAction((IWorkbenchPart) erdEditor);
                deleteAction.update();
                return deleteAction.isEnabled();
            case PROP_EDIT_MODE:
                return erdEditor.getDiagram().isEditEnabled();
            case SUPPORTS_STRUCTURE_EDIT:
                return erdEditor.getDecorator().supportsStructureEdit() &&
                    erdEditor.getCommandContext() != null;
        }
        return false;
    }

}