package ish.oncourse.server.print


import groovy.transform.CompileStatic
import net.sf.jasperreports.engine.fonts.FontExtensionsRegistry
import net.sf.jasperreports.engine.fonts.FontFamily
import net.sf.jasperreports.extensions.ExtensionsRegistry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class FontExtensionsRegistryTest {

    
    @Test
    void test() {
        ExtensionsRegistry registry = new FontExtensionsRegistry(['resources/fonts/fonts.xml'])
        List<FontFamily> extensions = registry.getExtensions(FontFamily)

        Assertions.assertEquals(4, extensions.size())
        Assertions.assertEquals(1, extensions.findAll { e -> e.name.equals('Open Sans') }.size())
        Assertions.assertEquals(1, extensions.findAll { e -> e.name.equals('Open Sans Bold') }.size())
        Assertions.assertEquals(1, extensions.findAll { e -> e.name.equals('Open Sans Light') }.size())
        Assertions.assertEquals(1, extensions.findAll { e -> e.name.equals('Open Sans Condensed Light') }.size())
    }

}
