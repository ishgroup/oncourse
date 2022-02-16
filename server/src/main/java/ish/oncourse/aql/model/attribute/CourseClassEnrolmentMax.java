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

package ish.oncourse.aql.model.attribute;

import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.CourseClass;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;
import java.util.Optional;

/**

 */
public class CourseClassEnrolmentMax implements SyntheticAttributeDescriptor {

    public static final String ATTRIBUTE_NAME = "isMaxEnrolments";

    public CourseClassEnrolmentMax(EntityFactory factory) {
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return CourseClass.class;
    }

    @Override
    public String getAttributeName() {
        return ATTRIBUTE_NAME;
    }

    @Override
    public SimpleNode spawnNode() {
        return new SyntheticCourseClassEnrolmentNode() {
            @Override
            protected SimpleNode convertParentExpression(SimpleNode parent, List<SimpleNode> args) {
                var arg2 = args.get(2);
                if(arg2 instanceof ASTScalar && ((ASTScalar)arg2).getValue() == Boolean.FALSE) {
                    return (SimpleNode) CourseClass.ENROLMENTS.count().lt(CourseClass.MAXIMUM_PLACES);
                } else {
                    return (SimpleNode) CourseClass.ENROLMENTS.count().gte(CourseClass.MAXIMUM_PLACES);
                }
            }

            @Override
            protected String getAttributeName() {
                return ATTRIBUTE_NAME;
            }
        };
    }

    @Override
    public Optional<Class<?>> getAttributeType() {
        return Optional.of(Boolean.class);
    }

}
