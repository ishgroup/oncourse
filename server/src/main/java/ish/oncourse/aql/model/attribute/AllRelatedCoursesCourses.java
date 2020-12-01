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

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.Course;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;
import java.util.Optional;

public class AllRelatedCoursesCourses implements SyntheticAttributeDescriptor {

    private final EntityFactory factory;

    public AllRelatedCoursesCourses(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return Course.class;
    }

    @Override
    public String getAttributeName() {
        return "allRelatedCourses";
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(Course.class));
    }

    @Override
    public SimpleNode spawnNode() {
        return new LazyExpressionNode() {

            @Override
            public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
                if (ctx.hasErrors()) {
                    return null;
                }
                if (args.get(0) != this || !(args.get(1) instanceof ASTPath)) {
                    ctx.reportError(-1, -1, "Invalid arguments in VET property resolution.");
                }

                return null;
            }

            @Override
            public SimpleNode resolveSelf(CompilationContext ctx) {
                return this;
            }
        };
    }
}
