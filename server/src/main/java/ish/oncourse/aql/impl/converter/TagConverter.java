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
import org.apache.cayenne.exp.parser.SimpleNode;

/**
 * Converts tag operators (e.g. #'my tag').
 * Validates current query root for tag support.
 *

 */
class TagConverter implements Converter<AqlParser.TagPredicateContext> {

    @Override
    public SimpleNode apply(AqlParser.TagPredicateContext tag, CompilationContext ctx) {
        String tagValue;
        var tagContext = tag.tag();
        if(tagContext.Identifier() != null) {
            tagValue = tagContext.Identifier().getText();
        } else {
            if (tagContext.SingleQuotedStringLiteral() != null){
                tagValue = tagContext.SingleQuotedStringLiteral().getText();
            } else {
                tagValue = tagContext.DoubleQuotedStringLiteral().getText();
            }
            tagValue = tagValue.substring(1, tagValue.length()-1);
        }

        return new LazyPositiveTagNode(tagValue);
    }
}
