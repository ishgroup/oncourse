/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package io.swagger.codegen.languages;

import io.swagger.codegen.*;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;

public class LinkRestServerCodegen extends JavaCXFServerCodegen {
    private static final Logger LOGGER = LoggerFactory.getLogger(io.swagger.codegen.languages.LinkRestServerCodegen.class);

    private final Map<String, Set<String>> models = new HashMap<>();

    private String wrapperResponseClass = "com.nhl.link.rest.DataResponse";

    public static final String WRAPPER_RESPONSE_CLASS = "wrapperResponseClass";



    public LinkRestServerCodegen() {
        super();

        CodegenModelFactory.setTypeMapping(CodegenModelType.OPERATION, LinkRestCodegenOperation.class);
        CodegenModelFactory.setTypeMapping(CodegenModelType.MODEL, LinkRestCodegenModel.class);


        artifactId = "swagger-jaxrs-linkrest-server";

        supportsInheritance = true;
        sourceFolder = "src/main/java";
        implFolder = "src/main/java";
        testFolder = "src/test/java";

        outputFolder = "generated-code/JavaJaxRS-LinkRest";
        apiTemplateFiles.put("apiServiceImpl.mustache", ".java");

        apiTestTemplateFiles.clear();
        apiTestTemplateFiles.put("api_test.mustache", ".java");
        modelDocTemplateFiles.remove("model_doc.mustache");
        apiDocTemplateFiles.remove("api_doc.mustache");

        typeMapping.put("date", "LocalDateTime");
        importMapping.put("LocalDate", "java.time.LocalDateTime");
        importMapping.put("LocalDateTime", "java.time.LocalDateTime");
        typeMapping.put("DateTime", "LocalDateTime");
        typeMapping.put("number", "Double");

        embeddedTemplateDir = templateDir = JAXRS_TEMPLATE_DIRECTORY_NAME + File.separator + "linkrest";
    }
    @Override
    public void processOpts() {
        super.processOpts();

        if (additionalProperties.containsKey(WRAPPER_RESPONSE_CLASS)) {
            this.wrapperResponseClass = additionalProperties.get(WRAPPER_RESPONSE_CLASS).toString();
        }
    }
    @Override
    public String getName() {
        return "linkrest";
    }

    @Override
    public String getHelp() {
        return "Generates LinkRest jaxrs API.";
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        if (!isNullOrEmpty(model.classname)) {
            Set<String> props = models.get(model.classname);
            if (props == null) {
               props = new HashSet<>();
            }
            props.add(property.baseName);
            models.put(model.classname, props);
        }
    }

    @Override
    public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
        Map<String, Object> objsResult = super.postProcessOperations(objs);

        Map<String, Object> operations = (Map<String, Object>) objsResult.get("operations");

        if ( operations != null ) {
            @SuppressWarnings("unchecked")
            List<LinkRestCodegenOperation> ops = (List<LinkRestCodegenOperation>) operations.get("operation");
            for ( LinkRestCodegenOperation operation : ops ) {
                operation.isRestfulShow = "GET".equalsIgnoreCase(operation.httpMethod);

                // Remove containers from response
                if (!"void".equals(operation.returnType) && operation.isCrudOperation() && wrapperResponseClass != null) {
                    operation.returnType = String.format("%s<%s>", wrapperResponseClass, operation.superClassName);
                }

                // Stores model properties as header parameters to use them as constraints
                if (models.get(operation.baseName) != null) {
                    for (final String prop : models.get(operation.baseName)) {
                        final CodegenParameter codegenParam = new CodegenParameter();
                        codegenParam.paramName = prop;
                        operation.headerParams.add(codegenParam);
                    }
                }
            }
        }

        return objsResult;
    }

    @Override
    public CodegenOperation fromOperation(String resourcePath, String httpMethod, Operation operation, Map<String, Model> definitions, Swagger swagger) {

        LinkRestCodegenOperation codegenOperation = (LinkRestCodegenOperation) super.fromOperation(resourcePath, httpMethod, operation, definitions, swagger);
        Object crudFlag = operation.getVendorExtensions().get("x-crud");
        codegenOperation.setCrudOperation(crudFlag == null || Boolean.TRUE.equals(crudFlag));

        Model model = null;
        if (codegenOperation.isRestfulShow()){
            model = definitions.get(codegenOperation.returnBaseType);
        } else if (codegenOperation.isRestfulCreate()) {
            model = definitions.get(codegenOperation.bodyParam.baseType);

            Map.Entry<String, Property> property = model.getProperties().entrySet().stream().filter(e -> e.getValue().getVendorExtensions().get("x-unique") != null).findAny().orElse(null);
            codegenOperation.setUniqueMapper(property != null ? property.getKey() : null);
        }
        if (model != null) {
            codegenOperation.setSuperClassName(model.getVendorExtensions().get("x-superClass").toString());
        }

        return codegenOperation;
    }


    @Override
    public CodegenModel fromModel(String name, Model model, Map<String, Model> allDefinitions) {
        LinkRestCodegenModel codegenModel = (LinkRestCodegenModel) super.fromModel(name, model, allDefinitions);
        Object superClass = model.getVendorExtensions().get("x-superClass");
        if (superClass != null) {
            codegenModel.setSuperClassName(superClass.toString());
        }

        return codegenModel;
    }

}
