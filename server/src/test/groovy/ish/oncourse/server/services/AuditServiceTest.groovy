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
package ish.oncourse.server.services

import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow
import static org.mockito.Mockito.mock

@CompileStatic
class AuditServiceTest {

    @Test
    void testShutdownDoesNotThrow() {
        ICayenneService cayenneService = mock(ICayenneService)
        ISystemUserService systemUserService = mock(ISystemUserService)

        AuditService service = new AuditService(cayenneService, systemUserService)

        // Явный каст к Executable обязателен: под @CompileStatic Groovy не может выбрать между
        // перегрузками assertDoesNotThrow(Executable) и assertDoesNotThrow(ThrowingSupplier<T>),
        // потому что замыкание приводимо к обеим.
        assertDoesNotThrow({ service.shutdown() } as Executable)
    }

    @Test
    void testShutdownCanBeCalledMultipleTimes() {
        ICayenneService cayenneService = mock(ICayenneService)
        ISystemUserService systemUserService = mock(ISystemUserService)

        AuditService service = new AuditService(cayenneService, systemUserService)

        assertDoesNotThrow({
            service.shutdown()
            service.shutdown()
        } as Executable)
    }
}
