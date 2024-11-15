/*
 * Copyright 2003-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain ssh copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.com.ish.docs

import groovyjarjarantlr.RecognitionException
import groovyjarjarantlr.TokenStreamException
import groovyjarjarantlr.collections.AST
import org.apache.commons.lang3.StringUtils
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
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyExecutableMemberDoc
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyPackageDoc
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyRootDoc

import java.util.regex.Matcher

class DslGroovyRootDocBuilder {

	List<LinkArgument> links
	DslGroovyDocTool tool
    SimpleGroovyRootDoc rootDoc
    Properties properties

    def log = tool.project.logger

	DslGroovyRootDocBuilder(DslGroovyDocTool tool, List<LinkArgument> links) {
		this.tool = tool
		this.links = links
		this.rootDoc = new SimpleGroovyRootDoc("root")
		this.properties = new Properties()
	}

	private Map<String, GroovyClassDoc> parseGroovy(String src, String packagePath, String file)
			throws RecognitionException, TokenStreamException {
		boolean isJava = file.endsWith(".java")

		SourceBuffer sourceBuffer = new SourceBuffer()
		UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(new StringReader(src), sourceBuffer)

		def parser
		if (isJava) {
			JavaLexer lexer = new JavaLexer(unicodeReader)
			unicodeReader.setLexer(lexer)
			parser = JavaRecognizer.make(lexer)
			parser.setSourceBuffer(sourceBuffer)
			parser.compilationUnit()
		} else {
			GroovyLexer lexer = new GroovyLexer(unicodeReader)
			unicodeReader.setLexer(lexer)
			parser = GroovyRecognizer.make(lexer)
			parser.setSourceBuffer(sourceBuffer)
			parser.compilationUnit()
		}

		AST ast = parser.getAST()
		if (isJava) {
			// modify the Java AST into ssh Groovy AST (just token types)
			Visitor java2groovyConverter = new Java2GroovyConverter(parser.getTokenNames())
			AntlrASTProcessor java2groovyTraverser = new PreOrderTraversal(java2groovyConverter)
			java2groovyTraverser.process(ast)

			// now mutate (groovify) the ast into groovy
			Visitor groovifier = new Groovifier(parser.getTokenNames(), false)
			AntlrASTProcessor groovifierTraverser = new PreOrderTraversal(groovifier)
			groovifierTraverser.process(ast)
		}
		Visitor visitor = new SimpleGroovyClassDocAssembler(packagePath, file, sourceBuffer, links, properties, !isJava)
		AntlrASTProcessor traverser = new SourceCodeTraversal(visitor)
		traverser.process(ast)
		return ((SimpleGroovyClassDocAssembler)visitor).getGroovyClassDocs()
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

	/** Extract the package name from inside the source file */
    private static String getPackageName(String source) {
		if (StringUtils.isNotBlank(source)) {
			Matcher packageName = source =~ /(?m)^\s*?package\s+?([A-Za-z.0-9]+);?$/
			if (packageName && packageName[0] && packageName[0][1]) {
				return (packageName[0] as String[])[1].replaceAll(/\./, '/')
			}
		}
		return "DefaultPackage"
    }

	protected void processFile(File srcFile) throws IOException {
		def src = ResourceGroovyMethods.getText(srcFile)
		def packagePath = getPackageName(src)

		def filename = srcFile.getName()
		SimpleGroovyPackageDoc packageDoc = (SimpleGroovyPackageDoc) rootDoc.packageNamed(packagePath)

		try {
            Map<String, GroovyClassDoc> classDocs
			classDocs = parseGroovy(src, packagePath, filename)
			rootDoc.putAllClasses(classDocs)

			if (packageDoc == null) {
				packageDoc = new SimpleGroovyPackageDoc(packagePath)
			}
			packageDoc.putAll(classDocs)
			rootDoc.put(packagePath, packageDoc)

		} catch (RecognitionException | TokenStreamException e) {
			throw new IllegalStateException("Parsing failed for '${filename}' due to: ${e.message}", e)
		}
	}

	GroovyRootDoc resolve() {
		rootDoc.resolve()
		return rootDoc
	}

    /**
     * Find all the mixin classes and merge their javadoc with the class they are designed to mix to.
     * So ArtistMixin.class should be added to Artist.class
     */
    void mergeMixins() {
        def mixins = rootDoc.classes().findAll { it.name().endsWith("Mixin") }

        for (GroovyClassDoc mixin : mixins) {
            def doc = rootDoc.classes().find { it.name() == mixin.name().replace("Mixin", "") }
            if (doc) {
                mixin.methods().each { m ->
                    log.info("Found mixin {}.{} adding to {}", mixin.name(), m.name(), doc.name())
                    if (m.parameters() && m.parameters()[0]?.name() == "self") {
                        // use reflection to drop the first fake 'self' param for the mixin method
                        SimpleGroovyExecutableMemberDoc.metaClass.setAttribute(m, 'parameters', m.parameters().drop(1))
                        ((SimpleGroovyExecutableMemberDoc)m).setStatic(false)
                    }
                    doc.add(m)
               }
            }
        }
    }

	void mergeTraits(List<GroovyClassDoc> classes) {
		for (GroovyClassDoc annotatedClass : classes) {
			for (GroovyClassDoc implementedInterface : annotatedClass.interfaces()) {
				implementedInterface.methods().each { method ->
					log.info("found method {} in interface {}, adding to class {}", method.name(), implementedInterface.name(), annotatedClass.name())
					annotatedClass.add(method)
				}
			}
		}
	}
}
