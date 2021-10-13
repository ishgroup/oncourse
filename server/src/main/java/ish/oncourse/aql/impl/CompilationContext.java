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

import ish.oncourse.aql.CompilationError;
import ish.oncourse.aql.CompilationResult;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.server.users.SystemUserService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.*;

/**
 * Context object for expression compilation.
 * Accumulates errors and result, created per each compilation.
 *

 */
public class CompilationContext {

    /**
     * root for current query
     */
    private final Entity queryRootEntity;

    /**
     * expression stack filled by parsed tree visitor
     */
    private final Deque<List<SimpleNode>> expressionStack = new LinkedList<>();

    /**
     * java type for the last resolved path, will be used for operators and scalars evaluation
     */
    private Class<?> currentPathJavaType;

    /**
     * compilation errors, initialized lazily
     */
    private List<CompilationError> errors;

    /**
     * Counter of custom fields used in current query
     */
    private int customFieldsCounter;

    private ObjectContext context;


    private SystemUserService systemUserService;

    CompilationContext(Class<?> queryRootClass, ObjectContext context, SystemUserService systemUserService) {
        var entityFactory = new EntityFactory(context);
        this.queryRootEntity = entityFactory.createEntity(queryRootClass);
        this.context = context;
        this.currentPathJavaType = queryRootClass;
        this.systemUserService = systemUserService;
    }

    private Expression getCompiledExpression() {
        if(hasErrors()) {
            return null;
        }

        if(expressionStack.getFirst().size() != 1) {
            throw new IllegalStateException("Expression tree has wrong root of size " + expressionStack.getFirst().size());
        }
        var exp = expressionStack.getFirst().get(0);
        // check if it's lazy node, if it is, should resolve before return
        if(exp instanceof LazyExpressionNode) {
            // try to resolve self
            exp = ((LazyExpressionNode) exp).resolveSelf(this);
            if(exp instanceof LazyExpressionNode) {
                // should resolve via parent
                exp = ((LazyExpressionNode) exp).resolveParent(null, Collections.emptyList(), this);
            }
        }
        return exp;
    }

    public void setCurrentPathJavaType(Class<?> currentPathJavaType) {
        this.currentPathJavaType = currentPathJavaType;
    }

    public Class<?> getCurrentPathJavaType() {
        return currentPathJavaType;
    }

    public Entity getQueryRootEntity() {
        return queryRootEntity;
    }

    public void pushLevel() {
        expressionStack.addLast(new ArrayList<>());
    }

    public List<SimpleNode> popLevel() {
        return expressionStack.removeLast();
    }

    public void reportError(int line, int charPositionInLine, String message) {
        if(errors == null) {
            errors = new ArrayList<>();
        }

        errors.add(new CompilationError(line, charPositionInLine, message));
    }

    void popLevelAndAdd(SimpleNode node) {
        var children = popLevel();
        List<SimpleNode> resolvedChildren = new ArrayList<>(children.size());
        List<LazyExpressionNode> lazyExpressionNodes = new ArrayList<>(children.size());

        // resolve lazy nodes in two stages
        for(var child : children) {
            if(child instanceof LazyExpressionNode) {
                // first resolve simple cases
                var resolvedChild = ((LazyExpressionNode) child).resolveSelf(this);
                resolvedChildren.add(resolvedChild);
                // mark if node still needs resolution
                if(resolvedChild instanceof LazyExpressionNode) {
                    lazyExpressionNodes.add((LazyExpressionNode)resolvedChild);
                }
            } else {
                resolvedChildren.add(child);
            }
        }

        if(hasErrors()) {
            return;
        }

        if(!lazyExpressionNodes.isEmpty()) {
            // resolve those who need to rewrite parent node
            for(var lazyNode : lazyExpressionNodes) {
                node = lazyNode.resolveParent(node, resolvedChildren, this);
            }
            addNode(node);
            return;
        }

        if(hasErrors()) {
            return;
        }

        var i=0;
        for(var child: resolvedChildren) {
            ExpressionUtil.addChild(node, child, i++);
        }
        addNode(node);
    }

    public void addNode(SimpleNode node) {
        expressionStack.getLast().add(node);
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public int getAndIncrementCustomFieldsCounter() {
        return customFieldsCounter++;
    }

    CompilationResult buildResult() {
        var expression = getCompiledExpression();
        return hasErrors()
                ? new CompilationResult(Collections.unmodifiableList(errors))
                : new CompilationResult(expression);
    }

    public ObjectContext getContext() {
        return context;
    }

    public SystemUserService getSystemUserService() {
        return systemUserService;
    }
}
