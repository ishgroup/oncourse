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
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.cayenne.exp.parser.ASTAnd;
import org.apache.cayenne.exp.parser.ASTOr;
import org.apache.cayenne.exp.parser.ASTScalar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Just a singleton map of converters by ANTLR rule class.
 *

 */
public class ConverterDirectory {

    private static final ConverterDirectory INSTANCE = new ConverterDirectory();

    private final Map<Class<? extends ParseTree>, Converter<?>> converters = new ConcurrentHashMap<>();

    private ConverterDirectory() {
        // root expressions
        registerConverter(AqlParser.PredicateExpressionContext.class,
                new ExpressionRootConverter<>());
        registerConverter(AqlParser.AndExpressionContext.class,
                new ExpressionRootConverter<>());
        registerConverter(AqlParser.OrExpressionContext.class,
                new ExpressionRootConverter<>());
        registerConverter(AqlParser.BracketExpressionContext.class,
                new ExpressionRootConverter<>());

        // expressions
        registerConverter(AqlParser.AndExpressionContext.class, (and, ctx) ->
                new ASTAnd());
        registerConverter(AqlParser.OrExpressionContext.class, (or, ctx) ->
                new ASTOr());

        // predicates
        registerConverter(AqlParser.OperatorPredicateContext.class,
                new OperatorConverter());
        registerConverter(AqlParser.InPredicateContext.class,
                new InPredicateConverter());
        registerConverter(AqlParser.ReferencePredicateContext.class,
                new ReferencePredicateConverter());
        registerConverter(AqlParser.PathSegmentPredicateContext.class,
                new PathSegmentPredicateConverter());
        registerConverter(AqlParser.IdSetPredicateContext.class,
                new IdSetPredicateConverter());
        registerConverter(AqlParser.TagPredicateContext.class,
                new TagConverter());
        registerConverter(AqlParser.NotTagPredicateContext.class,
                new NotTagConverter());
        registerConverter(AqlParser.UnaryOperatorPredicateContext.class,
                new UnaryOpConverter());
        registerConverter(AqlParser.EntityRootSearchContext.class,
                new EntityRootSearchConverter());

        // math ops
        registerConverter(AqlParser.MathOpContext.class,
                new MathOpConverter());

        // path resolver
        registerConverter(AqlParser.PathContext.class,
                new PathConverter());

        // other expressions
        registerConverter(AqlParser.SetContext.class, (set, ctx) ->
                new LazyListNode());
        registerConverter(AqlParser.AmountContext.class,
                new AmountConverter());
        registerConverter(AqlParser.DateMathOpContext.class,
                new DateTimeMathOpConverter());
        registerConverter(AqlParser.DateAmountTermContext.class,
                new DateAmountConverter());
        registerConverter(AqlParser.DateTimeLiteralContext.class,
                new DateTimeLiteralConverter());
        registerConverter(AqlParser.UnaryTermContext.class,
                new UnaryTermOperatorConverter());
        registerConverter(AqlParser.IdsSetContext.class, (set, ctx) ->
                new LazyListNode());

        // All kinds of scalar values
        registerConverter(AqlParser.IntContext.class, (i, ctx) ->
                new ASTScalar(Integer.valueOf(i.IntegerLiteral().getText())));
        registerConverter(AqlParser.FloatContext.class, (f, ctx) ->
                new ASTScalar(Double.valueOf(f.FloatingPointLiteral().getText())));
        registerConverter(AqlParser.StringContext.class,
                new StringConverter());
        registerConverter(AqlParser.DateTimeContext.class,
                new DateTimeConverter());
        registerConverter(AqlParser.BooleanContext.class, (b, ctx) ->
                new ASTScalar(Boolean.valueOf(b.BooleanLiteral().getText())));
        registerConverter(AqlParser.NullContext.class, (n, ctx) ->
                new ASTScalar(null));
        registerConverter(AqlParser.EmptyContext.class, (n, ctx) ->
                new LazyEmptyNode());
        registerConverter(AqlParser.IdContext.class,
                new IdentifierConverter());
    }

    private <T extends ParseTree> void registerConverter(Class<T> treeType, Converter<T> converter) {
        converters.put(treeType, converter);
    }

    @SuppressWarnings("unchecked")
    public Converter<ParseTree> getConverter(Class<? extends ParseTree> treeType) {
        return (Converter<ParseTree>)converters.get(treeType);
    }

    public static ConverterDirectory getInstance() {
        return INSTANCE;
    }

}
