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

import ish.oncourse.server.ICayenneService;
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.Query

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*
import static org.mockito.Mockito.*

class ClusteredAutoincrementServiceTest {

    ClusteredAutoincrementService service

    @Before
    void initService() {
        def context = mock(DataContext.class)
        when(context.selectOne(any(Query.class)))
                .thenReturn(123L)

        def cayenne = mock(ICayenneService.class)
        when(cayenne.getNewNonReplicatingContext())
                .thenReturn(context)

        service = spy(new ClusteredAutoincrementService(cayenne))
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
        assertEquals(123L, nextId)

        nextId = service.nextInvoiceNumber
        assertEquals(124L, nextId)

        nextId = service.nextInvoiceNumber
        assertEquals(125L, nextId)

        verify(service, times(1)).nextId(anyString())
    }

}
