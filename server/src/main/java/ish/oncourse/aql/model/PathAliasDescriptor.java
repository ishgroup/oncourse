/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.aql.model;

import java.util.List;
import java.util.Optional;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.SimpleNode;

public class PathAliasDescriptor implements SyntheticAttributeDescriptor {

    private final EntityFactory factory;
    private final Class<? extends Persistent> entityType;
    private final String path;
    private final String alias;
    private final Class<? extends Persistent> nextEntity;

    public PathAliasDescriptor(EntityFactory factory, Class<? extends Persistent> entityType,
                               String path, String alias, Class<? extends Persistent> nextEntity) {
        this.factory = factory;
        this.entityType = entityType;
        this.path = path;
        this.alias = alias;
        this.nextEntity = nextEntity;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return entityType;
    }

    @Override
    public String getAttributeName() {
        return alias;
    }

    @Override
    public Optional<Entity> nextEntity() {
        return nextEntity == null ?
                Optional.empty() :
                Optional.of(factory.createEntity(nextEntity));
    }

    @Override
    public SimpleNode spawnNode() {
        return new AliasPathLazyExpressionNode(path, alias);
    }

    private static class AliasPathLazyExpressionNode extends LazyExpressionNode {

        private final String path;
        private final String alias;

        AliasPathLazyExpressionNode(String path, String alias) {
            this.path = path;
            this.alias = alias;
        }

        @Override
        public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
            if (ctx.hasErrors()) {
                return null;
            }

            if(args.get(0) != this || !(args.get(1) instanceof ASTObjPath)) {
                ctx.reportError(-1, -1, "Invalid arguments in path alias resolution.");
                return null;
            }

            var pathExp = (ASTObjPath) args.get(1);
            var other = args.subList(2, args.size());
            var idx = 1;

            String path = pathExp.getPath();
            path = path.replaceFirst(alias, this.path);

            ExpressionUtil.addChild(parent, new ASTObjPath(path), 0);
            for (var child : other) {
                ExpressionUtil.addChild(parent, child, idx++);
            }

            return parent;
        }

        @Override
        public SimpleNode resolveSelf(CompilationContext ctx) {
            return this;
        }
    }
}