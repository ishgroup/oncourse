/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.LazyExpressionNode;
import org.apache.cayenne.exp.parser.ASTPath;


/**
 * extended expression node, that provides opportunity to find base path of node and resolve path to taggable entity
 */
public abstract class LazyExprNodeWithBasePathResolver extends LazyExpressionNode implements BasePathProvider{
    protected static final String TAGGING_RELATIONS = "taggingRelations";
    protected static final String TAGS = "tags";
    protected static final String CHECKLISTS = "checklists";

    /**
     * @return not null path
     */
    public String resolveBasePath() {
        if(jjtGetNumChildren() == 1) {
            var node = jjtGetChild(0);
            if(node instanceof ASTPath) {
                var path = ((ASTPath) node).getPath();
                return path == null ? "" : path;
            }
        }

        return "";
    }
}
