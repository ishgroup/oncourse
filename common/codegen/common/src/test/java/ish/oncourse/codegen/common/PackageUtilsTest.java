package ish.oncourse.codegen.common;

import org.junit.Test;

import static ish.oncourse.codegen.common.PackageUtils.containsPackage;
import static ish.oncourse.codegen.common.PackageUtils.deletePackage;
import static ish.oncourse.codegen.common.PackageUtils.getClassName;
import static ish.oncourse.codegen.common.PackageUtils.withSeparator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ibragimov Ruslan
 */
public class PackageUtilsTest {
    @Test
    public void getClassNameTest() throws Exception {
        final String actual = getClassName(withSeparator("com", "ish", "autocomplete", "Item"));

        assertEquals("Item", actual);
    }

    @Test
    public void containsPackageTest() throws Exception {
        assertTrue(containsPackage(withSeparator("com", "ish", "autocomplete", "Item")));
        assertTrue(containsPackage(withSeparator("autocomplete", "Item")));

        assertFalse(containsPackage("Item"));
    }

    @Test
    public void deletePackageTest() throws Exception {
        assertEquals(
                "List<Product>",
                deletePackage(
                        withSeparator("List<com", "ish", "web", "Product>"),
                        withSeparator("com", "ish", "web")
                )
        );
        assertEquals(
                "List<Product>",
                deletePackage(withSeparator("List<web", "Product>"), "web")
        );
    }
}