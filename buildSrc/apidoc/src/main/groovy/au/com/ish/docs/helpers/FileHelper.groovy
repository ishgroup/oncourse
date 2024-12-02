/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.helpers

import au.com.ish.docs.generator.DSLGenerator
import au.com.ish.docs.generator.chapter.ChapterDSLGenerator
import au.com.ish.docs.generator.root.Context
import au.com.ish.docs.generator.root.SectionDSLGenerator
import au.com.ish.docs.utils.FileUtils
import au.com.ish.docs.utils.TextUtils
import com.github.jknack.handlebars.Options
import org.apache.commons.io.FilenameUtils
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.OutputTool

import java.nio.charset.Charset
import java.nio.file.Files

import static au.com.ish.docs.helpers.FileHelper.GeneratorFactory.*

class FileHelper {

    @Helper
    static String render(String path, Options options) {
        // todo copy if exist
        def context = options.context.data("root") as Context
        DSLGenerator generator = new SectionDSLGenerator(context.output, context.project, getDestDir(context))
        path = FilenameUtils.getBaseName(path)

        String fullPath = getDestDir(context) + File.separator + FileUtils.getTemplatePackage(path, context.project)
        String destFileName = "${fullPath}${FilenameUtils.getBaseName(path)}.md"
        getOutput(context).makeOutputArea(fullPath)
        new File(destFileName).createNewFile()

        String renderedSrc = null
        try {
            renderedSrc = generator.generate(getClasses(context), path)
            renderedSrc = TextUtils.trimExtraLineSeperators(renderedSrc)
        } catch (Exception e) {
            throw new RuntimeException(String.format("Template %s generation failed", path), e)
        }

        getOutput(context).writeToOutput(destFileName, renderedSrc, Charset.defaultCharset().name())
        String rootFile = FileUtils.findFileInFolder(new File(getDestDir(context)), FilenameUtils.getBaseName(options.fn.filename()) + ".md")

        return TextUtils.updateLinksWithRelativePaths(renderedSrc, new File(destFileName), new File(rootFile))
    }

    @Helper
    static String renderChapter(GroovyClassDoc model, Options options) {
        def context = options.context.data("root") as Context
        File destFile = new File(getDestDir(context) + "/" + model.getFullPathName() + ".md")

        String renderedSrc = null
        if (!destFile.exists()) {
            DSLGenerator generator = new ChapterDSLGenerator()
            try {
                renderedSrc = generator.generate(List.of(model), '')
            } catch (Exception e) {
                e.printStackTrace()
            }
            getOutput(context).writeToOutput(destFile.absolutePath, renderedSrc, Charset.defaultCharset().name())
        } else {
            renderedSrc = Files.readString(destFile.toPath())
        }

        String rootFile = FileUtils.findFileInFolder(new File(getDestDir(context)), FilenameUtils.getBaseName(options.fn.filename()) + ".md")
        return TextUtils.updateLinksWithRelativePaths(renderedSrc, destFile, new File(rootFile))
    }

    // todo
    class GeneratorFactory {
        static DSLGenerator get(Object context) {
            if (context instanceof Context) {
                Context genContext = context as Context
                return new SectionDSLGenerator(genContext.output, context.project, getDestDir(context))
            } else {

                new ChapterDSLGenerator()
            }
            return null
        }

        static OutputTool getOutput(Object context) {
            if (context instanceof Context) {
                return (context as Context).output
            }
            return null
        }

        static String getDestDir(Object context) {
            if (context instanceof Context) {
                return (context as Context).distDir
            }
            return null
        }

        static Collection<GroovyClassDoc> getClasses(Object context) {
            if (context instanceof Context) {
                return (context as Context).classes
            }
            return null
        }
    }
}
