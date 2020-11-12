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

package ish.oncourse.server.db;

public class DatabaseUtils {
    public static String validateDerbyURIPath(String uri){
        if (uri.endsWith("create=true") || uri.endsWith("upgrade=true"))
            return uri;

        if (uri.endsWith("/onCourse.iocdata/onCourse")) {
            return uri;
        } else if (uri.endsWith("/onCourse.iocdata/onCourse/")) {
            return uri.replace("/onCourse.iocdata/onCourse/", "/onCourse.iocdata/onCourse");
        } else if (uri.endsWith("/onCourse.iocdata")) {
            return uri + "/onCourse";
        } else if (uri.endsWith("/onCourse.iocdata/")) {
            return uri + "onCourse";
        } else if (uri.endsWith("/")) {
            return uri + "onCourse.iocdata/onCourse";
        } else {
            return uri + "/onCourse.iocdata/onCourse";
        }
    }
}
