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
import ish.oncourse.server.AuditListener
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.model.SystemPreferenceDTO
import ish.oncourse.server.api.v1.service.impl.PreferenceApiImpl
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.services.AuditService
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.lang.reflect.Field
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import static ish.persistence.Preferences.ASSISTANT_ADMIN_ONLY
import static ish.persistence.Preferences.ASSISTANT_ENABLED
import static ish.persistence.Preferences.ASSISTANT_STUDENT_DATA_ALLOWED

/**
 * onCourse Assistant settings are Preference records, so the existing audit listeners record every change:
 * AuditListener the first save (the record is created), AuditCommitListener later changes.
 */
@CompileStatic
@DatabaseSetup(value = 'ish/oncourse/server/preference/assistantPreferencesDataSet.xml')
class AssistantPreferencesAuditTest extends TestWithDatabase {

    private static final String ADMIN_EMAIL = 'admin@mail.com'
    private static final Long ADMIN_ID = 1L

    @BeforeAll
    void registerAuditListener() {
        // TestModule binds a mock AuditListener, register the real one the way CayenneListenersService does
        getCayenneService().addListener(new AuditListener(injector.getInstance(AuditService)))
    }

    @Test
    void changingAssistantSettingsIsAudited() {
        PreferenceApiImpl preferenceApi = preferenceApi()

        [(ASSISTANT_ENABLED): 'true', (ASSISTANT_ADMIN_ONLY): 'false', (ASSISTANT_STUDENT_DATA_ALLOWED): 'true'].each { String key, String value ->
            // a missing preference reads as its default, so the first change creates the record
            preferenceApi.update([new SystemPreferenceDTO(uniqueKey: key, valueString: value)])
            Long id = preferenceId(key)

            List<Map<String, Object>> entries = auditEntries(id)
            Assertions.assertEquals(1, entries.size())
            Assertions.assertEquals('CREATE', entries[0].action)
            Assertions.assertEquals("Entity created by ${ADMIN_EMAIL}: ${key}".toString(), entries[0].message)
            Assertions.assertEquals(ADMIN_ID, entries[0].systemUserId)

            preferenceApi.update([new SystemPreferenceDTO(uniqueKey: key, valueString: Boolean.toString(!Boolean.valueOf(value)))])

            entries = auditEntries(id)
            Assertions.assertEquals(2, entries.size())
            Assertions.assertEquals('UPDATE', entries[1].action)
            Assertions.assertEquals("Updated fields: Updated by ${ADMIN_EMAIL} : valueString".toString(), entries[1].message)
            Assertions.assertEquals(ADMIN_ID, entries[1].systemUserId)
        }
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

    private Long preferenceId(String key) {
        Preference preference = ObjectSelect.query(Preference)
                .where(Preference.NAME.eq(key))
                .selectOne(getCayenneService().newContext)
        Assertions.assertNotNull(preference)
        return preference.id
    }

    private List<Map<String, Object>> auditEntries(Long preferenceId) {
        // audit entries are written asynchronously by the single AuditService thread, wait for the queued ones
        injector.getInstance(AuditService).executorService.submit({} as Runnable).get()

        List<Map<String, Object>> result = []
        Connection connection = dataSource.connection
        try {
            PreparedStatement statement = connection.prepareStatement(
                    'SELECT action, message, systemUserId FROM Audit WHERE entityName = ? AND entityId = ? ORDER BY id')
            statement.setString(1, Preference.simpleName)
            statement.setLong(2, preferenceId)
            ResultSet resultSet = statement.executeQuery()
            while (resultSet.next()) {
                result << ([action      : resultSet.getString('action'),
                            message     : resultSet.getString('message'),
                            systemUserId: resultSet.getLong('systemUserId')] as Map<String, Object>)
            }
            resultSet.close()
            statement.close()
        } finally {
            connection.close()
        }
        return result
    }
}
