import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer

// Add the AST annotation @CompileStatic
// and @ToString to all classes.
configuration.addCompilationCustomizers(
        new ASTTransformationCustomizer(CompileStatic))
configuration.addCompilationCustomizers(
        new ASTTransformationCustomizer(ToString))