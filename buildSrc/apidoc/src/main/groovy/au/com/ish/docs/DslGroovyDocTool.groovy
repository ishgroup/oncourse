/*
 * Copyright 2007-2013 the original author or authors.
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

import au.com.ish.docs.handlebars.EmptyTemplate
import au.com.ish.docs.handlebars.HandlebarsContext
import au.com.ish.docs.generator.chapter.ChapterContext
import au.com.ish.docs.generator.root.SectionContext
import au.com.ish.docs.helpers.FileHelper
import au.com.ish.docs.utils.GroovyDocUtils
import com.github.jknack.handlebars.Options
import groovyjarjarantlr.RecognitionException
import groovyjarjarantlr.TokenStreamException
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyRootDoc
import org.codehaus.groovy.tools.groovydoc.FileOutputTool
import org.codehaus.groovy.tools.groovydoc.LinkArgument
import org.codehaus.groovy.tools.groovydoc.OutputTool
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

/**
 * This is external interface to the entire tool. First we instantiate the class with the templates
 * and some properties. Then we add all the files we want to document (which causes them to be
 * immediately parsed). And then finally we generate the documentation by rendering the groovy templates.
 */
class DslGroovyDocTool {

	def log

	private Project project
	private final DslGroovyRootDocBuilder rootDocBuilder
	private final OutputTool output = new FileOutputTool()

	/**
	 * Let's set everything up.
	 *
	 * @param docTemplate Top level index template for the entire document.
	 * @param classTemplate A template per class.
	 * @param links Some external links to other javadocs we might want to link to
	 */
	DslGroovyDocTool(List<LinkArgument> links, Project project) {
		this.project = project
		this.log = project.logger
		this.rootDocBuilder = new DslGroovyRootDocBuilder(this, links)
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
		def classes = rootDoc.classes().findAll { GroovyDocUtils.isVisible(it) }.toList()
		rootDocBuilder.mergeTraits(classes)

		writeClasses(classes, destdir)
		writeRoot(classes, project, destdir)

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

	void cleanUpDirectory(String destDir) {
		File dest = new File(destDir)
		if (dest.exists()) {
			dest.deleteDir()
		}
	}

	private void writeClasses(Collection<GroovyClassDoc> classes, String destdir) throws Exception {
		classes.each { classDoc ->
			def context = new ChapterContext.Builder()
					.setClassDoc(classDoc)
					.setClasses(classes)
					.setDistDir(destdir)
					.setProject(project)
					.setOutput(output)
					.setProject(project)
					.build()

			def _context = new HandlebarsContext(["root": context])
			def options = new Options(null, null, null, _context, new EmptyTemplate("index.md"), null, null, null, [])
			FileHelper.renderChapter(classDoc, options)
		}
	}

	private void writeRoot(Collection<GroovyClassDoc> classes, Project project, String destdir) throws Exception {
		String path = destdir + "/index.md"
		def context = new SectionContext.Builder()
				.setClasses(classes)
				.setDistDir(destdir)
				.setProject(project)
				.setOutput(output)
				.setProject(project)
				.build()

		def _context = new HandlebarsContext(["root": context])
		def options = new Options(null, null, null, _context, new EmptyTemplate("index.md"), null, null, null, [])
		FileHelper.render(path, options)
	}

}
