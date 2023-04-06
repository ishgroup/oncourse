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

package org.apache.cayenne.access;

import ish.oncourse.server.api.servlet.ApiFilter;
import org.apache.cayenne.DataChannelSyncFilter;
import org.apache.cayenne.DataChannelSyncFilterChain;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.graph.GraphDiff;

/**
 * If the X-Validate:true header present in API request
 * we emulate the cayenne commit and passing through whole cayenne commit chain (filters and so on)
 * except real db SQL operrations (do not modify any data in db)
 * We need it to perform all cayenne object validation callbacks like PaymentIn.validateForSave()
 * We are using in on class UI - make x-validate request to prevent any possible commit errors. 
 * And if everything is ok - make reall commits.
 * 
 * keep that Class in cayenne package since it use package visible ObjectStoreGraphDiff class.
 * 
 */
public class ValidationFilter implements DataChannelSyncFilter {

    @Override
    public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelSyncFilterChain filterChain) {
        if (ApiFilter.validateOnly.get() != null && ApiFilter.validateOnly.get()) {
            ((ObjectStoreGraphDiff) changes).validateAndCheckNoop();
            return changes;
        } else {
            return filterChain.onSync(originatingContext, changes, syncType);
        }
    }
}
