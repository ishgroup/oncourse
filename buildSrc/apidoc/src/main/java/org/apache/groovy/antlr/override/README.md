GroovyDocVisitor is a special class for Parsing Groovy documents that was introduced in version > 4. 

Since this class is not yet in the version used, it was copied into the project and also included manually.

After updating Groovy > 4, all contents of the package 

`buildSrc/apidoc/src/main/java/org/apache/groovy/antlr/override` 

can be removed and standard Document Parsing mechanisms can be used.

// TODO after Groovy update

Change class [DslGroovyRootDocBuilder.groovy](..%2F..%2F..%2F..%2F..%2F..%2Fgroovy%2Fau%2Fcom%2Fish%2Fdocs%2FDslGroovyRootDocBuilder.groovy)

````
private Map<String, GroovyClassDoc> parseGroovy(String src, String packagePath, String file) {
    ...
}
````

Acourding to https://issues.apache.org/jira/browse/GROOVY-11269