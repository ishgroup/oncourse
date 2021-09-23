/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.util;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.server.cayenne.Outcome;

public class EntityPathUtils {
    private static final String TAGGING_RELATIONS = "taggingRelations";

    public static Entity resolvePath(String path, CompilationContext ctx) {
        var entity = ctx.getQueryRootEntity();

        if (!path.isEmpty()) {
            var pathComponents = path.split("\\.");
            for (var next : pathComponents) {
                var nextSegment = next.replaceAll("\\+", "");
                entity = entity.getRelationship(nextSegment).orElse(null);
                if (entity == null) {
                    ctx.reportError(-1, -1, "Can't resolve component '" + nextSegment + "' of relationship '" + path + "'");
                    return null;
                }
            }
        }

        return entity;
    }

    public static Entity resolvePathToTaggable(String path, CompilationContext ctx) {
        if (path.startsWith(TAGGING_RELATIONS))
            return ctx.getQueryRootEntity();
        var entity = resolvePath(path, ctx);
        if (entity == null)
            return null;
        if (!entity.getRelationship(TAGGING_RELATIONS).isPresent()) {
            if (!Outcome.class.equals(entity.getJavaClass())) {
                ctx.reportError(-1, -1, "Can't use tag here, entity '" + entity.getName() + "' is not taggable");
                return null;
            }
        }
        return entity;
    }
}
