/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.dao

import ish.oncourse.server.cayenne.ApiToken
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class ApiTokenDao implements CayenneLayer<ApiToken> {

    @Override
    ApiToken newObject(ObjectContext context) {
        ApiToken apiToken = context.newObject(ApiToken)
        apiToken.createdOn = new Date()
        apiToken.modifiedOn = new Date()
        return apiToken
    }

    @Override
    ApiToken getById(ObjectContext context, Long id) {
        return SelectById.query(ApiToken, id).selectOne(context)
    }
    
    List<ApiToken> getList(ObjectContext context) {
        ObjectSelect.query(ApiToken)
                .prefetch(ApiToken.SYSTEM_USER.joint())
                .orderBy(ApiToken.NAME.asc())
                .select(context)
    }
}
