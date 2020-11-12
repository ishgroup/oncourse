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

package ish.oncourse.aql.model;

import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Optional;

/**
 * Descriptor providing information about "synthetic" attributes, i.e. attributes that you can use in AQL
 * but they are not part of Cayenne model or generic custom attributes.
 * Instead these attributes calculated by business logic.
 *
 * @see ish.oncourse.aql.model.attribute.AccountTransactionBanking as an example.
 *

 */
public interface SyntheticAttributeDescriptor {

    Class<? extends Persistent> getEntityType();

    String getAttributeName();

    SimpleNode spawnNode();

    default Optional<Class<?>> getAttributeType() {
        return Optional.empty();
    }

    default Optional<Entity> nextEntity() {
        return Optional.empty();
    }
}
