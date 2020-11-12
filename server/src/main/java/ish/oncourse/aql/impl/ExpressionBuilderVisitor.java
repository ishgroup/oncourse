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

package ish.oncourse.aql.impl;

import ish.oncourse.aql.impl.converter.Converter;
import ish.oncourse.aql.impl.converter.ConverterDirectory;
import org.antlr.v4.runtime.tree.*;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Objects;

class ExpressionBuilderVisitor implements ParseTreeVisitor<Void> {

    private final CompilationContext context;

    ExpressionBuilderVisitor(CompilationContext context) {
        this.context = Objects.requireNonNull(context);
        context.pushLevel();
    }

    @Override
    public Void visit(ParseTree tree) {
        return tree.accept(this);
    }

    @Override
    public Void visitChildren(RuleNode node) {
        var action = ConverterDirectory.getInstance().getConverter(node.getClass());
        if(action != null) {
            // push stack if we able to process this node
            context.pushLevel();
        }

        // add all arguments on stack
        for (var i = 0; i<node.getChildCount(); i++) {
            node.getChild(i).accept(this);
        }

        if(action != null) {
            // create new expression node consuming arguments, if any
            var expressionNode = action.apply(node, context);
            if(expressionNode != null) {
                context.popLevelAndAdd(expressionNode);
            } else {
                // trim last entry in the stack as we always push it for non-null action
                for(var next : context.popLevel()) {
                    context.addNode(next);
                }
            }
        }

        return null;
    }

    @Override
    public Void visitTerminal(TerminalNode node) {
        var converter = ConverterDirectory.getInstance().getConverter(node.getClass());
        if(converter != null) {
            var expressionNode = converter.apply(node, context);
            // push argument on stack
            if(expressionNode != null) {
                context.addNode(expressionNode);
            }
        }
        return null;
    }


    @Override
    public Void visitErrorNode(ErrorNode node) {
        // do nothing
        return null;
    }
}
