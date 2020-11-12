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

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.cayenne.TaggableClasses;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Outcome;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.cayenne.exp.parser.*;

import java.util.Collections;
import java.util.List;

/**
 * Lazy node that resolves '#tag' expressions.
 *

 */
public class LazyTagNode extends LazyExpressionNode {

    private final String tag;
    private final ParserRuleContext ruleContext;

    public LazyTagNode(String tag, ParserRuleContext ruleContext) {
        this.tag = tag;
        this.ruleContext = ruleContext;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        return parent;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        var path = resolveBasePath();
        var taggedEntity = resolvePath(path, ctx);
        if(taggedEntity == null) {
            return null;
        }

        if(!path.isEmpty()) {
            path += '.';
        }

        SimpleNode node;

        if (CourseClass.class.equals(taggedEntity.getJavaClass())) {

            node = new ASTOr();

            SimpleNode classNode = createTaggedNode(path, resolveTaggableClass(taggedEntity));
            SimpleNode courseNode = createTaggedNode(path + "course.", TaggableClasses.COURSE);

            ExpressionUtil.addChild(node, classNode, 0);
            ExpressionUtil.addChild(node, courseNode, 1);

        } else if (Outcome.class.equals(taggedEntity.getJavaClass())) {
            node = createTaggedNode(path + "enrolment.", TaggableClasses.ENROLMENT);
        } else {
            node = createTaggedNode(path, resolveTaggableClass(taggedEntity));
        }

        return node;
    }

    private SimpleNode createTaggedNode(String path, TaggableClasses tagEntity) {
        SimpleNode taggedNode = new ASTAnd();
        var aliasName = tag.replace(".", "_");
        var map = Collections.singletonMap(aliasName, "taggingRelations+");

        var entityPath = new ASTObjPath(path + aliasName + ".entityIdentifier");
        entityPath.setPathAliases(map);

        var namePath = new ASTObjPath(path + aliasName + ".tag+.name");
        namePath.setPathAliases(map);

        var entityIdentifier = new ASTEqual(entityPath, tagEntity.getDatabaseValue());
        var tagName = new ASTEqual(namePath, tag);

        ExpressionUtil.addChild(taggedNode, entityIdentifier, 0);
        ExpressionUtil.addChild(taggedNode, tagName, 1);

        return taggedNode;
    }

    private TaggableClasses resolveTaggableClass(Entity entity) {
        var name = capitalizedAsConstant(entity.getName());
        return TaggableClasses.valueOf(name);
    }

    private static String capitalizedAsConstant(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        var charArray = name.toCharArray();
        var buffer = new StringBuilder();

        for (var i = 0; i < charArray.length; i++) {
            if ((Character.isUpperCase(charArray[i])) && (i != 0)) {
                var prevChar = charArray[i - 1];
                if ((Character.isLowerCase(prevChar))) {
                    buffer.append("_");
                }
            }
            buffer.append(Character.toUpperCase(charArray[i]));
        }

        return buffer.toString();
    }

    private Entity resolvePath(String path, CompilationContext ctx) {
        var entity = ctx.getQueryRootEntity();

        if (!path.isEmpty()) {
            var pathComponents = path.split("\\.");
            for (var next : pathComponents) {
                var nextSegment = next.replaceAll("\\+", "");
                entity = entity.getRelationship(nextSegment).orElse(null);
                if (entity == null) {
                    ctx.reportError(ruleContext.start.getLine(), ruleContext.start.getCharPositionInLine(),
                        "Can't resolve component '" + nextSegment + "' of relationship '" + path + "'");
                    return null;
                }
            }
        }

        // check that entity is taggable
        if (!entity.getRelationship("taggingRelations").isPresent()) {
            if (!Outcome.class.equals(entity.getJavaClass())) {
                ctx.reportError(ruleContext.start.getLine(), ruleContext.start.getCharPositionInLine(),
                        "Can't use tag here, entity '" + entity.getName() + "' is not taggable");
                return null;
            }
        }

        return entity;
    }

    /**
     * @return not null path
     */
    private String resolveBasePath() {
        if(jjtGetNumChildren() == 1) {
            var node = jjtGetChild(0);
            if(node instanceof ASTPath) {
                var path = ((ASTPath) node).getPath();
                return path == null ? "" : path;
            }
        }

        return "";
    }
}
