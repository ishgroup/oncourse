package ish.oncourse.codegen.common;

import org.junit.Test;

import static ish.oncourse.codegen.common.CodegenConstants.SEP;
import static ish.oncourse.codegen.common.PackageUtils.containsPackage;
import static ish.oncourse.codegen.common.PackageUtils.deletePackage;
import static ish.oncourse.codegen.common.PackageUtils.getClassName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ibragimov Ruslan
 */
public class PackageUtilsTest {
    @Test
    public void getClassNameTest() throws Exception {
        final String actual = getClassName("com" + SEP + "ish" + SEP + "autocomplete" + SEP + "Item");

        assertEquals("Item", actual);
    }

    @Test
    public void containsPackageTest() throws Exception {
        assertTrue(containsPackage("com" + SEP + "ish" + SEP + "autocomplete" + SEP + "Item"));
        assertTrue(containsPackage("autocomplete" + SEP + "Item"));

        assertFalse(containsPackage("Item"));
    }

    @Test
    public void deletePackageTest() throws Exception {
        assertEquals(
                "List<Product>",
                deletePackage(
                        "List<com" + SEP + "ish" + SEP + "web" + SEP + "Product>",
                        "com" + SEP + "ish" + SEP + "web"
                )
        );
        assertEquals(
                "List<Product>",
                deletePackage("List<web" + SEP + "Product>", "web")
        );
    }
}