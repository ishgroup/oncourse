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
import ish.oncourse.server.cayenne.CourseClassTutor;
import ish.oncourse.server.cayenne.Session;
import ish.oncourse.server.cayenne.Tutor;
import ish.oncourse.server.cayenne.TutorAttendance;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;
import java.util.Optional;

public class SessionTutor implements SyntheticAttributeDescriptor {

    private EntityFactory factory;

    public SessionTutor(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return Session.class;
    }

    @Override
    public String getAttributeName() {
        return "tutor";
    }

    @Override
    public SimpleNode spawnNode() {
        return new TutorAttributeLazyNode();

    }

    @Override
    public Optional<Class<?>> getAttributeType() {
        return Optional.empty();
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(Tutor.class));
    }

    static class TutorAttributeLazyNode extends LazyExpressionNode {
        @Override
        public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
            if (ctx.hasErrors()) {
                return null;
            }

            // args should be following: this, path, args required for parent (e.g. list for tutor[1,2,3] expression)
            if(args.get(0) != this || !(args.get(1) instanceof ASTPath)) {
                ctx.reportError(-1, -1, "Invalid arguments in tutor property resolution.");
            }
            var pathExp = (ASTPath) args.get(1);
            var other = args.subList(2, args.size());

            var path = pathExp.getPath();
            var tutorPos = path.indexOf(".tutor");
            var pathPrefix = tutorPos <= 0 ? "" : path.substring(0, path.indexOf(".tutor"));
            tutorPos = path.indexOf("tutor.");
            var pathSuffix = tutorPos < 0 ? "" : path.substring(tutorPos + "tutor.".length());

            var idx = 1;
            for (var child : other) {
                ExpressionUtil.addChild(parent, child, idx++);
            }

            Property<?> tutorPath = Session.SESSION_TUTORS.dot(TutorAttendance.COURSE_CLASS_TUTOR).dot(CourseClassTutor.TUTOR);
            if(pathSuffix.length() > 0) {
                tutorPath = tutorPath.dot(pathSuffix);
            }

            if (!pathPrefix.isEmpty()) {
                Property<?> pathPrefixProp = Property.create(pathPrefix, Persistent.class);
                tutorPath = pathPrefixProp.dot(tutorPath);
            }

            ExpressionUtil.addChild(parent, tutorPath.getExpression(), 0);
            return parent;
        }

        @Override
        public SimpleNode resolveSelf(CompilationContext ctx) {
            return this;
        }
    }
}
