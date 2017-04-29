package ish.oncourse.codegen.cxf;

import org.junit.Test;

import static ish.oncourse.codegen.common.CodegenConstants.SEP;
import static org.junit.Assert.assertEquals;

/**
 * Tests for JAX-RS swagger codegen.
 *
 * @author Ibragimov Ruslan
 */
public class WillowJaxRsGeneratorTest {
    @Test
    public void toModelName() throws Exception {
        WillowJaxRsGenerator generator = new WillowJaxRsGenerator();

        assertEquals("Item", generator.toModelName("Item"));
        assertEquals(
                "autocomplete" + SEP + "Item",
                generator.toModelName("autocomplete.Item")
        );
        assertEquals(
                "com" + SEP + "ish" + SEP + "autocomplete" + SEP + "Item",
                generator.toModelName("com.ish.autocomplete.Item")
        );
    }

    @Test
    public void toModelNameWithPrefixSuffix() throws Exception {
        WillowJaxRsGenerator generator = new WillowJaxRsGenerator();
        generator.setModelNamePrefix("Ish");
        generator.setModelNameSuffix("Model");

        assertEquals("IshItemModel", generator.toModelName("Item"));
        assertEquals(
                "autocomplete" + SEP + "IshItemModel",
                generator.toModelName("autocomplete.Item")
        );
        assertEquals(
                "com" + SEP + "ish" + SEP + "autocomplete" + SEP + "IshItemModel",
                generator.toModelName("com.ish.autocomplete.Item")
        );
    }
}