/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.model.Entity;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.*;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class NodeUtils {
    public static SimpleNode inverseNodeByIds(SimpleNode positiveNode, Entity taggedEntity, CompilationContext ctx){
        List<Long> notEmptyIds = ObjectSelect.columnQuery(taggedEntity.getJavaClass(), Property.create("id", Long.class))
                .where(positiveNode)
                .select(ctx.getContext());

        if (notEmptyIds.isEmpty())
            return new ASTTrue();

        return new ASTNotIn(new ASTObjPath("id"), new ASTList(notEmptyIds));
    }
}
