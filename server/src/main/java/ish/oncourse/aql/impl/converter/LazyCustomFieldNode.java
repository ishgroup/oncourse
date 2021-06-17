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
import ish.oncourse.server.cayenne.CustomFieldType;
import org.apache.cayenne.exp.parser.*;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SQLSelect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
            ctx.reportError(-1, -1, "Invalid arguments in custom field property resolution.");
            return null;
        }

        var arg = args.get(1);
        if (arg instanceof ASTScalar && parent instanceof ASTEqual && ((ASTScalar) arg).getValue() == null) {

            String clazzName = ctx.getQueryRootEntity().getJavaClass().getSimpleName();

            Long fieldKeyId = ObjectSelect.columnQuery(CustomFieldType.class, CustomFieldType.ID)
                    .where(CustomFieldType.KEY.eq(fieldKey))
                    .selectOne(ctx.getContext());

            String sql;
            if (Arrays.asList("Article", "Membership", "Voucher").contains(clazzName)) {
                int typeId = 1;
                switch (clazzName) {
                    case "Membership":
                        typeId = 2;
                        break;
                    case "Voucher":
                        typeId = 3;
                        break;
                }

                sql = String.format("SELECT DISTINCT p.id from ProductItem p " +
                        "LEFT JOIN CustomField cf ON cf.foreignId = p.id and customFieldTypeId = %d " +
                        "WHERE cf.id is NULL and p.type = %d", fieldKeyId, typeId);
            } else {
                sql = String.format("SELECT DISTINCT c.id from %s c " +
                        "LEFT JOIN CustomField cf ON cf.foreignId = c.id and customFieldTypeId = %d " +
                        "WHERE cf.id is NULL", clazzName, fieldKeyId);
            }

            List<Long> ids = SQLSelect.dataRowQuery(sql)
                    .select(ctx.getContext())
                    .stream()
                    .map(dataRow -> (Long) dataRow.get("id"))
                    .collect(Collectors.toList());

            return new ASTIn(new ASTObjPath("id"), new ASTList(ids));

        } else {
            // build expression like this:
            // path.customField+.customFieldType+.name = passportNumber and path.customField+.value = 123
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

    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
