import groovy.transform.CompileStatic
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
//
//// Add the AST annotation @CompileStatic
//// and @ToString to all classes.
configuration.addCompilationCustomizers(
        new ASTTransformationCustomizer(CompileStatic))