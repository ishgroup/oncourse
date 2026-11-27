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

package ish.oncourse.server.api.servlet

import ish.oncourse.server.ICayenneService
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.services.AuditContext
import ish.oncourse.server.services.AuditService
import ish.oncourse.types.AuditSource
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response
import org.junit.jupiter.api.Test

import javax.servlet.FilterChain
import javax.servlet.http.HttpSession

import static ish.oncourse.server.api.servlet.AngelSessionDataStore.USER_ATTRIBUTE
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ApiFilterAuditSourceTest {

    private Request request = mock(Request)
    private Response response = mock(Response)
    private IPermissionService permissionService = mock(IPermissionService)
    private ApiFilter filter = new ApiFilter(mock(AuditService), mock(ICayenneService), permissionService)

    @Test
    void testSessionRequestIsAuditedAsUser() {
        HttpSession session = mock(HttpSession)
        when(request.pathInfo).thenReturn('/v1/list/entity/audit/1')
        when(request.requestedSessionIdValid).thenReturn(true)
        when(request.getSession(false)).thenReturn(session)
        when(session.getAttribute(USER_ATTRIBUTE)).thenReturn(new Object())
        when(permissionService.authorize(request, response)).thenReturn(true)

        AuditContext seen = runChain()

        assertEquals(AuditSource.USER, seen.source)
        assertFalse(seen.recordValues)
        assertNull(AuditContext.current())
    }

    @Test
    void testRequestWithoutUserHasNoSource() {
        when(request.pathInfo).thenReturn('/v1/login')

        boolean called = false
        filter.doFilter(request, response, { req, res ->
            called = true
            assertNull(AuditContext.current())
        } as FilterChain)

        assertTrue(called)
    }

    private AuditContext runChain() {
        AuditContext seen = null
        filter.doFilter(request, response, { req, res -> seen = AuditContext.current() } as FilterChain)
        return seen
    }
}
