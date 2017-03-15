package ish.oncourse.codegen.cxf;

import io.swagger.codegen.CodegenModel;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenProperty;
import io.swagger.codegen.SupportingFile;
import io.swagger.codegen.languages.AbstractJavaJAXRSServerCodegen;
import io.swagger.models.Operation;
import ish.oncourse.codegen.common.Launcher;

import java.util.List;
import java.util.Map;

/**
 * Generates interfaces and models for Willow-Api project.
 *
 * @author Ibragimov Ruslan
 * @since 0.1
 */
public class WillowJaxRsGenerator extends AbstractJavaJAXRSServerCodegen {

    public static void main(String[] args) {
        new Launcher(new WillowJaxRsGenerator()).run();
    }

    public WillowJaxRsGenerator() {
        super();

        supportsInheritance = true;

        apiPackage = "ish.oncourse.willow.service";
        modelPackage = "ish.oncourse.willow.model";

        sourceFolder = "src/main/groovy";
        implFolder = "src/main/groovy";

        outputFolder = "../../api";

        apiTemplateFiles.put("apiServiceImpl.mustache", ".groovy");

        modelDocTemplateFiles.remove("model_doc.mustache");
        apiDocTemplateFiles.remove("api_doc.mustache");

        typeMapping.put("date", "LocalDateTime");

        importMapping.put("LocalDate", "java.time.LocalDateTime");
        importMapping.put("LocalDateTime", "java.time.LocalDateTime");
        typeMapping.put("DateTime", "LocalDateTime");

        embeddedTemplateDir = templateDir = "willow-jaxrs";
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
    public String getHelp() {
        return "Generates a willow-jaxrs server.";
    }
}
