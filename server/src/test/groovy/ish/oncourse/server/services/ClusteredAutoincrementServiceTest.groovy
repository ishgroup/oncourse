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

import ish.oncourse.server.ICayenneService
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.Query
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.*

class ClusteredAutoincrementServiceTest {

    ClusteredAutoincrementService service

    @BeforeEach
    void initService() {
        def context = mock(DataContext.class)
        when(context.selectOne(any(Query.class)))
                .thenReturn(123L)

        def cayenne = mock(ICayenneService.class)
        when(cayenne.getNewNonReplicatingContext())
                .thenReturn(context)

        service = spy(new ClusteredAutoincrementService(cayenne))

        doReturn(123L).when(service)
                .nextId("student")
        doReturn(12L).when(service)
                .nextId("invoice")
    }

    @Test
    void getStudentId() {
        long nextId

        nextId = service.nextStudentNumber
        assertEquals(123L, nextId)

        nextId = service.nextStudentNumber
        assertEquals(124L, nextId)

        nextId = service.nextStudentNumber
        assertEquals(125L, nextId)

        verify(service, times(1)).nextId(anyString())
    }

    @Test
    void getInvoiceId() {
        long nextId

        nextId = service.nextInvoiceNumber
        assertEquals(12L, nextId)

        nextId = service.nextInvoiceNumber
        assertEquals(13L, nextId)

        nextId = service.nextInvoiceNumber
        assertEquals(14L, nextId)

        verify(service, times(1)).nextId(anyString())
    }

}
