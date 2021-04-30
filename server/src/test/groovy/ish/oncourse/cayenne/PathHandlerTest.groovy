package ish.oncourse.cayenne

import org.junit.jupiter.api.Test

import static org.junit.Assert.*

/**
 * Created by anarut on 8/25/16.
 */
class PathHandlerTest {

    @Test
    void testPathHandler() {
        PathHandler pathHandler1 = PathHandler.valueOf("invoiceLine.description")
        assertTrue(pathHandler1.hasJoins())
        assertEquals("invoiceLine", pathHandler1.getPrefixProperty())
        assertEquals("description", pathHandler1.getPathForPrefixProperty())


        PathHandler pathHandler2 = PathHandler.valueOf("invoiceLine+.description")
        assertTrue(pathHandler2.hasJoins())
        assertEquals("invoiceLine", pathHandler2.getPrefixProperty())
        assertEquals("description", pathHandler2.getPathForPrefixProperty())

        PathHandler pathHandler3 = PathHandler.valueOf("invoiceLine.contact.description")
        assertTrue(pathHandler3.hasJoins())
        assertEquals("invoiceLine", pathHandler3.getPrefixProperty())
        assertEquals("contact.description", pathHandler3.getPathForPrefixProperty())

        PathHandler pathHandler4 = PathHandler.valueOf("invoiceLine+.contact.description")
        assertTrue(pathHandler4.hasJoins())
        assertEquals("invoiceLine", pathHandler4.getPrefixProperty())
        assertEquals("contact.description", pathHandler4.getPathForPrefixProperty())


        PathHandler pathHandler5 = PathHandler.valueOf("invoiceLine+.contact+.description")
        assertTrue(pathHandler5.hasJoins())
        assertEquals("invoiceLine", pathHandler5.getPrefixProperty())
        assertEquals("contact+.description", pathHandler5.getPathForPrefixProperty())


        PathHandler pathHandler6 = PathHandler.valueOf("invoiceLine.contact+.description")
        assertTrue(pathHandler6.hasJoins())
        assertEquals("invoiceLine", pathHandler6.getPrefixProperty())
        assertEquals("contact+.description", pathHandler6.getPathForPrefixProperty())


        PathHandler pathHandler7 = PathHandler.valueOf("invoiceLine")
        assertFalse(pathHandler7.hasJoins())
        assertNull(pathHandler7.getPrefixProperty())
        assertNull(pathHandler7.getPathForPrefixProperty())


        PathHandler pathHandler8 = PathHandler.valueOf("invoiceLine.contact+.description.date")
        assertTrue(pathHandler8.hasJoins())
        assertEquals("invoiceLine", pathHandler8.getPrefixProperty())
        assertEquals("contact+.description.date", pathHandler8.getPathForPrefixProperty())
    }
}
