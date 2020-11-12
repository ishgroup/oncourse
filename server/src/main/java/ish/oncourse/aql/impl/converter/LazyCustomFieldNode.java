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
import org.apache.cayenne.exp.parser.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Lazy node that creates custom field comparision operation node.
 * It combines root operation (that is unknown at creation time of this node) with correct arguments.
 *
 * I.e. this node performs this transformation:
 *
 *       _op__                    ______and_______
 *      /     \      -->         /                 \
 *  'field'   arg             __eq__             __op____
 *                           /      \           /        \
 *                field.type.fieldKey 'field'  field.value   arg
 *

 */
class LazyCustomFieldNode extends LazyExpressionNode {

    private final String basePath;
    private final String fieldKey;

    LazyCustomFieldNode(String basePath, String fieldKey) {
        this.basePath = Objects.requireNonNull(basePath);
        this.fieldKey = Objects.requireNonNull(fieldKey);
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        if(parent == null || args.get(0) != this) {
            // violation of this invariant means this code is out of sync with parser...
            // TODO: should we throw in this case?
            return null;
        }

        // build expression like this:
        // path.customField+.customFieldType+.name = passportNumber and path.customField+.value = 123
        var arg = args.get(1);
        var and = new ASTAnd();

        var alias = "__custom_field_alias__" + ctx.getAndIncrementCustomFieldsCounter();
        var map = Collections.singletonMap(alias, "customFields+");

        var equalFieldKey = new ASTEqual();
        var namePath = new ASTObjPath(basePath + alias + ".customFieldType+.key");
        namePath.setPathAliases(map);
        var value = new ASTScalar(fieldKey);
        ExpressionUtil.addChild(equalFieldKey, namePath, 0);
        ExpressionUtil.addChild(equalFieldKey, value, 1);

        var valuePath = new ASTObjPath(basePath + alias + ".value");
        valuePath.setPathAliases(map);
        ExpressionUtil.addChild(parent, valuePath, 0);
        ExpressionUtil.addChild(parent, arg, 1);

        ExpressionUtil.addChild(and, equalFieldKey, 0);
        ExpressionUtil.addChild(and, parent, 1);

        return and;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
