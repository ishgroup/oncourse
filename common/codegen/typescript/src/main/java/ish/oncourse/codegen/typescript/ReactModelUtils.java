package ish.oncourse.codegen.typescript;

import org.apache.commons.lang3.StringUtils;

import static ish.oncourse.codegen.common.CodegenConstants.SEP;

/**
 * Model Utils specific for react codegen.
 *
 * @author Ibragimov Ruslan
 */
public final class ReactModelUtils {
    private ReactModelUtils() {
    }

    /**
     * Sample: autocomplete/Import
     *
     * @param name name with SEP
     * @return prefix for path
     */
    public static String getModelImportPathPrefix(final String name) {
        final int length = name.split("\\.").length;
        return StringUtils.repeat("../", length-1);
    }
}
