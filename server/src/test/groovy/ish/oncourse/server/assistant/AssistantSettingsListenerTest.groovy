/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant

import groovy.transform.CompileStatic
import ish.common.types.SystemEventType
import ish.oncourse.common.SystemEvent
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.preference.AssistantPreferencesTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static ish.persistence.Preferences.ASSISTANT_ENABLED

/**
 * The preference controller posts ASSISTANT_SETTINGS_CHANGED when the assistant is turned on or off
 * or opened to all users or to admins only.
 */
@CompileStatic
class AssistantSettingsListenerTest {

    private EventService events
    private AssistantConversationStore store
    private AssistantConversationStore.Conversation admin
    private AssistantConversationStore.Conversation ann
    // users with the Admin toggle on now
    private Set<Long> admins = [1L] as Set<Long>
    private boolean lookupFails
    private int lookups

    @BeforeEach
    void setUp() {
        events = new EventService()
        store = new AssistantConversationStore()
        // no database: the turned-off and open-to-all cases must not look up admins at all
        new AssistantSettingsListener(events, store, null) {
            @Override
            protected Set<Long> adminsAmong(Set<Long> userIds) {
                lookups++
                if (lookupFails) {
                    throw new IllegalStateException('database is down')
                }
                return userIds.findAll { Long it -> admins.contains(it) } as Set<Long>
            }
        }

        admin = store.start(1L)
        store.save(admin)
        ann = store.start(2L)
        store.save(ann)
    }

    @Test
    void turningOffDropsAllConversations() {
        lookupFails = true

        events.postEvent(SystemEvent.valueOf(SystemEventType.ASSISTANT_SETTINGS_CHANGED, new AssistantAvailability(false, false)))

        Assertions.assertNull(store.find(admin.id, 1L))
        Assertions.assertNull(store.find(ann.id, 2L))
        Assertions.assertEquals(0, lookups)
    }

    @Test
    void adminOnlyDropsConversationsOfUsersWithoutTheAdminToggle() {
        events.postEvent(SystemEvent.valueOf(SystemEventType.ASSISTANT_SETTINGS_CHANGED, new AssistantAvailability(true, true)))

        Assertions.assertSame(admin, store.find(admin.id, 1L))
        Assertions.assertNull(store.find(ann.id, 2L))
    }

    @Test
    void adminOnlyDropsAllConversationsWhenTheAdminTogglesCantBeRead() {
        lookupFails = true

        // the settings save isn't failed by the lookup
        events.postEvent(SystemEvent.valueOf(SystemEventType.ASSISTANT_SETTINGS_CHANGED, new AssistantAvailability(true, true)))

        // the admin's conversation goes too: a fail-safe
        Assertions.assertNull(store.find(admin.id, 1L))
        Assertions.assertNull(store.find(ann.id, 2L))
        Assertions.assertEquals(1, lookups)
    }

    @Test
    void adminOnlyUsesTheAdminToggleAtTheTimeOfTheChange() {
        // the toggles changed after the conversations started
        admins = [2L] as Set<Long>

        events.postEvent(SystemEvent.valueOf(SystemEventType.ASSISTANT_SETTINGS_CHANGED, new AssistantAvailability(true, true)))

        Assertions.assertNull(store.find(admin.id, 1L))
        Assertions.assertSame(ann, store.find(ann.id, 2L))
    }

    @Test
    void openToAllKeepsConversations() {
        events.postEvent(SystemEvent.valueOf(SystemEventType.ASSISTANT_SETTINGS_CHANGED, new AssistantAvailability(true, false)))
        events.postEvent(SystemEvent.valueOf(SystemEventType.ASSISTANT_SETTINGS_CHANGED, null))

        Assertions.assertEquals(2, store.size())
    }

    @Test
    void preferenceControllerPostsTheChanges() {
        AssistantPreferencesTest.InMemoryPreferenceController preferences = new AssistantPreferencesTest.InMemoryPreferenceController(events)
        preferences.values[ASSISTANT_ENABLED] = 'true'
        preferences.setAssistantAdminOnly(false)
        Assertions.assertEquals(2, store.size())

        preferences.setAssistantAdminOnly(true)
        Assertions.assertEquals(1, store.size())

        preferences.setValueForKey(ASSISTANT_ENABLED, 'false')
        Assertions.assertEquals(0, store.size())
    }
}
