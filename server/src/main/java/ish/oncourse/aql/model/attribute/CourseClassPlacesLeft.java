/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model.attribute;

import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.CourseClass;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

public class CourseClassPlacesLeft implements SyntheticAttributeDescriptor {

    public CourseClassPlacesLeft(EntityFactory factory) {
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return CourseClass.class;
    }

    @Override
    public String getAttributeName() {
        return "placesLeft";
    }

    @Override
    public SimpleNode spawnNode() {
        return new SyntheticCourseClassPlacesLeftNode();
    }
}
