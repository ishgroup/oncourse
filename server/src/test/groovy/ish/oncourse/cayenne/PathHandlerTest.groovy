package ish.oncourse.cayenne

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


@CompileStatic
class PathHandlerTest {

    @Test
    void testPathHandler() {
        PathHandler pathHandler1 = PathHandler.valueOf("invoiceLine.description")
        Assertions.assertTrue(pathHandler1.hasJoins())
        Assertions.assertEquals("invoiceLine", pathHandler1.getPrefixProperty())
        Assertions.assertEquals("description", pathHandler1.getPathForPrefixProperty())


        PathHandler pathHandler2 = PathHandler.valueOf("invoiceLine+.description")
        Assertions.assertTrue(pathHandler2.hasJoins())
        Assertions.assertEquals("invoiceLine", pathHandler2.getPrefixProperty())
        Assertions.assertEquals("description", pathHandler2.getPathForPrefixProperty())

        PathHandler pathHandler3 = PathHandler.valueOf("invoiceLine.contact.description")
        Assertions.assertTrue(pathHandler3.hasJoins())
        Assertions.assertEquals("invoiceLine", pathHandler3.getPrefixProperty())
        Assertions.assertEquals("contact.description", pathHandler3.getPathForPrefixProperty())

        PathHandler pathHandler4 = PathHandler.valueOf("invoiceLine+.contact.description")
        Assertions.assertTrue(pathHandler4.hasJoins())
        Assertions.assertEquals("invoiceLine", pathHandler4.getPrefixProperty())
        Assertions.assertEquals("contact.description", pathHandler4.getPathForPrefixProperty())


        PathHandler pathHandler5 = PathHandler.valueOf("invoiceLine+.contact+.description")
        Assertions.assertTrue(pathHandler5.hasJoins())
        Assertions.assertEquals("invoiceLine", pathHandler5.getPrefixProperty())
        Assertions.assertEquals("contact+.description", pathHandler5.getPathForPrefixProperty())


        PathHandler pathHandler6 = PathHandler.valueOf("invoiceLine.contact+.description")
        Assertions.assertTrue(pathHandler6.hasJoins())
        Assertions.assertEquals("invoiceLine", pathHandler6.getPrefixProperty())
        Assertions.assertEquals("contact+.description", pathHandler6.getPathForPrefixProperty())


        PathHandler pathHandler7 = PathHandler.valueOf("invoiceLine")
        Assertions.assertFalse(pathHandler7.hasJoins())
        Assertions.assertNull(pathHandler7.getPrefixProperty())
        Assertions.assertNull(pathHandler7.getPathForPrefixProperty())


        PathHandler pathHandler8 = PathHandler.valueOf("invoiceLine.contact+.description.date")
        Assertions.assertTrue(pathHandler8.hasJoins())
        Assertions.assertEquals("invoiceLine", pathHandler8.getPrefixProperty())
        Assertions.assertEquals("contact+.description.date", pathHandler8.getPathForPrefixProperty())
    }
}
