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

import ish.oncourse.aql.impl.AqlParser;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.Op;
import ish.oncourse.aql.impl.TypeClassifier;
import org.apache.cayenne.exp.parser.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Converts operator. Depends on current path data type.
 * See {@link OperatorConverter#converters} field for actually supported operators for given data type.
 *

 */
class OperatorConverter implements Converter<AqlParser.OperatorPredicateContext> {

    private final Map<TypeClassifier, Function<Op, SimpleNode>> converters
            = new EnumMap<>(TypeClassifier.class);

    OperatorConverter() {
        converters.put(TypeClassifier.NUMERIC, new ComparableOpConverter());
        converters.put(TypeClassifier.STRING, new StringOpConverter());
        converters.put(TypeClassifier.DATE, new ComparableOpConverter());
        converters.put(TypeClassifier.ENUM, new SimpleOpConverter());
        converters.put(TypeClassifier.BOOLEAN, new SimpleOpConverter());
        converters.put(TypeClassifier.CUSTOM_FIELD, new StringOpConverter());
        converters.put(TypeClassifier.CONTACT, new ContactOpConverter());
        converters.put(TypeClassifier.SITE, new SiteOpConverter());
        converters.put(TypeClassifier.ROOM, new RoomOpConverter());
        converters.put(TypeClassifier.QUALIFICATION, new QualificationOpConverter());
        converters.put(TypeClassifier.MODULE, new ModuleOpConverter());
        converters.put(TypeClassifier.DOCUMENT, new DocumentOpConverter());
        converters.put(TypeClassifier.INVOICE, new InvoiceOpConverter());
        converters.put(TypeClassifier.ACCOUNT_TRANSACTION, new AccountTransactionOpConverter());
        converters.put(TypeClassifier.ACCOUNT, new AccountOpConverter());
        converters.put(TypeClassifier.PAYSLIP, new PayslipOpConverter());
        converters.put(TypeClassifier.AUDIT, new AuditOpConverter());
        converters.put(TypeClassifier.SCRIPT, new ScriptOpConverter());
        converters.put(TypeClassifier.SYSTEM_USER, new SystemUserOpConverter());
        converters.put(TypeClassifier.PERSISTENT, new PersistentOpConverter());
    }

    @Override
    public SimpleNode apply(AqlParser.OperatorPredicateContext op, CompilationContext ctx) {

        var typeClassifier = TypeClassifier.of(ctx.getCurrentPathJavaType());
        var operator = Op.from(op.operator());
        if(operator == null) {
            ctx.reportError(op.operator().start.getLine(), op.operator().start.getCharPositionInLine(),
                    "Unknown operator '" + op.operator().getText() + "'");
            return null;
        }

        var converter = converters.get(typeClassifier);
        var node = converter == null
                ? null
                : converter.apply(operator);
        if(node == null) {
            ctx.reportError(op.operator().start.getLine(), op.operator().start.getCharPositionInLine(),
                    "Unsupported operator '" + op.operator().getText() + "' for " + typeClassifier.getReadableName() + " type");
            return null;
        }

        // could compare with null only persistent objects
        if(typeClassifier == TypeClassifier.PERSISTENT) {
            if(!(op.termOp() instanceof AqlParser.SingleTermContext)) {
                ctx.reportError(op.operator().start.getLine(), op.operator().start.getCharPositionInLine(),
                        "Only comparison with NULL is supported for the Persistent type");
                return null;
            }
            AqlParser.SingleTermContext termContext = (AqlParser.SingleTermContext) op.termOp();
            if(!(termContext.term() instanceof AqlParser.ValueTermContext)) {
                ctx.reportError(op.operator().start.getLine(), op.operator().start.getCharPositionInLine(),
                        "Only comparison with NULL is supported for the Persistent type");
                return null;
            }
            AqlParser.ValueTermContext valueTermContext = (AqlParser.ValueTermContext)termContext.term();
            if(!(valueTermContext.value() instanceof AqlParser.NullContext
                    || valueTermContext.value() instanceof AqlParser.EmptyContext)) {
                ctx.reportError(op.operator().start.getLine(), op.operator().start.getCharPositionInLine(),
                        "Only comparison with NULL is supported for the Persistent type");
                return null;
            }
        }

        return node;
    }

    private static class SimpleOpConverter implements Function<Op, SimpleNode> {
        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case NE:
                    return new ASTNotEqual();
                case EQ:
                    return new ASTEqual();
            }
            return null;
        }
    }

    private static class ComparableOpConverter implements Function<Op, SimpleNode> {
        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                    return new ASTEqual();
                case NE:
                    return new ASTNotEqual();
                case BEFORE:
                case LT:
                    return new ASTLess();
                case AFTER:
                case GT:
                    return new ASTGreater();
                case LE:
                    return new ASTLessOrEqual();
                case GE:
                    return new ASTGreaterOrEqual();
            }
            return null;
        }
    }

    private static class StringOpConverter implements Function<Op, SimpleNode> {
        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case NE:
                    return new ASTNotEqual();
                case EQ:
                    return new ASTEqual();
                case LIKE:
                case CONTAINS:
                case STARTS_WITH:
                case ENDS_WITH:
                    return new ASTLikeIgnoreCase();
                case NOT_LIKE:
                case NOT_CONTAINS:
                case NOT_STARTS_WITH:
                case NOT_ENDS_WITH:
                    return new ASTNotLikeIgnoreCase();
                case GT:
                    return new ASTGreater();
                case LT:
                    return new ASTLess();

            }
            return null;
        }
    }

    private static class ContactOpConverter implements Function<Op, SimpleNode> {
        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyContactComparisionNode(op);
            }
            return null;
        }
    }

    private static class SiteOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazySiteComparisonNode(op);
            }
            return null;
        }
    }

    private static class RoomOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyRoomComparisonNode(op);
            }
            return null;
        }
    }

    private static class QualificationOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyQualificationComparisonNode(op);
            }
            return null;
        }
    }

    private static class ModuleOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyModuleComparisonNode(op);
            }
            return null;
        }
    }

    private static class DocumentOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyDocumentComparisonNode(op);
            }
            return null;
        }
    }

    private static class InvoiceOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyInvoiceComparisonNode(op);
            }
            return null;
        }
    }

    private static class AccountOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyAccountComparisonNode(op);
            }
            return null;
        }
    }

    private static class AccountTransactionOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyTransactionComparisonNode(op);
            }
            return null;
        }
    }

    private static class PayslipOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyPayslipComparisonNode(op);
            }
            return null;
        }
    }

    private static class AuditOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyAuditComparisonNode(op);
            }
            return null;
        }
    }

    private static class ScriptOpConverter implements Function<Op, SimpleNode> {

        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                case LIKE:
                case CONTAINS:
                case NOT_LIKE:
                case NOT_CONTAINS:
                    return new LazyScriptComparisonNode(op);
            }
            return null;
        }
    }

    private static class SystemUserOpConverter implements Function<Op, SimpleNode> {
        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                    return new LazySystemUserComparisonNode(op);
            }
            return null;
        }
    }

    private static class PersistentOpConverter implements Function<Op, SimpleNode> {
        @Override
        public SimpleNode apply(Op op) {
            switch (op) {
                case EQ:
                case NE:
                    return new LazyRelationshipComparisonNode(op);
            }
            return null;
        }
    }
}
