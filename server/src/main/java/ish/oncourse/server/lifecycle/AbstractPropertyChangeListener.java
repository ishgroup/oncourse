/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.commitlog.CommitLogListener;
import org.apache.cayenne.commitlog.model.AttributeChange;
import org.apache.cayenne.commitlog.model.ChangeMap;
import org.apache.cayenne.exp.Property;

public abstract class AbstractPropertyChangeListener<C, V> implements CommitLogListener {
    protected Class<C> entityClass;
    protected Property<V> property;

    @Override
    public void onPostCommit(ObjectContext originatingContext, ChangeMap changes) {
        changes.getChanges().forEach((k, v) -> {
            if (k.getEntityName().equals(entityClass.getSimpleName()) && k.getReplacementIdMap().isEmpty()){
                v.getAttributeChanges().forEach((n, ch) -> {
                    if (n.equals(property.getName()) && changed(ch)){
                        this.run(k);
                    }
                });
            }
        });
    }

    protected abstract boolean changed(AttributeChange attributeChange);

    protected abstract void run(ObjectId id);
}
