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
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.integration.OnCourseEventListener
import org.apache.cayenne.query.ObjectSelect

/**
 * Drops the onCourse Assistant conversations of the users the assistant is no longer available to: everyone's
 * when it is turned off, those of users without the Admin toggle when it is opened to admins only. Their
 * conversations must not come back when the assistant is available to them again. The preference controller posts
 * {@link SystemEventType#ASSISTANT_SETTINGS_CHANGED} whenever these settings are saved.
 */
@Singleton
@CompileStatic
class AssistantSettingsListener implements OnCourseEventListener {

    private final AssistantConversationStore conversationStore
    private final ICayenneService cayenneService

    @Inject
    AssistantSettingsListener(EventService eventService, AssistantConversationStore conversationStore, ICayenneService cayenneService) {
        this.conversationStore = conversationStore
        this.cayenneService = cayenneService
        eventService.registerListener(this, SystemEventType.ASSISTANT_SETTINGS_CHANGED)
    }

    @Override
    void dispatchEvent(SystemEvent event) {
        Object value = event.value
        if (!(value instanceof AssistantAvailability)) {
            return
        }
        AssistantAvailability availability = value as AssistantAvailability
        if (availability.isAvailableTo(false)) {
            return
        }
        Set<Long> users = conversationStore.userIds()
        if (users.empty) {
            return
        }
        // the Admin toggle may have changed since the conversations started, so it is read now
        Set<Long> admins = availability.isAvailableTo(true) ? adminsAmong(users) : Collections.<Long>emptySet()
        users.findAll { Long it -> !availability.isAvailableTo(admins.contains(it)) }
                .each { Long it -> conversationStore.clearForUser(it) }
    }

    /**
     * @return ids of the given users who have the Admin toggle on now
     */
    protected Set<Long> adminsAmong(Set<Long> userIds) {
        return ObjectSelect.columnQuery(SystemUser, SystemUser.ID)
                .where(SystemUser.ID.in(userIds))
                .and(SystemUser.IS_ADMIN.isTrue())
                .select(cayenneService.newContext) as Set<Long>
    }
}
