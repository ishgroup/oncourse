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

package ish.oncourse.server.dashboard

import com.google.inject.Inject
import ish.oncourse.server.api.v1.model.SearchGroupDTO

class DashboardSearchManager {

    @Inject
    private ClassSearchService classSearchService

    @Inject
    private ContactSearchService contactSearchService

    @Inject
    private CourseSearchService courseSearchService

    @Inject
    private EnrolmentSearchService enrolmentSearchService

    @Inject
    private InvoiceSearchService invoiceSearchService

    @Inject
    private DocumentSearchService documentSearchService

    List<SearchGroupDTO> getSearchResult(String search) {
        [
                classSearchService,
                contactSearchService,
                courseSearchService,
                enrolmentSearchService,
                invoiceSearchService,
                documentSearchService

        ]*.getSearchResult(search).findAll()
    }
}




