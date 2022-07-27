/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model.attribute.tagging;

import ish.common.types.NodeType;
import ish.oncourse.aql.impl.converter.LazyTagsNode;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.cayenne.glue.TaggableCayenneDataObject;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Optional;

public class ChecklistsAttribute implements SyntheticAttributeDescriptor {
    EntityFactory factory;

    public ChecklistsAttribute(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return TaggableCayenneDataObject.class;
    }

    @Override
    public String getAttributeName() {
        return "checklists";
    }

    @Override
    public SimpleNode spawnNode() {
        return new LazyTagsNode(NodeType.CHECKLIST);
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(Tag.class));
    }
}
