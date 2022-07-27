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

import ish.common.types.NodeType;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.cayenne.TaggableClasses;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Outcome;
import ish.util.EntityPathUtils;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.cayenne.exp.parser.*;

import java.util.Collections;
import java.util.List;

import static ish.util.TaggableUtil.resolveTaggableClass;

/**
 * Lazy node that resolves '#tag' expressions.
 *

 */
public class LazyTagNode extends LazyExprNodeWithBasePathResolver {
    protected static final String TAGGING_RELATIONS = "taggingRelations";

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
        Entity taggedEntity = ctx.getQueryRootEntity();
        if(!path.startsWith(TAGGING_RELATIONS))
            taggedEntity = EntityPathUtils.resolvePathToTaggable(path, ctx);
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

        var tagNodePath = new ASTObjPath(path + aliasName + ".tag+.nodeType");
        tagNodePath.setPathAliases(map);
        var tagNodeType = new ASTEqual(tagNodePath, NodeType.TAG.getDatabaseValue());

        ExpressionUtil.addChild(taggedNode, entityIdentifier, 0);
        ExpressionUtil.addChild(taggedNode, tagNodeType, 1);
        ExpressionUtil.addChild(taggedNode, tagName, 2);

        return taggedNode;
    }
}
