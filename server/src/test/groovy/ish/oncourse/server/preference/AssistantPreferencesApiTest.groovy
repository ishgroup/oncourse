/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.preference

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.model.SystemPreferenceDTO
import ish.oncourse.server.api.v1.service.impl.PreferenceApiImpl
import ish.oncourse.server.cayenne.Preference
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

import javax.ws.rs.ClientErrorException
import java.lang.reflect.Field

import static ish.persistence.Preferences.ASSISTANT_ADMIN_ONLY
import static ish.persistence.Preferences.ASSISTANT_ENABLED
import static ish.persistence.Preferences.ASSISTANT_STUDENT_DATA_ALLOWED

/**
 * onCourse Assistant settings saved through POST /preference into the Preference table.
 */
@CompileStatic
@DatabaseSetup(value = 'ish/oncourse/server/preference/assistantPreferencesDataSet.xml')
class AssistantPreferencesApiTest extends TestWithDatabase {

    @Test
    void savesTrueAndFalse() {
        PreferenceApiImpl api = preferenceApi()

        ['true': 'true', 'FALSE': 'false', ' false ': 'false'].each { String valueString, String stored ->
            api.update([new SystemPreferenceDTO(uniqueKey: ASSISTANT_ENABLED, valueString: valueString)])
            Assertions.assertEquals(stored, storedValue(ASSISTANT_ENABLED))
            Assertions.assertEquals([stored], api.get(ASSISTANT_ENABLED)*.valueString)
        }
    }

    @Test
    void otherValuesAreRejectedWithoutSavingTheRequest() {
        PreferenceApiImpl api = preferenceApi()

        ClientErrorException e = Assertions.assertThrows(ClientErrorException, {
            api.update([new SystemPreferenceDTO(uniqueKey: ASSISTANT_ENABLED, valueString: 'yes')])
        } as Executable)
        Assertions.assertEquals(400, e.response.status)
        Assertions.assertNull(preference(ASSISTANT_ENABLED))
        Assertions.assertEquals(['false'], api.get(ASSISTANT_ENABLED)*.valueString)

        // settings saved earlier in the same request are rolled back
        e = Assertions.assertThrows(ClientErrorException, {
            api.update([new SystemPreferenceDTO(uniqueKey: ASSISTANT_STUDENT_DATA_ALLOWED, valueString: 'true'),
                        new SystemPreferenceDTO(uniqueKey: ASSISTANT_ENABLED, valueString: 'yes')])
        } as Executable)
        Assertions.assertEquals(400, e.response.status)
        Assertions.assertNull(preference(ASSISTANT_STUDENT_DATA_ALLOWED))
        Assertions.assertNull(preference(ASSISTANT_ENABLED))
        Assertions.assertEquals(['false', 'false'], api.get("${ASSISTANT_STUDENT_DATA_ALLOWED},${ASSISTANT_ENABLED}".toString())*.valueString)
    }

    @Test
    void blankAdminOnlyKeepsTheDefault() {
        PreferenceApiImpl api = preferenceApi()

        api.update([new SystemPreferenceDTO(uniqueKey: ASSISTANT_ADMIN_ONLY, valueString: '')])
        Assertions.assertNull(storedValue(ASSISTANT_ADMIN_ONLY))
        Assertions.assertEquals(['true'], api.get(ASSISTANT_ADMIN_ONLY)*.valueString)

        api.update([new SystemPreferenceDTO(uniqueKey: ASSISTANT_ADMIN_ONLY, valueString: 'false')])
        Assertions.assertEquals(['false'], api.get(ASSISTANT_ADMIN_ONLY)*.valueString)

        api.update([new SystemPreferenceDTO(uniqueKey: ASSISTANT_ADMIN_ONLY, valueString: ' ')])
        Assertions.assertNull(storedValue(ASSISTANT_ADMIN_ONLY))
        Assertions.assertEquals(['true'], api.get(ASSISTANT_ADMIN_ONLY)*.valueString)
    }

    private PreferenceApiImpl preferenceApi() {
        PreferenceApiImpl api = new PreferenceApiImpl()
        setField(api, 'cayenneService', getCayenneService())
        setField(api, 'controller', injector.getInstance(PreferenceController))
        return api
    }

    private static void setField(Object target, String name, Object value) {
        Field field = target.class.getDeclaredField(name)
        field.setAccessible(true)
        field.set(target, value)
    }

    private Preference preference(String key) {
        return ObjectSelect.query(Preference)
                .where(Preference.NAME.eq(key))
                .selectOne(getCayenneService().newContext)
    }

    private String storedValue(String key) {
        return preference(key)?.valueString
    }
}
