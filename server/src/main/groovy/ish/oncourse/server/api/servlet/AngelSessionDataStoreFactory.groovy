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

package ish.oncourse.server.api.servlet

import ish.oncourse.server.CayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.integration.EventService
import org.eclipse.jetty.server.session.AbstractSessionDataStoreFactory
import org.eclipse.jetty.server.session.SessionDataStore
import org.eclipse.jetty.server.session.SessionHandler

class AngelSessionDataStoreFactory extends AbstractSessionDataStoreFactory {

    private CayenneService cayenneService
    private PreferenceController preferenceController
    private EventService eventService

    AngelSessionDataStoreFactory(CayenneService cayenneService, PreferenceController preferenceController, EventService eventService) {
        this.cayenneService = cayenneService
        this.preferenceController = preferenceController
        this.eventService = eventService
    }

    @Override
    SessionDataStore getSessionDataStore(SessionHandler handler) throws Exception {
        handler.getSessionCache().setEvictionPolicy(60)

        return new AngelSessionDataStore(cayenneService, preferenceController, eventService)
    }
}
