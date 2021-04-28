/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import ish.oncourse.server.cayenne.Script;
import ish.oncourse.server.scripting.GroovyScriptService;
import ish.oncourse.server.scripting.ScriptParameters;
import ish.util.EntityUtil;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.commitlog.CommitLogListener;
import org.apache.cayenne.commitlog.model.ChangeMap;
import org.apache.cayenne.commitlog.model.ObjectChangeType;
import org.apache.cayenne.map.LifecycleEvent;
import org.apache.cayenne.query.SelectById;

import java.util.Set;

public class ScriptTriggeringCommitListener implements CommitLogListener {

    private final GroovyScriptService scriptService;
    private final static String QUEUED_RECORD = "QueuedRecord";
    private final static String QUEUED_TRANSACTION = "QueuedTransaction";

    public ScriptTriggeringCommitListener(GroovyScriptService scriptService) {
        this.scriptService = scriptService;
    }

    @Override
    public void onPostCommit(ObjectContext originatingContext, ChangeMap changes) {
        if (!Boolean.TRUE.equals(originatingContext.getUserProperty(GroovyScriptService.SCRIPT_CONTEXT_PROPERTY))) {
            changes.getChanges()
                    .entrySet()
                    .stream()
                    .filter(entry -> !QUEUED_RECORD.equals(entry.getKey().getEntityName()) &&
                            !QUEUED_TRANSACTION.equals(entry.getKey().getEntityName()))
                    .filter(entry -> ObjectChangeType.UPDATE.equals(entry.getValue().getType()))
                    .forEach(entityChange -> {
                        Class<?> entityClass = EntityUtil.entityClassForName(entityChange.getKey().getEntityName());
                        Set<Script> scripts = scriptService.getScriptsForEntity(entityClass, LifecycleEvent.POST_UPDATE);

                        scripts.forEach(script -> {
                            if (script.getEntityAttribute() == null ||
                                    entityChange.getValue().getAttributeChanges().containsKey(script.getEntityAttribute())) {

                                CayenneDataObject entity = SelectById.query(CayenneDataObject.class, entityChange.getKey())
                                        .selectOne(originatingContext);
                                scriptService.runScript(script, new ScriptParameters().fillDefaultParameters(entity));
                            }
                        });
                    });
        }
    }
}
