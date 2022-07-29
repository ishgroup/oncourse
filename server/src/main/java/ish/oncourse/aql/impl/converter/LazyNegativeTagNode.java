/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.model.Entity;
import org.apache.cayenne.exp.parser.SimpleNode;

import static ish.oncourse.aql.NodeUtils.inverseNodeByIds;

public class LazyNegativeTagNode extends LazyTagNode{

    public LazyNegativeTagNode(String tag) {
        super(tag);
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        var path = resolveBasePath();
        Entity taggedEntity = resolveTaggableEntity(path, ctx);
        if(taggedEntity == null) {
            return null;
        }

        var positiveNode = resolveDefaultTagNode(path, taggedEntity);
        return inverseNodeByIds(positiveNode, taggedEntity, ctx);
    }
}
