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

import javax.inject.Inject;
import ish.oncourse.aql.AqlService;
import ish.oncourse.aql.CompilationResult;
import ish.oncourse.server.users.SystemUserService;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.apache.cayenne.ObjectContext;

/**
 * <a href="http://www.antlr.org">ANTLR</a> based AQL query service.
 *

 */
public class AntlrAqlService implements AqlService {

    @Inject
    private SystemUserService systemUserService;

    /**
     * Method that validates and compiles SQL query into Cayenne {@link org.apache.cayenne.exp.Expression}.
     * <p>
     * This method is stateless thus thread safe.
     *
     * @param aqlQuery query string to parse
     * @param queryRootClass root entity class for this query, will be used to validate paths
     * @param cayenneContext Cayenne context
     * @return query compilation result
     */
    @Override
    public CompilationResult compile(String aqlQuery, Class<?> queryRootClass, ObjectContext cayenneContext) {
        // create will be used by all stages
        var context = new CompilationContext(queryRootClass, cayenneContext, systemUserService);
        var parsedQuery = parseQuery(aqlQuery, context);
        compile(parsedQuery, context);
        return context.buildResult();
    }

    private AqlParser.QueryContext parseQuery(String aqlQuery, CompilationContext context) {
        // will create new one per each request, as no recycling is supported
        var parser = createParser(aqlQuery);
        // setup own error reporting, default one is using stdout
        parser.removeErrorListeners();
        parser.addErrorListener(new AqlErrorListener(context));
        // query is the root of Grammar
        return parser.query();
    }

    private void compile(AqlParser.QueryContext query, CompilationContext context) {
        if(context.hasErrors()) {
            return;
        }
        // using visitor to build expression out of parsed tree
        var visitor = new ExpressionBuilderVisitor(context);
        visitor.visit(query);
    }

    private AqlParser createParser(String aqlQuery) {
        CharStream input = CharStreams.fromString(aqlQuery);
        TokenStream tokens = new CommonTokenStream(new AqlLexer(input));
        return new AqlParser(tokens);
    }

}
