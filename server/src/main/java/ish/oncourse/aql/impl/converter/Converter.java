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
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.function.BiFunction;

/**
 * Just a specialized {@link BiFunction} interface
 * that converts ANTLR node into Cayenne expression node
 *

 */
public interface Converter<T extends ParseTree> extends BiFunction<T, CompilationContext, SimpleNode> {
}
