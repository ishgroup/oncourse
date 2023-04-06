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

package ish.oncourse.cayenne

class PathHandler {

    private static final String dot_join = "."
    private static final String outer_dot_join = "+."

    private String path
    private String firstJoin

    private PathHandler() {

    }

    static PathHandler valueOf(String path) {
        PathHandler pathHandler = new PathHandler()
        pathHandler.path = path
        pathHandler.init()
        return pathHandler
    }

    private void init() {
        if (hasJoins()) {
            int i1 = path.indexOf(outer_dot_join)
            int i2 = path.indexOf(dot_join)

            firstJoin = (i1 == -1) ? dot_join : (i2 == -1) ? outer_dot_join : (i1 < i2) ? outer_dot_join : dot_join
        }
    }

    boolean hasJoins() {
        return path.contains(outer_dot_join) || path.contains(dot_join)
    }

    String getPrefixProperty() {
        return hasJoins() ? path.substring(0, path.indexOf(firstJoin)) : null
    }

    String getPathForPrefixProperty() {
        return hasJoins() ? path.substring(path.indexOf(firstJoin) + firstJoin.length()) : null
    }
}
