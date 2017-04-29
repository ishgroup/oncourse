package ish.oncourse.codegen.common;

import java.io.File;

import static ish.oncourse.codegen.common.CodegenConstants.SEP;

/**
 * Contains utility method related to support of packages in codegen;
 *
 * @author Ibragimov Ruslan
 */
public final class PackageUtils {
    private PackageUtils() {
    }

    /**
     * Strips package from class name, example:
     * <p>
     * input: com/ish/autocomplete/Item
     * output: Item
     *
     * @param name class name with package
     * @return class name without package
     */
    public static String getClassName(String name) {
        if (name == null) return null;

        final int lastIndexOf = name.lastIndexOf(File.separator);

        return name.substring(lastIndexOf + 1, name.length());
    }

    /**
     * Strips class name from name, example:
     * <p>
     * input: com/ish/autocomplete/Item
     * output: com/ish/autocomplete
     *
     * @param name class name with package
     * @return package
     */
    public static String getPackage(String name) {
        final int lastIndexOf = name.lastIndexOf(File.separator);

        return name.substring(0, lastIndexOf);
    }

    /**
     * Delete package from string via replace.
     *
     * @param name type with package and class name
     * @param modelPackage package to delete
     * @return type without package
     */
    public static String deletePackage(String name, String modelPackage) {
        return name.replaceAll(modelPackage + SEP, "");
    }

    /**
     * Checks if class name contains package
     *
     * @param name class name with/without package
     * @return true if string contains package information
     */
    public static boolean containsPackage(String name) {
        return name != null && name.contains(File.separator);
    }
}
