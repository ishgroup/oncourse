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
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.cayenne.TaggableClasses;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Outcome;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.cayenne.exp.parser.*;

import java.util.List;

import static ish.util.TaggableUtil.resolveTaggableClass;

/**
 * Lazy node that resolves '#tag' expressions.
 *

 */
public class LazyPositiveTagNode extends LazyTagNode {

    public LazyPositiveTagNode(String tag) {
        super(tag);
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        var path = resolveBasePath();
        Entity taggedEntity = resolveTaggableEntity(path, ctx);
        if(taggedEntity == null) {
            return null;
        }

        return resolveDefaultTagNode(path, taggedEntity);
    }
}
