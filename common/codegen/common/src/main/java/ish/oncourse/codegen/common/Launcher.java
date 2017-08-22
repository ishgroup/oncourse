package ish.oncourse.codegen.common;

import io.swagger.codegen.ClientOptInput;
import io.swagger.codegen.ClientOpts;
import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.DefaultGenerator;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

import java.io.File;
import java.util.List;

/**
 * Helper for running code generators.
 *
 * @author Ibragimov Ruslan
 * @since 0.1
 */
public final class Launcher {
    private final CodegenConfig codegenConfig;

    public Launcher(CodegenConfig codegenConfig) {
        this.codegenConfig = codegenConfig;
    }

    public List<File> run() {
        Swagger swagger = new SwaggerParser().read("swagger.yaml");

        ClientOptInput clientOptInput = new ClientOptInput();
        clientOptInput.setConfig(codegenConfig);
        clientOptInput
                .opts(new ClientOpts())
                .swagger(swagger);

        return new DefaultGenerator().opts(clientOptInput).generate();
    }
}
