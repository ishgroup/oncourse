/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model.attribute.tagging;

import ish.oncourse.aql.impl.converter.LazyTaggableAttributeNode;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.cayenne.glue.TaggableCayenneDataObject;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Optional;

import static ish.oncourse.aql.model.attribute.tagging.TaggableAttribute.CHECKED_TASKS;

public class CheckedTasksAttribute implements SyntheticAttributeDescriptor {
    EntityFactory factory;

    public CheckedTasksAttribute(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return TaggableCayenneDataObject.class;
    }

    @Override
    public String getAttributeName() {
        return CHECKED_TASKS.toString();
    }

    @Override
    public SimpleNode spawnNode() {
        return new LazyTaggableAttributeNode(CHECKED_TASKS);
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(Tag.class));
    }
}
