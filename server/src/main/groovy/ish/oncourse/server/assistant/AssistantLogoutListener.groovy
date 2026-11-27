/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant

import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import ish.common.types.SystemEventType
import ish.oncourse.common.SystemEvent
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.integration.OnCourseEventListener
import org.apache.cayenne.ObjectId

/**
 * Drops the onCourse Assistant conversations of a user whose session ends. The session store posts
 * {@link SystemEventType#USER_LOGGED_OUT} whenever a session is deleted: on logout, when the user is kicked out
 * and when the session expires. A user has only one session, so all their conversations go.
 */
@Singleton
@CompileStatic
class AssistantLogoutListener implements OnCourseEventListener {

    private final AssistantConversationStore conversationStore

    @Inject
    AssistantLogoutListener(EventService eventService, AssistantConversationStore conversationStore) {
        this.conversationStore = conversationStore
        eventService.registerListener(this, SystemEventType.USER_LOGGED_OUT)
    }

    @Override
    void dispatchEvent(SystemEvent event) {
        // the event keeps the user's ObjectId, not the user
        Object value = event.value
        if (!(value instanceof ObjectId)) {
            return
        }
        ObjectId userId = value as ObjectId
        if (userId.entityName == SystemUser.simpleName && !userId.temporary) {
            Object id = userId.idSnapshot.get(SystemUser.ID_PK_COLUMN)
            if (id instanceof Number) {
                conversationStore.clearForUser((id as Number).longValue())
            }
        }
    }
}
