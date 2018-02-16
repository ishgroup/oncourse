package ish.oncourse.codegen.cxf;

import io.swagger.codegen.CodegenModel;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenParameter;
import io.swagger.codegen.CodegenProperty;
import io.swagger.codegen.SupportingFile;
import io.swagger.codegen.languages.AbstractJavaJAXRSServerCodegen;
import io.swagger.models.Operation;
import ish.oncourse.codegen.common.Launcher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.codegen.common.CodegenConstants.SEP;
import static ish.oncourse.codegen.common.ModelUtils.processVars;
import static ish.oncourse.codegen.common.PackageUtils.containsPackage;
import static ish.oncourse.codegen.common.PackageUtils.getClassName;
import static ish.oncourse.codegen.common.PackageUtils.getPackage;

/**
 * Generates interfaces and models for Willow-Api projects.
 *
 * @author Ibragimov Ruslan
 * @since 0.1
 */
public class  WillowJaxRsGenerator extends AbstractJavaJAXRSServerCodegen {
    private static final Logger LOGGER = LoggerFactory.getLogger(WillowJaxRsGenerator.class);


    public WillowJaxRsGenerator() {
        super();
        supportsInheritance = true;
        implFolder = "src/main/groovy";

        apiTemplateFiles.put("apiServiceImpl.mustache", ".groovy");
        apiTestTemplateFiles.clear();
        apiTestTemplateFiles.put("api_test.mustache", ".groovy");
        modelDocTemplateFiles.remove("model_doc.mustache");
        apiDocTemplateFiles.remove("api_doc.mustache");
        
        typeMapping.put("date", "LocalDateTime");
        importMapping.put("LocalDate", "java.time.LocalDateTime");
        importMapping.put("LocalDateTime", "java.time.LocalDateTime");
        typeMapping.put("DateTime", "LocalDateTime");
        typeMapping.put("number", "Double");
        embeddedTemplateDir = templateDir = "willow-jaxrs";
    }

    public static void main(String[] args) {
        for (String s : args) {
            System.out.println(s);
        }
        
        WillowJaxRsGenerator generator = new WillowJaxRsGenerator();
        generator.apiPackage = args[0];
        generator.modelPackage = args[1];
        generator.outputFolder = args[2];
        new Launcher(generator).run(args[3]);
    }
    
    @Override
    public void processOpts() {
        super.processOpts();

        supportingFiles.clear(); // Don't need extra files provided by AbstractJAX-RS & Java Codegen

        writeOptional(outputFolder, new SupportingFile(
                "swagger-codegen-ignore.mustache",
                "",
                ".swagger-codegen-ignore"
        ));
    }

    @Override
    public String getName() {
        return "willow-jaxrs";
    }

    @Override
    public String apiFilename(String templateName, String tag) {
        String suffix = apiTemplateFiles().get(templateName);
        String result = apiFileFolder() + '/' + toApiFilename(tag) + suffix;

        if (templateName.endsWith("Impl.mustache")) {
            int ix = result.lastIndexOf('/');
            result = result.substring(0, ix) + "/impl" + result.substring(ix, result.length() - 7) + "ServiceImpl.groovy";
            result = result.replace(apiFileFolder(), implFileFolder(implFolder));
        } else if (templateName.endsWith("Factory.mustache")) {
            int ix = result.lastIndexOf('/');
            result = result.substring(0, ix) + "/factories" + result.substring(ix, result.length() - 5) + "ServiceFactory.java";
            result = result.replace(apiFileFolder(), implFileFolder(implFolder));
        } else if (templateName.endsWith("Service.mustache")) {
            int ix = result.lastIndexOf('.');
            result = result.substring(0, ix) + "Service.java";
        }
        return result;
    }

    private String implFileFolder(String output) {
        return outputFolder + "/" + output + "/" + apiPackage().replace('.', '/');
    }

    @Override
    public void addOperationToGroup(String tag, String resourcePath, Operation operation, CodegenOperation co, Map<String, List<CodegenOperation>> operations) {
        super.addOperationToGroup(tag, resourcePath, operation, co, operations);
        co.subresourceOperation = !co.path.isEmpty();
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        model.imports.remove("ApiModelProperty");
        model.imports.remove("ApiModel");
        model.imports.remove("JsonSerialize");
        model.imports.remove("ToStringSerializer");
    }

    @Override
    public Map<String, Object> postProcessModels(Map<String, Object> objs) {

        for (CodegenProperty var : ((List<Map<String, CodegenModel>>) objs.get("models")).get(0).get("model").vars) {
            if (var.isDateTime) {
                List<Map<String, String>> imports = (List) objs.get("imports");

                Map<String, String> newImportMap = new HashMap<>();
                newImportMap.put("import", "com.fasterxml.jackson.annotation.JsonFormat");
                imports.add(newImportMap);
                newImportMap = new HashMap<>();
                newImportMap.put("import", "com.fasterxml.jackson.databind.annotation.JsonDeserialize");
                imports.add(newImportMap);
                newImportMap = new HashMap<>();
                newImportMap.put("import", "com.fasterxml.jackson.databind.annotation.JsonSerialize");
                imports.add(newImportMap);
                newImportMap = new HashMap<>();
                newImportMap.put("import", "com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer");
                imports.add(newImportMap);
                newImportMap = new HashMap<>();
                newImportMap.put("import", "com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer");
                imports.add(newImportMap);
                newImportMap = new HashMap<>();
                newImportMap.put("import", "ish.oncourse.util.FormatUtils");
                imports.add(newImportMap);
                break;
            }
        }

        return super.postProcessModels(objs);
    }

    @Override
    public String getHelp() {
        return "Generates a willow-jaxrs server.";
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

        return modelPackage + camelizedName;
    }

    @Override
    public String toModelFilename(final String name) {
        // should be the same as the model name
        return toModelName(name);
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
                final Map<String, Object> modelsMap = modelsList.get(0);
                final CodegenModel model = (CodegenModel) modelsMap.get("model");

                if (containsPackage(className)) {

                    final String newClassName = getClassName(className);
                    LOGGER.info("Replace classname {} with {}.", className, newClassName);
                    value.put("classname", newClassName);

                    final String modelPackage = (String) value.get("package");
                    final String newModelPackage = modelPackage + "." + getPackage(className).replaceAll(SEP, ".");

                    LOGGER.info("Replace package {} with {}.", modelPackage, newModelPackage);
                    value.put("package", newModelPackage);

                    final String importPath = (String) modelsMap.get("importPath");
                    modelsMap.put("importPath", importPath.replaceAll(SEP, "."));

                    model.classname = newClassName;
                }

                processVars(model.vars);
            } else {
                LOGGER.error("Unexpected behaviour in postProcessAllModels, objs value is not a Map");
            }
        }

        return super.postProcessAllModels(objs);
    }

    @Override
    public String toModelImport(final String name) {
        final String nameWithPackage = name.replaceAll(SEP, ".");
        return super.toModelImport(nameWithPackage);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> postProcessOperations(final Map<String, Object> objs) {
        final Map<String, Object> postProcessOperations = super.postProcessOperations(objs);
        final Map<String, Object> operations = (Map<String, Object>) postProcessOperations.get("operations");
        List<CodegenOperation> operationList = (List<CodegenOperation>) operations.get("operation");

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

        return postProcessOperations;
    }
}
