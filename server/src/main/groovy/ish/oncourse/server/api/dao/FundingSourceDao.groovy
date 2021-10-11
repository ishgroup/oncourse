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

import javax.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.FundingSource
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById


class FundingSourceDao implements CayenneLayer<FundingSource>{

    @Inject
    private ICayenneService cayenneService

    @Override
    FundingSource newObject(ObjectContext context) {
        context.newObject(FundingSource)

    }

    @Override
    FundingSource getById(ObjectContext context, Long id) {
        SelectById.query(FundingSource, id)
                .selectOne(context)
    }

    List<FundingSource> getAll() {
        ObjectSelect.query(FundingSource)
                .orderBy(FundingSource.NAME.asc())
                .select(cayenneService.newContext)
    }

}
