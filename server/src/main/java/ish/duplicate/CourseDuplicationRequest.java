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

package ish.duplicate;

import ish.oncourse.cayenne.PersistentObjectI;
import org.apache.cayenne.Cayenne;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CourseDuplicationRequest implements Serializable {

    private List<Long> ids;

    public CourseDuplicationRequest() {
    }

    public static <T extends PersistentObjectI> CourseDuplicationRequest valueOf(List<T> classes) {
        CourseDuplicationRequest courseDuplicationRequest = new CourseDuplicationRequest();
        courseDuplicationRequest.ids = new LinkedList<>();
        for (T record : classes) {
            courseDuplicationRequest.ids.add(Cayenne.longPKForObject(record));
        }
        return courseDuplicationRequest;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
