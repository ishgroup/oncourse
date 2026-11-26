/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import ish.oncourse.server.services.AuditContext;
import ish.oncourse.server.services.AuditService;
import ish.oncourse.server.services.AuditValues;
import ish.oncourse.server.services.ISystemUserService;
import ish.oncourse.types.AuditAction;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.commitlog.CommitLogListener;
import org.apache.cayenne.commitlog.model.ChangeMap;
import org.apache.cayenne.commitlog.model.ObjectChange;
import org.apache.cayenne.commitlog.model.ObjectChangeType;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class AuditCommitListener implements CommitLogListener {
    private final static String QUEUED_RECORD = "QueuedRecord";
    private final static String QUEUED_TRANSACTION = "QueuedTransaction";
    private final static String AUDIT = "Audit";

    private final AuditService auditService;
    private final ISystemUserService systemUserService;


    public AuditCommitListener(AuditService auditService, ISystemUserService systemUserService) {
        this.auditService = auditService;
        this.systemUserService = systemUserService;
    }

    @Override
    public void onPostCommit(ObjectContext originatingContext, ChangeMap changes) {
        var currentUser = systemUserService.getCurrentUser();
        String createdBy = currentUser != null ? "Updated by " + currentUser.getEmail() + " : " : "";
        var context = AuditContext.current();
        boolean recordValues = context != null && context.isRecordValues();
        changes.getChanges()
                .entrySet()
                .stream()
                .filter(entry -> !QUEUED_RECORD.equals(entry.getKey().getEntityName()) &&
                        !QUEUED_TRANSACTION.equals(entry.getKey().getEntityName()) &&
                        !AUDIT.equals(entry.getKey().getEntityName()))
                .filter(entry -> ObjectChangeType.UPDATE.equals(entry.getValue().getType()))
                .forEach(entityChange -> {
                    ObjectChange change = entityChange.getValue();
                    Set<String> fieldNames = new LinkedHashSet<>(change.getAttributeChanges().keySet());
                    if (recordValues) {
                        // undo needs relationship changes too
                        fieldNames.addAll(change.getToOneRelationshipChanges().keySet());
                    }
                    fieldNames.removeIf(AuditValues::isIgnored);

                    if(!fieldNames.isEmpty()) {
                        Long id = (Long) entityChange.getKey().getIdSnapshot().get("id");
                        String entityName = entityChange.getKey().getEntityName();
                        String message = "Updated fields: " + createdBy + String.join(",", fieldNames);

                        if (recordValues) {
                            Map<String, Object> before = new TreeMap<>();
                            Map<String, Object> after = new TreeMap<>();
                            change.getAttributeChanges().forEach((name, value) -> {
                                before.put(name, value.getOldValue());
                                after.put(name, value.getNewValue());
                            });
                            change.getToOneRelationshipChanges().forEach((name, value) -> {
                                before.put(name, value.getOldValue());
                                after.put(name, value.getNewValue());
                            });
                            before.keySet().retainAll(fieldNames);
                            after.keySet().retainAll(fieldNames);
                            auditService.submit(entityName, id, AuditAction.UPDATE, message, before, after);
                        } else {
                            auditService.submit(entityName, id, AuditAction.UPDATE, message);
                        }
                    }
                });
    }
}
