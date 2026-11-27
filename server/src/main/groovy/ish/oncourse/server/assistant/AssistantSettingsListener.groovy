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
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.integration.OnCourseEventListener

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

    @Inject
    AssistantSettingsListener(EventService eventService, AssistantConversationStore conversationStore) {
        this.conversationStore = conversationStore
        eventService.registerListener(this, SystemEventType.ASSISTANT_SETTINGS_CHANGED)
    }

    @Override
    void dispatchEvent(SystemEvent event) {
        Object value = event.value
        if (value instanceof AssistantAvailability) {
            AssistantAvailability availability = value as AssistantAvailability
            conversationStore.clearWhere { AssistantConversationStore.Conversation it -> !availability.isAvailableTo(it.userIsAdmin) }
        }
    }
}
