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

package ish.oncourse.server.api.dao

import groovy.transform.CompileDynamic
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.Persistent

@CompileDynamic
interface AutomationDao<K extends Persistent> extends CayenneLayer<K> {

    K getByKeyCode(ObjectContext context, String keyCode)

    List<K> getForEntity(String entityName, ObjectContext context)

    List<K> getByName(ObjectContext context, String name)
}
