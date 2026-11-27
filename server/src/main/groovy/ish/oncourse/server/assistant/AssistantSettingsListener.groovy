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
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Drops the onCourse Assistant conversations of the users the assistant is no longer available to: everyone's
 * when it is turned off, those of users without the Admin toggle when it is opened to admins only. Their
 * conversations must not come back when the assistant is available to them again. The preference controller posts
 * {@link SystemEventType#ASSISTANT_SETTINGS_CHANGED} whenever these settings are saved.
 */
@Singleton
@CompileStatic
class AssistantSettingsListener implements OnCourseEventListener {

    private static final Logger logger = LogManager.logger

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
        Set<Long> admins = availability.isAvailableTo(true) ? currentAdminsAmong(users) : Collections.<Long>emptySet()
        users.findAll { Long it -> !availability.isAvailableTo(admins.contains(it)) }
                .each { Long it -> conversationStore.clearForUser(it) }
    }

    /**
     * Reads the admins among the users. If they can't be read, no one is treated as an admin, deliberately:
     * without the toggles the users who lost access can't be told from those who kept it, and dropping the admins'
     * conversations too only loses their chat history, while keeping the others' would leave a conversation with
     * someone the assistant is no longer available to. A failed lookup never fails the settings save. Errors such as
     * running out of memory aren't caught.
     *
     * @return ids of the users who have the Admin toggle on now, none if they can't be read
     */
    private Set<Long> currentAdminsAmong(Set<Long> users) {
        try {
            return adminsAmong(users)
        } catch (Exception e) {
            logger.warn('onCourse Assistant could not read the Admin toggles after it was opened to admins only, ' +
                    'so it dropped the conversations of all users, admins included, as a fail-safe', e)
            return Collections.<Long>emptySet()
        }
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
