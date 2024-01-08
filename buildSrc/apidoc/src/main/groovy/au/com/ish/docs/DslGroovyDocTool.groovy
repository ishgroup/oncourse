/*
 * Copyright 2007-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import groovy.text.GStringTemplateEngine
import groovy.text.Template
import groovyjarjarantlr.RecognitionException
import groovyjarjarantlr.TokenStreamException
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyRootDoc
import org.codehaus.groovy.tools.groovydoc.FileOutputTool
import org.codehaus.groovy.tools.groovydoc.LinkArgument
import org.codehaus.groovy.tools.groovydoc.OutputTool
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

import java.nio.charset.Charset
/**
 * This is external interface to the entire tool. First we instantiate the class with the templates
 * and some properties. Then we add all the files we want to document (which causes them to be
 * immediately parsed). And then finally we generate the documentation by rendering the groovy templates.
 */
class DslGroovyDocTool {
	private final DslGroovyRootDocBuilder rootDocBuilder
	private final GStringTemplateEngine engine = new GStringTemplateEngine()
    private final OutputTool output = new FileOutputTool()
    private Template docTemplate, classTemplate
    Project project
    def log


    /**
	 * Let's set everything up.
	 *
	 * @param docTemplate Top level index template for the entire document.
	 * @param classTemplate A template per class.
	 * @param links Some external links to other javadocs we might want to link to
	 */
	DslGroovyDocTool(File docTemplateURL, File classTemplateURL, List<LinkArgument> links, Project project) {

		this.project = project
        this.log = project.logger
		this.rootDocBuilder = new DslGroovyRootDocBuilder(this, links)

        docTemplate = engine.createTemplate(docTemplateURL)
        classTemplate = engine.createTemplate(classTemplateURL)
    }

	/**
	 * Adding the files to the tool causes them to be parsed by the antlr based groovy or java parser
	 *
	 * @param files
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 * @throws IOException
	 */
	void add(FileCollection files) throws RecognitionException, TokenStreamException, IOException {
		rootDocBuilder.setOverview()

		for (File srcFile : files) {
			if (srcFile.exists()) {
				rootDocBuilder.processFile(srcFile)
			}
		}
	}

	/**
	 * Now we take the results of the parsing and put them through the groovy templates to generate the final output
	 *
	 * @param output
	 * @param destdir
	 * @throws Exception
	 */
	void renderToOutput(String destdir) throws Exception {
		GroovyRootDoc rootDoc = rootDocBuilder.resolve()

        rootDocBuilder.mergeMixins()

        // only output classes with @API annotation
        def classes = rootDoc.classes().findAll {
            it.annotations().collect { it.name()}.contains("API")
        }
        //rootDocBuilder.mergeTraits(classes)

		writeRoot(classes, destdir)
		writeClasses(classes, destdir)

        // clean up by deleting all the empty folders
        def emptyDirs = []

        project.fileTree(dir: destdir).visit { v ->
            File f = v.file

            if (f.isDirectory() ) {
                def children = project.fileTree(f).filter { f.isFile() }.files
                if (children.size() == 0) {
                    emptyDirs << f
                }
            }
        }

        // reverse so that we do the deepest folders first
        emptyDirs.reverseEach { it.delete() }

	}

    private static isVisible = { doc -> doc.annotations().find { ann -> ann.name() == "API" } }

    private void writeClasses(Collection<GroovyClassDoc> classes, String destdir) throws Exception {

        for (GroovyClassDoc classDoc : classes ) {
            log.debug("Generating asciidoc for " + classDoc.simpleTypeName())
            def binding = new HashMap<String, Object>()
            def helper = new DslTemplateHelper(this)
            binding.put("classDoc", classDoc)
            binding.put("helper", helper)
            binding.put("visibleMethods", (classDoc.methods().findAll(isVisible) + classDoc.superclass()?.methods()?.findAll(isVisible)).findAll())
            binding.put("visibleConstructors", classDoc.constructors().findAll(isVisible))

            String destFileName = destdir + "/" + classDoc.getFullPathName() + ".adoc"
            log.debug("Generating " + destFileName)
            String renderedSrc = classTemplate.make(binding).toString()
            output.writeToOutput(destFileName, renderedSrc, Charset.defaultCharset().name())
        }
    }

    private void writeRoot(Collection<GroovyClassDoc> classes, String destdir) throws Exception {
        log.debug("Generating asciidoc index")
        output.makeOutputArea(destdir)
        String destFileName = destdir + "/" + "index.adoc"

        def binding = new HashMap<String, Object>()
        binding.put("classes", classes)
        String renderedSrc = docTemplate.make(binding).toString()

        output.writeToOutput(destFileName, renderedSrc, Charset.defaultCharset().name())
    }

}
