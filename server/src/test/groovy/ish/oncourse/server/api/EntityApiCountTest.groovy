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

package ish.oncourse.server.api

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.aql.AqlService
import ish.oncourse.server.api.v1.model.DataResponseDTO
import ish.oncourse.server.api.v1.model.SearchCountDTO
import ish.oncourse.server.api.v1.model.SearchErrorDTO
import ish.oncourse.server.api.v1.model.SearchQueryDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.impl.EntityApiImpl
import ish.oncourse.server.preference.UserPreferenceService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.ws.rs.ClientErrorException

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/api/entityApiCountTest.xml")
class EntityApiCountTest extends TestWithDatabase {

    private EntityApiImpl api

    @BeforeEach
    void setupApi() {
        api = new EntityApiImpl()
        api.cayenneService = cayenneService
        api.aql = injector.getInstance(AqlService)
        api.preference = injector.getInstance(UserPreferenceService)
    }

    @Test
    void testValidSearchReturnsCount() {
        SearchCountDTO result = api.count("Room", "seatedCapacity > 20")

        Assertions.assertTrue(result.valid)
        Assertions.assertEquals(3L, result.count)
        Assertions.assertTrue(result.errors.empty)
    }

    @Test
    void testSearchThroughRelationshipCountsDistinctRecords() {
        // site 1 matches through two rooms, it is still one site
        Assertions.assertEquals(2L, api.count("Site", "rooms.seatedCapacity > 20").count)
        Assertions.assertEquals(3L, api.count("Room", "site.name = 'Main campus'").count)
    }

    @Test
    void testEmptySearchCountsAllRecords() {
        Assertions.assertEquals(4L, api.count("Room", null).count)
        Assertions.assertEquals(4L, api.count("Room", "  ").count)
        Assertions.assertEquals(3L, api.count("Site", "").count)
    }

    @Test
    void testEntityNameIsCaseInsensitiveForFirstLetter() {
        Assertions.assertEquals(3L, api.count("room", "seatedCapacity > 20").count)
    }

    @Test
    void testSyntaxErrorReturnsPosition() {
        SearchCountDTO result = api.count("Room", "seatedCapacity >")

        Assertions.assertFalse(result.valid)
        Assertions.assertNull(result.count)
        Assertions.assertFalse(result.errors.empty)
        SearchErrorDTO error = result.errors.first()
        Assertions.assertNotNull(error.message)
        Assertions.assertEquals(1, error.line)
        Assertions.assertEquals(16, error.position)
    }

    @Test
    void testUnknownAttributeReturnsLineAndPosition() {
        SearchCountDTO result = api.count("Room", "seatedCapacity > 20\nand site.foo = 1")

        Assertions.assertFalse(result.valid)
        Assertions.assertNull(result.count)
        Assertions.assertEquals(1, result.errors.size())
        SearchErrorDTO error = result.errors.first()
        Assertions.assertEquals("Can't resolve attribute 'foo' for entity 'Site'", error.message)
        Assertions.assertEquals(2, error.line)
        Assertions.assertEquals(9, error.position)
    }

    @Test
    void testErrorWithoutPositionOmitsLineAndPosition() {
        SearchCountDTO result = api.count("Room", "seatedCapacity = 'abc'")

        Assertions.assertFalse(result.valid)
        Assertions.assertEquals(1, result.errors.size())
        SearchErrorDTO error = result.errors.first()
        Assertions.assertNotNull(error.message)
        Assertions.assertNull(error.line)
        Assertions.assertNull(error.position)
    }

    @Test
    void testUnclosedQuoteReturnsLexerError() {
        SearchCountDTO result = api.count("Room", 'name is "abc')

        Assertions.assertFalse(result.valid)
        Assertions.assertNull(result.count)
        // the lexer error comes first, then the parser error it causes
        Assertions.assertEquals(2, result.errors.size())
        SearchErrorDTO error = result.errors.first()
        Assertions.assertEquals("token recognition error at: '\"abc'", error.message)
        Assertions.assertEquals(1, error.line)
        Assertions.assertEquals(8, error.position)
        Assertions.assertEquals(12, result.errors[1].position)

        error = api.count("Room", "name is 'abc").errors.first()
        Assertions.assertEquals("token recognition error at: ''abc'", error.message)
        Assertions.assertEquals(8, error.position)
    }

    @Test
    void testStrayCharacterReturnsLexerError() {
        SearchCountDTO result = api.count("Room", "name is 'Main 1' \$")

        Assertions.assertFalse(result.valid)
        Assertions.assertNull(result.count)
        Assertions.assertEquals(1, result.errors.size())
        SearchErrorDTO error = result.errors.first()
        Assertions.assertEquals("token recognition error at: '\$'", error.message)
        Assertions.assertEquals(1, error.line)
        Assertions.assertEquals(17, error.position)

        error = api.count("Room", "seatedCapacity > 20\nand name ^ 'Main 1'").errors.first()
        Assertions.assertEquals("token recognition error at: '^'", error.message)
        Assertions.assertEquals(2, error.line)
        Assertions.assertEquals(9, error.position)
    }

    @Test
    void testUnknownEntityIsBadRequest() {
        ClientErrorException e = Assertions.assertThrows(ClientErrorException) {
            api.count("NoSuchEntity", "id > 0")
        }
        Assertions.assertEquals(400, e.response.status)
        Assertions.assertEquals("Unknown entity: 'NoSuchEntity'.", (e.response.entity as ValidationErrorDTO).errorMessage)
    }

    @Test
    void testListSearchIsUnchanged() {
        DataResponseDTO response = api.getAll("Room", new SearchQueryDTO(search: "seatedCapacity > 20"))
        Assertions.assertEquals(3L, response.filteredCount)
        Assertions.assertEquals(3, response.rows.size())

        ClientErrorException e = Assertions.assertThrows(ClientErrorException) {
            api.getAll("Room", new SearchQueryDTO(search: "seatedCapacity >"))
        }
        Assertions.assertEquals(400, e.response.status)
        Assertions.assertEquals("Invalid search expression: 'seatedCapacity >'.", (e.response.entity as ValidationErrorDTO).errorMessage)

        // list search still skips characters the lexer can't match
        Assertions.assertEquals(1L, api.getAll("Room", new SearchQueryDTO(search: "name is 'Main 1' \$")).filteredCount)
        e = Assertions.assertThrows(ClientErrorException) {
            api.getAll("Room", new SearchQueryDTO(search: 'name is "abc'))
        }
        Assertions.assertEquals("Invalid search expression: 'name is \"abc'.", (e.response.entity as ValidationErrorDTO).errorMessage)
    }
}
