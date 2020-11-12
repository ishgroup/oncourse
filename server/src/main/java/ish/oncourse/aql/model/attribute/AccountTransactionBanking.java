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
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.*;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;
import java.util.Optional;

/**
 * Synthetic "banking" relation for {@link AccountTransaction} entity.
 * Calculated by PAYMENT_IN and PAYMENT_OUT FOREIGN_RECORD_ID identifiers.
 *

 */
public class AccountTransactionBanking implements SyntheticAttributeDescriptor {

    private final EntityFactory factory;

    public AccountTransactionBanking(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<AccountTransaction> getEntityType() {
        return AccountTransaction.class;
    }

    @Override
    public String getAttributeName() {
        return "banking";
    }

    @Override
    public SimpleNode spawnNode() {
        return new BankingAttributeLazyNode();
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(Banking.class));
    }

    private static class BankingAttributeLazyNode extends LazyExpressionNode {
        @Override
        public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
            if (ctx.hasErrors()) {
                return null;
            }

            // args should be following: this, path, args required for parent (e.g. list for banking[1,2,3] expression)
            if(args.get(0) != this || !(args.get(1) instanceof ASTPath)) {
                ctx.reportError(-1, -1, "Invalid arguments in banking property resolution.");
            }
            var pathExp = (ASTPath) args.get(1);
            var other = args.subList(2, args.size());

            var path = pathExp.getPath();
            var bankingPos = path.indexOf(".banking");
            var pathPrefix = bankingPos <= 0 ? "" : path.substring(0, path.indexOf(".banking"));
            bankingPos = path.indexOf("banking.");
            var pathSuffix = bankingPos < 0 ? "" : path.substring(bankingPos + "banking.".length());

            var idx = 1;
            for (var child : other) {
                ExpressionUtil.addChild(parent, child, idx++);
            }

            Property<?> bankingPath = PaymentInLine.PAYMENT_IN.dot(PaymentIn.BANKING);
            if(pathSuffix.length() > 0) {
                bankingPath = bankingPath.dot(pathSuffix);
            }
            ExpressionUtil.addChild(parent, bankingPath.getExpression(), 0);
            var paymentInLines = ObjectSelect.columnQuery(PaymentInLine.class, PaymentInLine.ID)
                    .where(parent)
                    .select(ctx.getContext());

            bankingPath = PaymentOutLine.PAYMENT_OUT.dot(PaymentOut.BANKING);
            if(pathSuffix.length() > 0) {
                bankingPath = bankingPath.dot(pathSuffix);
            }
            ExpressionUtil.addChild(parent, bankingPath.getExpression(), 0);
            var paymentOutLines = ObjectSelect.columnQuery(PaymentOutLine.class, PaymentOutLine.ID)
                    .where(parent)
                    .select(ctx.getContext());

            Property<?> pathPrefixProp = Property.create(pathPrefix, Persistent.class);
            var fkRecordId = pathPrefix.isEmpty()
                    ? AccountTransaction.FOREIGN_RECORD_ID
                    : pathPrefixProp.dot(AccountTransaction.FOREIGN_RECORD_ID);
            var tableName = pathPrefix.isEmpty()
                    ? AccountTransaction.TABLE_NAME
                    : pathPrefixProp.dot(AccountTransaction.TABLE_NAME);

            return (SimpleNode) fkRecordId.in(paymentInLines).andExp(tableName.eq(AccountTransactionType.PAYMENT_IN_LINE))
                    .orExp(fkRecordId.in(paymentOutLines).andExp(tableName.eq(AccountTransactionType.PAYMENT_OUT_LINE)));
        }

        @Override
        public SimpleNode resolveSelf(CompilationContext ctx) {
            return this;
        }
    }
}
