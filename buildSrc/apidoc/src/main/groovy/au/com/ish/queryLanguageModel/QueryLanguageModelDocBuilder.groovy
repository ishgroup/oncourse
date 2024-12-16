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

package au.com.ish.queryLanguageModel

import groovyjarjarantlr.RecognitionException
import groovyjarjarantlr.TokenStreamException
import groovyjarjarantlr.collections.AST
import org.codehaus.groovy.antlr.AntlrASTProcessor
import org.codehaus.groovy.antlr.SourceBuffer
import org.codehaus.groovy.antlr.UnicodeEscapingReader
import org.codehaus.groovy.antlr.java.Groovifier
import org.codehaus.groovy.antlr.java.Java2GroovyConverter
import org.codehaus.groovy.antlr.java.JavaLexer
import org.codehaus.groovy.antlr.java.JavaRecognizer
import org.codehaus.groovy.antlr.parser.GroovyLexer
import org.codehaus.groovy.antlr.parser.GroovyRecognizer
import org.codehaus.groovy.antlr.treewalker.PreOrderTraversal
import org.codehaus.groovy.antlr.treewalker.SourceCodeTraversal
import org.codehaus.groovy.antlr.treewalker.Visitor
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyRootDoc
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.codehaus.groovy.tools.groovydoc.LinkArgument
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDocAssembler
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyRootDoc

import java.util.regex.Matcher

class QueryLanguageModelDocBuilder {

    List<LinkArgument> links
    SimpleGroovyRootDoc rootDoc
    Properties properties

    QueryLanguageModelDocBuilder() {
        this.links = [] as List
        this.rootDoc = new SimpleGroovyRootDoc("root")
        this.properties = new Properties()
    }

    private Map<String, GroovyClassDoc> parseJava(String src, String packagePath, String file)
        throws RecognitionException, TokenStreamException {
        SourceBuffer sourceBuffer = new SourceBuffer()
        JavaRecognizer parser = getJavaParser(src, sourceBuffer)
        String[] tokenNames = parser.getTokenNames()
        parser.compilationUnit()
        AST ast = parser.getAST()

        // modify the Java AST into ssh Groovy AST (just token types)
        Visitor java2groovyConverter = new Java2GroovyConverter(tokenNames)
        AntlrASTProcessor java2groovyTraverser = new PreOrderTraversal(java2groovyConverter)
        java2groovyTraverser.process(ast)

        // now mutate (groovify) the ast into groovy
        Visitor groovifier = new Groovifier(tokenNames, false)
        AntlrASTProcessor groovifierTraverser = new PreOrderTraversal(groovifier)
        groovifierTraverser.process(ast)

        // now do the business
        Visitor visitor = new SimpleGroovyClassDocAssembler(packagePath, file, sourceBuffer, links, properties, false)
        AntlrASTProcessor traverser = new SourceCodeTraversal(visitor)

        traverser.process(ast)

        return ((SimpleGroovyClassDocAssembler) visitor).getGroovyClassDocs()
    }

    private Map<String, GroovyClassDoc> parseGroovy(String src, String packagePath, String file)
        throws RecognitionException, TokenStreamException {
        SourceBuffer sourceBuffer = new SourceBuffer()
        GroovyRecognizer parser = getGroovyParser(src, sourceBuffer)
        parser.compilationUnit()
        AST ast = parser.getAST()

        // now do the business
        Visitor visitor = new SimpleGroovyClassDocAssembler(packagePath, file, sourceBuffer, links, properties, true)
        AntlrASTProcessor traverser = new SourceCodeTraversal(visitor)
        traverser.process(ast)
        return ((SimpleGroovyClassDocAssembler) visitor).getGroovyClassDocs()
    }

    private static JavaRecognizer getJavaParser(String input, SourceBuffer sourceBuffer) {
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(new StringReader(input), sourceBuffer)
        JavaLexer lexer = new JavaLexer(unicodeReader)
        unicodeReader.setLexer(lexer)
        JavaRecognizer parser = JavaRecognizer.make(lexer)
        parser.setSourceBuffer(sourceBuffer)
        return parser
    }

    private static GroovyRecognizer getGroovyParser(String input, SourceBuffer sourceBuffer) {
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(new StringReader(input), sourceBuffer)
        GroovyLexer lexer = new GroovyLexer(unicodeReader)
        unicodeReader.setLexer(lexer)
        GroovyRecognizer parser = GroovyRecognizer.make(lexer)
        parser.setSourceBuffer(sourceBuffer)
        return parser
    }

    protected void setOverview() {
        String path = properties.getProperty("overviewFile")
        if (path != null && path.length() > 0) {
            try {
                String content = ResourceGroovyMethods.getText(new File(path))
                rootDoc.setDescription(content)
            } catch (IOException e) {
                log.error("Unable to load overview file", e)
            }
        }
    }

/**
 * Extract the package name from inside the source file
 */
    private static String getPackageName(String source) {
        Matcher packageName = source =~ /(?m)^package\s([ssh-z.0-9]+);?$/

        if (packageName && packageName[0] && packageName[0][1]) {
            return packageName[0][1].replaceAll(/\./, '/')
        }
        return "DefaultPackage"
    }

    protected void processFile(File srcFile) throws IOException {
        def src = ResourceGroovyMethods.getText(srcFile)
        if (!src.contains(" enum ")) {
            return
        }
        def packagePath = getPackageName(src)
        def filename = srcFile.getName()

        if (filename.equals("AdditionalParameters.java")) {
            return
        }

        try {
            Map<String, GroovyClassDoc> classDocs
            if (filename.indexOf(".java") > 0) {
                classDocs = parseJava(src, packagePath, filename)
            } else {
                classDocs = parseGroovy(src, packagePath, filename)
            }

            rootDoc.putAllClasses(classDocs)

        } catch (Exception e) {
            // just ignore when we hit some Java 11 we can't parse
        }
    }

    GroovyRootDoc resolve() {
        rootDoc.resolve()
        return rootDoc
    }
}
