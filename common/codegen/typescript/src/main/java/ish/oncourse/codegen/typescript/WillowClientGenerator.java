package ish.oncourse.codegen.typescript;

import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.CodegenModel;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenParameter;
import io.swagger.codegen.CodegenType;
import io.swagger.codegen.languages.AbstractTypeScriptClientCodegen;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.FileProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import ish.oncourse.codegen.common.Launcher;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static ish.oncourse.codegen.common.CodegenConstants.SEP;
import static ish.oncourse.codegen.common.ModelUtils.processVars;
import static ish.oncourse.codegen.common.PackageUtils.containsPackage;
import static ish.oncourse.codegen.common.PackageUtils.getClassName;
import static ish.oncourse.codegen.common.PackageUtils.normalizePackage;

/**
 * Generator for Willow React Application.
 *
 * @author Ibragimov Ruslan
 * @since 0.1
 */
public class WillowClientGenerator extends AbstractTypeScriptClientCodegen implements CodegenConfig {

    // source folder where to write the files
    private String sourceFolder = "src";
    private String apiVersion = "1.0.0";

    public WillowClientGenerator() {
        super();

        // working dir: /common/codegen/typescript
        outputFolder = "../../../checkout";

        modelTemplateFiles.put("model.mustache", ".ts");
        apiTemplateFiles.put("api.mustache", ".ts");

        typeMapping.put("DateTime", "string");
        typeMapping.put("any", "any");
        typeMapping.put("Date", "string");
        typeMapping.put("date", "string");

        templateDir = "willow-typescript";
        apiPackage = "js.http";
        modelPackage = "js.model";
        additionalProperties.put("apiVersion", apiVersion);
    }

    public static void main(String[] args) {
        new Launcher(new WillowClientGenerator()).run();
    }

    /**
     * Configures the type of generator.
     *
     * @return the CodegenType for this generator
     * @see io.swagger.codegen.CodegenType
     */
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    /**
     * Configures a friendly name for the generator.  This will be used by the generator
     * to select the library with the -l flag.
     *
     * @return the friendly name for the generator
     */
    public String getName() {
        return "willow-typescript";
    }

    /**
     * Returns human-friendly help for the generator.  Provide the consumer with help
     * tips, parameters here
     *
     * @return A string value for the help message
     */
    public String getHelp() {
        return "Generates a willow-typescript client library.";
    }

    /**
     * Escapes a reserved word as defined in the `reservedWords` array. Handle escaping
     * those terms here.  This logic is only called if a variable matches the reseved words
     *
     * @return the escaped term
     */
    @Override
    public String escapeReservedWord(final String name) {
        return "_" + name;  // add an underscore to the name
    }

    /**
     * Location to write model files.  You can use the modelPackage() as defined when the class is
     * instantiated
     */
    public String modelFileFolder() {
        return outputFolder + "/" + sourceFolder + "/" + modelPackage().replace('.', File.separatorChar);
    }

    /**
     * Location to write api files.  You can use the apiPackage() as defined when the class is
     * instantiated
     */
    @Override
    public String apiFileFolder() {
        return outputFolder + "/" + sourceFolder + "/" + apiPackage().replace('.', File.separatorChar);
    }

    /**
     * Optional - swagger type conversion.  This is used to map swagger types in a `Property` into
     * either language specific types via `typeMapping` or into complex models if there is not a mapping.
     *
     * @return a string value of the type or complex model for this property
     * @see io.swagger.models.properties.Property
     */
    @Override
    public String getTypeDeclaration(final Property p) {
        if (p instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) p;
            Property inner = ap.getItems();
            return getTypeDeclaration(inner) + "[]";
        } else if (p instanceof MapProperty) {
            MapProperty mp = (MapProperty) p;
            Property inner = mp.getAdditionalProperties();
            return "{ [key: string]: " + getTypeDeclaration(inner) + "; }";
        } else if (p instanceof FileProperty) {
            return "any";
        }
        return super.getTypeDeclaration(p);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> postProcessModels(final Map<String, Object> objs) {
        // recursively add import for mapping one type to multiple imports
        List<Map<String, String>> recursiveImports = (List<Map<String, String>>) objs.get("imports");
        if (recursiveImports == null) {
            return objs;
        }

        ListIterator<Map<String, String>> listIterator = recursiveImports.listIterator();
        while (listIterator.hasNext()) {
            String _import = listIterator.next().get("import");
            // if the import package happens to be found in the importMapping (key)
            // add the corresponding import package to the list
            if (importMapping.containsKey(_import)) {
                Map<String, String> newImportMap = new HashMap<String, String>();
                newImportMap.put("import", importMapping.get(_import));
                listIterator.add(newImportMap);
            }
        }

        return postProcessModelsEnum(objs);
    }

