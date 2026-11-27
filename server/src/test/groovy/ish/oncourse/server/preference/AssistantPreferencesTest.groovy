/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.preference

import com.google.inject.AbstractModule
import com.google.inject.Guice
import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.function.PreferenceFunctions
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO
import ish.oncourse.server.api.v1.model.UserPreferenceDTO
import ish.oncourse.server.api.v1.service.impl.UserPreferenceApiImpl
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.integration.PluginsPrefsService
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.services.ISchedulerService
import ish.oncourse.server.services.ISystemUserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

import javax.ws.rs.ClientErrorException

import static ish.persistence.Preferences.ASSISTANT_ADMIN_ONLY
import static ish.persistence.Preferences.ASSISTANT_ENABLED
import static ish.persistence.Preferences.ASSISTANT_STUDENT_DATA_ALLOWED
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verifyZeroInteractions
import static org.mockito.Mockito.when

@CompileStatic
class AssistantPreferencesTest {

    private static final String AVAILABLE = PreferenceEnumDTO.ONCOURSE_ASSISTANT_AVAILABLE.toString()

    /**
     * Keeps college preferences in a map instead of the Preference table. A missing key reads as null, like a missing row.
     */
    static class InMemoryPreferenceController extends PreferenceController {

        final Map<String, String> values = [:]

        InMemoryPreferenceController() {
            super(mock(ICayenneService), mock(ISystemUserService), mock(LicenseService), mock(PluginsPrefsService), mock(ISchedulerService))
        }

        @Override
        protected String getValue(String key, boolean isUserPref) {
            return values[key]
        }

        @Override
        protected void setValue(String key, boolean isUserPref, String value) {
            values[key] = value
        }
    }

    private InMemoryPreferenceController controller = new InMemoryPreferenceController()

    @Test
    void missingPreferencesReadAsDefaults() {
        Assertions.assertFalse(controller.assistantEnabled)
        Assertions.assertTrue(controller.assistantAdminOnly)
        Assertions.assertFalse(controller.assistantStudentDataAllowed)

        // values returned by GET /preference
        Assertions.assertEquals('false', PreferenceFunctions.toString(ASSISTANT_ENABLED, controller))
        Assertions.assertEquals('true', PreferenceFunctions.toString(ASSISTANT_ADMIN_ONLY, controller))
        Assertions.assertEquals('false', PreferenceFunctions.toString(ASSISTANT_STUDENT_DATA_ALLOWED, controller))
    }

    @Test
    void preferencesWithoutValueReadAsDefaults() {
        controller.values[ASSISTANT_ENABLED] = (String) null
        controller.values[ASSISTANT_ADMIN_ONLY] = ''
        controller.values[ASSISTANT_STUDENT_DATA_ALLOWED] = (String) null

        Assertions.assertFalse(controller.assistantEnabled)
        Assertions.assertTrue(controller.assistantAdminOnly)
        Assertions.assertFalse(controller.assistantStudentDataAllowed)
    }

    @Test
    void preferenceApiSavesSettingsAsBooleans() {
        [ASSISTANT_ENABLED, ASSISTANT_ADMIN_ONLY, ASSISTANT_STUDENT_DATA_ALLOWED].each { String key ->
            // POST /preference converts the value and saves it this way
            controller.setValueForKey(key, PreferenceFunctions.toValue(key, 'true'))
            Assertions.assertEquals('true', controller.values[key])
            Assertions.assertEquals('true', PreferenceFunctions.toString(key, controller))

            controller.setValueForKey(key, PreferenceFunctions.toValue(key, 'false'))
            Assertions.assertEquals('false', controller.values[key])
            Assertions.assertEquals('false', PreferenceFunctions.toString(key, controller))
        }
    }

    @Test
    void settingsAcceptBooleansAndStrings() {
        [ASSISTANT_ENABLED, ASSISTANT_ADMIN_ONLY, ASSISTANT_STUDENT_DATA_ALLOWED].each { String key ->
            controller.setValueForKey(key, Boolean.TRUE)
            Assertions.assertEquals('true', controller.values[key])

            controller.setValueForKey(key, 'false')
            Assertions.assertEquals('false', controller.values[key])

            controller.setValueForKey(key, ' TRUE ')
            Assertions.assertEquals('true', controller.values[key])
        }
    }

