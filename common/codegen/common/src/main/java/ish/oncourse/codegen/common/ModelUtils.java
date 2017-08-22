package ish.oncourse.codegen.common;

import io.swagger.codegen.CodegenProperty;

import java.util.List;

import static ish.oncourse.codegen.common.PackageUtils.containsPackage;
import static ish.oncourse.codegen.common.PackageUtils.deletePackage;
import static ish.oncourse.codegen.common.PackageUtils.getClassName;
import static ish.oncourse.codegen.common.PackageUtils.getPackage;

/**
 *  Contains utility method related to support of model in codegen.
 *
 * @author Ibragimov Ruslan
 */
public final class ModelUtils {
    private ModelUtils() {
    }

    /**
     * Delete package from model properties.
     *
     * @param vars properties in current model
     */
    public static void processVars(final List<CodegenProperty> vars) {
        for (CodegenProperty var : vars) {
            if (containsPackage(var.datatypeWithEnum)) {
                var.baseType = getClassName(var.baseType);
                var.datatypeWithEnum = deletePackage(var.datatypeWithEnum, getPackage(var.complexType));
                var.defaultValue = deletePackage(var.defaultValue, getPackage(var.complexType));
                var.datatype = deletePackage(var.datatype, getPackage(var.complexType));

                if (var.items != null) {
                    var.items.datatypeWithEnum = getClassName(var.items.datatypeWithEnum);
                    var.items.datatype = getClassName(var.items.datatype);
                }
            }
        }
    }
}
