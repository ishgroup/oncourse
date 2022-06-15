/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;


import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.server.cayenne.Queueable;
import ish.util.EntityUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.commitlog.CommitLogListener;
import org.apache.cayenne.commitlog.model.ChangeMap;
import org.apache.cayenne.commitlog.model.ObjectChange;

import java.util.Collection;
import java.util.List;

public class RelationsChangeListener implements CommitLogListener {
    @Override
    public void onPostCommit(ObjectContext originatingContext, ChangeMap changes) {
        Collection<? extends ObjectChange> uniqueChanges = changes.getUniqueChanges();
        for (var objectChange : uniqueChanges) {
            var attributeChanges = objectChange.getAttributeChanges();
            var toOneChanges = objectChange.getToOneRelationshipChanges();
            if (toOneChanges.isEmpty() && attributeChanges.size() == 1 && attributeChanges.entrySet().stream().findAny().get().getKey().equals("modifiedOn")) {
                var postCommitId = objectChange.getPostCommitId();
                var classForName = EntityUtil.entityClassForName(postCommitId.getEntityName());
                var objectId = (Long) postCommitId.getIdSnapshot().get("id");
                List<PersistentObjectI> objectsByIds = EntityUtil.getObjectsByIds(originatingContext,classForName, List.of(objectId));
                if(!objectsByIds.isEmpty()){
                    var object = objectsByIds.get(0);
                    if(object instanceof Queueable){
                        ((Queueable)object).setAsyncReplicationAllowed(false);
                    }
                }
            }
        }
    }
}
