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

import ish.common.types.AccountTransactionType;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.server.cayenne.AccountTransaction;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentInLine;
import ish.oncourse.server.cayenne.PaymentOutLine;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class AccountTransactionLazyNode  extends LazyExpressionNode {

    private Property<Long> path;
    private AccountTransactionType type;
    private boolean isForContact;

    public AccountTransactionLazyNode(Property<Long> path, AccountTransactionType type, boolean isForContact) {
        super();
        this.path = path;
        this.type = type;
        this.isForContact = isForContact;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        if (ctx.hasErrors()) {
            return null;
        }
        if (args.get(0) != this || !(args.get(1) instanceof ASTPath)) {
            if (isForContact) {
                ctx.reportError(-1, -1, "Invalid arguments in account transaction property resolution.");
            }
        }
        var basePath = ((ASTPath) args.get(1)).getPath();

        var idx = 0;
        args.set(1, (SimpleNode) ExpressionFactory.pathExp(getSearchProperty(basePath)));

        for (var child : args.subList(1, args.size())) {
            if (child instanceof LazyExpressionNode) {
                parent = ((LazyExpressionNode) child).resolveParent(parent, args.subList(1, args.size()), ctx);
            } else {
                ExpressionUtil.addChild(parent, child, idx++);
            }
        }

        var ids = ObjectSelect.columnQuery(AccountTransaction.class, AccountTransaction.FOREIGN_RECORD_ID)
                .where(parent).and(AccountTransaction.TABLE_NAME.eq(type)).select(ctx.getContext());

        return (SimpleNode) path.in(ids);

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
}
