package ish.oncourse.codegen.typescript;

import org.junit.Test;

import static ish.oncourse.codegen.common.PackageUtils.withSeparator;
import static ish.oncourse.codegen.typescript.ReactModelUtils.getModelImportPathPrefix;
import static org.junit.Assert.assertEquals;

/**
 * @author Ibragimov Ruslan
 */
public class ReactModelUtilsTest {

    @Test
    public void getModelImportPathPrefixTest() throws Exception {
        assertEquals("", getModelImportPathPrefix("Model"));
        assertEquals("../", getModelImportPathPrefix("autocomplete.Item"));
        assertEquals(
                "../../../",
                getModelImportPathPrefix("com.ish.autocomplete.Item")
        );
    }
}