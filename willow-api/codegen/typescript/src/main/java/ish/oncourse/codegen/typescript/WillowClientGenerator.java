package ish.oncourse.codegen.typescript;

import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenParameter;
import io.swagger.codegen.CodegenType;
import io.swagger.codegen.languages.AbstractTypeScriptClientCodegen;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.FileProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import ish.oncourse.codegen.common.Launcher;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Generator for Willow React Application
 *
 * @author Ibragimov Ruslan
 * @since 0.1
 */
public class WillowClientGenerator extends AbstractTypeScriptClientCodegen implements CodegenConfig {

    public static void main(String[] args) {
        new Launcher(new WillowClientGenerator()).run();
    }

    // source folder where to write the files
    private String sourceFolder = "src";
    private String apiVersion = "1.0.0";

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

    public WillowClientGenerator() {
        super();

        outputFolder = "../../../react";

        modelTemplateFiles.put("model.mustache", ".ts");
        apiTemplateFiles.put("api.mustache", ".ts");

        typeMapping.put("DateTime", "string");
        typeMapping.put("any", "any");
        typeMapping.put("Date", "string");

        templateDir = "willow-typescript";
        apiPackage = "js.http";
        modelPackage = "js.model";
        additionalProperties.put("apiVersion", apiVersion);
    }

    /**
     * Escapes a reserved word as defined in the `reservedWords` array. Handle escaping
     * those terms here.  This logic is only called if a variable matches the reseved words
     *
     * @return the escaped term
     */
    @Override
    public String escapeReservedWord(String name) {
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
    public String getSwaggerType(Property p) {
        String swaggerType = super.getSwaggerType(p);
        String type = null;
        if (typeMapping.containsKey(swaggerType)) {
            type = typeMapping.get(swaggerType);
            if (languageSpecificPrimitives.contains(type))
                return type;
        } else
            type = swaggerType;
        return toModelName(type);
    }

    @Override
    public String getTypeDeclaration(Property p) {
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
    public Map<String, Object> postProcessModels(Map<String, Object> objs) {
        // recursively add import for mapping one type to multiple imports
        List<Map<String, String>> recursiveImports = (List<Map<String, String>>) objs.get("imports");
        if (recursiveImports == null)
            return objs;

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
    public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
        CodegenOperation operation = ((Map<String, List<CodegenOperation>>) objs.get("operations")).get("operation").get(0);

        if (operation.pathParams != null && operation.pathParams.size() > 0) {
            for (CodegenParameter it : operation.pathParams) {
                operation.path = operation.path.replace(
                        "{" + it.paramName + "}",
                        "${" + it.paramName + "}"
                );
            }
        }

        return objs;
    }
}