    @Test
    void settingsWithoutValueReadAsDefaults() {
        controller.setAssistantEnabled(true)
        controller.setAssistantAdminOnly(false)
        controller.setAssistantStudentDataAllowed(true)

        controller.setValueForKey(ASSISTANT_ENABLED, null)
        controller.setValueForKey(ASSISTANT_ADMIN_ONLY, null)
        controller.setValueForKey(ASSISTANT_STUDENT_DATA_ALLOWED, '')

        Assertions.assertFalse(controller.assistantEnabled)
        Assertions.assertTrue(controller.assistantAdminOnly)
        Assertions.assertFalse(controller.assistantStudentDataAllowed)
    }

    @Test
    void settingsRejectOtherValues() {
        [ASSISTANT_ENABLED, ASSISTANT_ADMIN_ONLY, ASSISTANT_STUDENT_DATA_ALLOWED].each { String key ->
            [(Object) 'yes', (Object) 1].each { Object value ->
                Assertions.assertThrows(IllegalArgumentException, {
                    controller.setValueForKey(key, value)
                } as Executable)
            }
            Assertions.assertFalse(controller.values.containsKey(key))
        }
    }

    @Test
    void disabledAssistantIsNotAvailable() {
        controller.setAssistantAdminOnly(false)

        Assertions.assertFalse(controller.isAssistantAvailableTo(true))
        Assertions.assertFalse(controller.isAssistantAvailableTo(false))
    }

    @Test
    void adminOnlyAssistantIsAvailableToAdminsOnly() {
        controller.setAssistantEnabled(true)

        Assertions.assertTrue(controller.isAssistantAvailableTo(true))
        Assertions.assertFalse(controller.isAssistantAvailableTo(false))
    }

    @Test
    void assistantForAllUsersIsAvailableToEveryone() {
        controller.setAssistantEnabled(true)
        controller.setAssistantAdminOnly(false)

        Assertions.assertTrue(controller.isAssistantAvailableTo(true))
        Assertions.assertTrue(controller.isAssistantAvailableTo(false))
    }

    @Test
    void userPreferenceApiReturnsAvailabilityForCurrentUser() {
        controller.setAssistantEnabled(true)

        Assertions.assertEquals([(AVAILABLE): 'false'], userPreferenceApi(user(false)).get(AVAILABLE))
        Assertions.assertEquals([(AVAILABLE): 'true'], userPreferenceApi(user(true)).get(AVAILABLE))

        controller.setAssistantAdminOnly(false)
        Assertions.assertEquals([(AVAILABLE): 'true'], userPreferenceApi(user(false)).get(AVAILABLE))

        controller.setAssistantEnabled(false)
        Assertions.assertEquals([(AVAILABLE): 'false'], userPreferenceApi(user(true)).get(AVAILABLE))
    }

    @Test
    void userPreferenceApiDoesNotChangeAvailability() {
        ICayenneService cayenneService = mock(ICayenneService)
        UserPreferenceApiImpl api = userPreferenceApi(user(false), cayenneService)

        ClientErrorException e = Assertions.assertThrows(ClientErrorException, {
            api.set(new UserPreferenceDTO(key: PreferenceEnumDTO.ONCOURSE_ASSISTANT_AVAILABLE, value: 'true'))
        } as Executable)

        Assertions.assertEquals(400, e.response.status)
        verifyZeroInteractions(cayenneService)
        Assertions.assertTrue(controller.values.isEmpty())
        Assertions.assertEquals([(AVAILABLE): 'false'], api.get(AVAILABLE))
    }

    private static SystemUser user(boolean isAdmin) {
        SystemUser user = new SystemUser()
        user.setIsAdmin(isAdmin)
        return user
    }

    private UserPreferenceApiImpl userPreferenceApi(SystemUser currentUser, ICayenneService cayenneService = mock(ICayenneService)) {
        ISystemUserService userService = mock(ISystemUserService)
        when(userService.getCurrentUser()).thenReturn(currentUser)

        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(PreferenceController).toInstance(controller)
                bind(ISystemUserService).toInstance(userService)
                bind(ICayenneService).toInstance(cayenneService)
                bind(LicenseService).toInstance(mock(LicenseService))
                bind(IPermissionService).toInstance(mock(IPermissionService))
            }
        }).getInstance(UserPreferenceApiImpl)
    }
}
