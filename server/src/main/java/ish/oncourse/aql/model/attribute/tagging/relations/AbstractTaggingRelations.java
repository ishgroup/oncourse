/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model.attribute.tagging.relations;

import ish.oncourse.aql.impl.converter.LazyTaggingRelationsNode;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import org.apache.cayenne.exp.parser.SimpleNode;

abstract class AbstractTaggingRelations implements SyntheticAttributeDescriptor {

    EntityFactory factory;

    public AbstractTaggingRelations(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public String getAttributeName() {
        return "taggingRelations";
    }

    @Override
    public SimpleNode spawnNode() {
        return new LazyTaggingRelationsNode();
    }
}
