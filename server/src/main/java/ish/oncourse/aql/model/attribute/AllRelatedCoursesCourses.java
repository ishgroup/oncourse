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
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.EntityRelation;
import ish.oncourse.server.cayenne.PaymentOutLine;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

                var basePath = ((ASTPath) args.get(1)).getPath();

                var idx = 0;
                var tempParent = parent;

                args.set(1, (SimpleNode) ExpressionFactory.pathExp(getSearchProperty(basePath)));

                for (var child : args.subList(1, args.size())) {
                    if (child instanceof LazyExpressionNode) {
                        tempParent = ((LazyExpressionNode) child).resolveParent(tempParent, args.subList(1, args.size()), ctx);
                    } else {
                        ExpressionUtil.addChild(tempParent, child, idx++);
                    }
                }

                List<Long> courseIds = ObjectSelect.columnQuery(Course.class, Course.ID).where(tempParent).select(ctx.getContext());
                List<Long> fromIds = ObjectSelect
                        .columnQuery(EntityRelation.class, EntityRelation.FROM_ENTITY_ANGEL_ID)
                        .where(EntityRelation.TO_ENTITY_ANGEL_ID.in(courseIds))
                        .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.class.getSimpleName()))
                        .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.class.getSimpleName()))
                        .select(ctx.getContext());

                List<Long> toIds = ObjectSelect
                        .columnQuery(EntityRelation.class, EntityRelation.TO_ENTITY_ANGEL_ID)
                        .where(EntityRelation.FROM_ENTITY_ANGEL_ID.in(courseIds))
                        .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.class.getSimpleName()))
                        .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.class.getSimpleName()))
                        .select(ctx.getContext());

                Expression relatedCourses = Course.ID.in(fromIds).orExp(Course.ID.in(toIds));

                return (SimpleNode) relatedCourses;
            }

            @Override
            public SimpleNode resolveSelf(CompilationContext ctx) {
                return this;
            }


            private String getSearchProperty(String path) {
                if (path.isEmpty()) {
                    return "id";
                }

                var pathComponents = path.split("\\.");
                return pathComponents[pathComponents.length-1];
            }
        };
    }
}
