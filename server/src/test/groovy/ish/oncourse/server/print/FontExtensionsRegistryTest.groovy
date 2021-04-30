package ish.oncourse.server.print

import net.sf.jasperreports.engine.fonts.FontExtensionsRegistry
import net.sf.jasperreports.engine.fonts.FontFamily
import net.sf.jasperreports.extensions.ExtensionsRegistry
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals

/**
 * Created by anarut on 2/21/17.
 */
class FontExtensionsRegistryTest {

    @Test
    void test() {
        ExtensionsRegistry registry = new FontExtensionsRegistry(['resources/fonts/fonts.xml'])
        List<FontFamily> extensions = registry.getExtensions(FontFamily)

        assertEquals(4, extensions.size())
        assertEquals(1, extensions.findAll{ e -> e.name.equals('Open Sans')}.size())
        assertEquals(1, extensions.findAll{ e -> e.name.equals('Open Sans Bold')}.size())
        assertEquals(1, extensions.findAll{ e -> e.name.equals('Open Sans Light')}.size())
        assertEquals(1, extensions.findAll{ e -> e.name.equals('Open Sans Condensed Light')}.size())
    }

}