    @Override
    public String toModelImport(String name) {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> postProcessOperations(final Map<String, Object> objs) {
        final Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
        List<CodegenOperation> operationList = (List<CodegenOperation>) operations.get("operation");

        for (CodegenOperation codegenOperation : operationList) {
            if (codegenOperation.pathParams != null && codegenOperation.pathParams.size() > 0) {
                for (CodegenParameter it : codegenOperation.pathParams) {
                    codegenOperation.path = codegenOperation.path.replace(
                            "{" + it.paramName + "}",
                            "${" + it.paramName + "}"
                    );
                }
            }
        }

        final List<Map<String, Object>> imports = (List<Map<String, Object>>) objs.get("imports");
        for (Map<String, Object> modelImport : imports) {
            final String anImport = (String) modelImport.get("import");
            modelImport.put("import", new CustomImport(getClassName(anImport), normalizePackage(anImport, "/")));
        }

        for (CodegenOperation operation : operationList) {
            if (containsPackage(operation.returnType)) {
                operation.returnType = getClassName(operation.returnType);
                operation.returnBaseType = getClassName(operation.returnBaseType);
            }

            for (CodegenParameter parameter : operation.allParams) {
                if (containsPackage(parameter.baseType)) {
                    parameter.baseType = getClassName(parameter.baseType);
                    parameter.dataType = getClassName(parameter.dataType);
                }
            }
        }

        return objs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> postProcessAllModels(final Map<String, Object> objs) {
        for (Map.Entry<String, Object> obj : objs.entrySet()) {
            // Since objs generalized as Object, we should check
            if (obj.getValue() instanceof Map<?, ?>) {
                final Map<String, Object> value = (Map<String, Object>) obj.getValue();
                final String className = (String) value.get("classname");

                final List<Map<String, Object>> modelsList = (List<Map<String, Object>>) value.get("models");
                if (modelsList.size() > 1) {
                    LOGGER.error("Expected only one model in modelsList.");
                }
                final Map<String, Object> modelsMap = modelsList.get(0);
                final CodegenModel model = (CodegenModel) modelsMap.get("model");

                if (containsPackage(className)) {
                    final String newClassName = getClassName(className);
                    model.classname = newClassName;
                    LOGGER.info("Replace classname {} with {}.", className, newClassName);
                    value.put("classname", newClassName);
                }

                String importPathPrefix = ReactModelUtils.getModelImportPathPrefix(obj.getKey());
                // Overwrite imports in model
                final ArrayList<CustomImport> customImports = new ArrayList<>();
                for (String modelImport : model.imports) {
                    customImports.add(new CustomImport(
                            getClassName(modelImport),
                            importPathPrefix + normalizePackage(modelImport, "/"))
                    );
                }
                value.put("imports", customImports);

                processVars(model.vars);
            } else {
                LOGGER.error("Unexpected behaviour in postProcessAllModels, objs value is not a Map.");
            }
        }

        return super.postProcessAllModels(objs);
    }

    @Override
    public String toModelName(final String name) {
        String nameOnly = sanitizeName(name);
        String modelPackage = "";
        if (nameOnly.contains("_")) {
            final int lastIndexOf = nameOnly.lastIndexOf("_");

            modelPackage = nameOnly
                    .substring(0, lastIndexOf)
                    .replace("_", SEP)
                    .toLowerCase() + SEP;
            nameOnly = nameOnly
                    .substring(lastIndexOf + 1, nameOnly.length());
        }

        String nameWithPrefixSuffix = nameOnly;
        if (!StringUtils.isEmpty(modelNamePrefix)) {
            // add '_' so that model name can be camelized correctly
            nameWithPrefixSuffix = modelNamePrefix + "_" + nameWithPrefixSuffix;
        }

        if (!StringUtils.isEmpty(modelNameSuffix)) {
            // add '_' so that model name can be camelized correctly
            nameWithPrefixSuffix = nameWithPrefixSuffix + "_" + modelNameSuffix;
        }

        // camelize the model name
        // phone_number => PhoneNumber
        final String camelizedName = camelize(nameWithPrefixSuffix);

        // model name cannot use reserved keyword, e.g. return
        if (isReservedWord(camelizedName)) {
            final String modelName = "Model" + camelizedName;
            LOGGER.warn(camelizedName + " (reserved word) cannot be used as model name. Renamed to " + modelName);
            return modelName;
        }

        // model name starts with number
        if (camelizedName.matches("^\\d.*")) {
            final String modelName = "Model" + camelizedName; // e.g. 200Response => Model200Response (after camelize)
            LOGGER.warn(name + " (model name starts with number) cannot be used as model name. Renamed to " + modelName);
            return modelName;
        }


        if (languageSpecificPrimitives.contains(camelizedName)) {
            String modelName = "Model" + camelizedName;
            LOGGER.warn(camelizedName + " (model name matches existing language type) cannot be used as a model name. Renamed to " + modelName);
            return modelName;
        }

        return modelPackage + camelizedName;
    }
}