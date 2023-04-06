/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.api.v1.model.FilterDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Account
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.parser.ASTBetween
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations

import static org.junit.jupiter.api.Assertions.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class FilterFunctionsTest {

    private static AqlService aqlService

    private static FilterDTO filter

    private static ERROR_MESSAGE = "Filter name can only contain letters, numbers, '-', '_' and spaces."

    @BeforeAll
    static void setUp() {
        MockitoAnnotations.initMocks(this)
        filter = new FilterDTO()
        filter.setEntity("Account")
        filter.setExpression("createdOn last year")
        Expression expression = new ASTBetween()
        CompilationResult aqlResult = new CompilationResult(expression)
        aqlService = mock(AqlService.class)
        when(aqlService.compile(filter.expression, Account.class, null)).thenReturn(aqlResult)
    }

    @Test
    void validateFilterWhenNameIsValid1() {
        filter.setName("Test Name 123")
        ValidationErrorDTO error = FilterFunctions.validateFilter(aqlService, null, filter)
        assertNull(error)
    }

    @Test
    void validateFilterWhenNameIsValid2() {
        filter.setName("TestName123")
        ValidationErrorDTO error = FilterFunctions.validateFilter(aqlService, null, filter)
        assertNull(error)
    }

    @Test
    void validateFilterWhenNameIsValid3() {
        filter.setName("1-Test_Name-12_3")
        ValidationErrorDTO error = FilterFunctions.validateFilter(aqlService, null, filter)
        assertNull(error)
    }

    @Test
    void validateFilterWhenNameIsNotValid1() {
        filter.setName("Date > 10")
        ValidationErrorDTO error = FilterFunctions.validateFilter(aqlService, null, filter)
        assertNotNull(error)
        assertEquals(ERROR_MESSAGE, error.errorMessage)
    }

    @Test
    void validateFilterWhenNameIsNotValid2() {
        filter.setName("Test (days)")
        ValidationErrorDTO error = FilterFunctions.validateFilter(aqlService, null, filter)
        assertNotNull(error)
        assertEquals(ERROR_MESSAGE, error.errorMessage)
    }

    @Test
    void validateFilterWhenNameIsNotValid3() {
        filter.setName("last day + filters")
        ValidationErrorDTO error = FilterFunctions.validateFilter(aqlService, null, filter)
        assertNotNull(error)
        assertEquals(ERROR_MESSAGE, error.errorMessage)
    }
}