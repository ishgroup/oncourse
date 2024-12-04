/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.helpers


import au.com.ish.docs.generator.DSLGenerator
import au.com.ish.docs.generator.chapter.ChapterContext
import au.com.ish.docs.generator.chapter.ChapterDSLGenerator
import au.com.ish.docs.generator.root.SectionContext
import au.com.ish.docs.generator.root.SectionDSLGenerator
import au.com.ish.docs.utils.FileUtils
import au.com.ish.docs.utils.TextUtils
import com.github.jknack.handlebars.Options
import org.apache.commons.io.FilenameUtils
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.OutputTool

import java.nio.charset.Charset
import java.nio.file.Files

import static au.com.ish.docs.Configuration.RESULT_DOC_TYPE
import static au.com.ish.docs.helpers.FileHelper.GeneratorFactory.getDestDir
import static au.com.ish.docs.helpers.FileHelper.GeneratorFactory.getOutput

class FileHelper {

    @Helper
    static String render(String path, Options options) {
        def context = options.context.data("root") as SectionContext
        path = FilenameUtils.getBaseName(path)
        DSLGenerator generator = new SectionDSLGenerator(path)

        String fullPath = getDestDir(context) + File.separator + FileUtils.getTemplatePackage(path, context.project)
        String destFileName = "${fullPath}${FilenameUtils.getBaseName(path)}.${RESULT_DOC_TYPE}"
        getOutput(context).makeOutputArea(fullPath)
        new File(destFileName).createNewFile()

        String renderedSrc = null
        try {
            renderedSrc = generator.generate(context)
            renderedSrc = TextUtils.trimExtraLineSeperators(renderedSrc)
        } catch (Exception e) {
            throw new RuntimeException(String.format("Template %s generation failed", path), e)
        }

        getOutput(context).writeToOutput(destFileName, renderedSrc, Charset.defaultCharset().name())
        String rootFile = FileUtils.findFileInFolder(new File(getDestDir(context)), FilenameUtils.getBaseName(options.fn.filename()) + "." + RESULT_DOC_TYPE)

        return TextUtils.updateLinksWithRelativePaths(renderedSrc, new File(destFileName), new File(rootFile))
    }

    @Helper
    static String renderChapter(GroovyClassDoc model, Options options) {

        def context = options.context.data("root") as SectionContext
        File destFile = new File(getDestDir(context) + "/" + model.getFullPathName() + "." + RESULT_DOC_TYPE)

        String renderedSrc = null
        if (!destFile.exists()) {
            DSLGenerator generator = new ChapterDSLGenerator(context)
            try {
                renderedSrc = generator.generate(new ChapterContext.Builder().init(context).setClassDoc(model).build())
            } catch (Exception e) {
                e.printStackTrace()
            }
            getOutput(context).writeToOutput(destFile.absolutePath, renderedSrc, Charset.defaultCharset().name())
        } else {
            renderedSrc = Files.readString(destFile.toPath())
        }

        try {
            String rootFile = FileUtils.findFileInFolder(new File(getDestDir(context)), FilenameUtils.getBaseName(options.fn.filename()) + "." + RESULT_DOC_TYPE)
            if (rootFile != null) {
                return TextUtils.updateLinksWithRelativePaths(renderedSrc, destFile, new File(rootFile))
            } else {
                return renderedSrc
            }
        } catch (Exception e) {
            e.printStackTrace()
            throw new RuntimeException(e)
        }

    }


    class GeneratorFactory {

        static <T extends SectionContext> OutputTool getOutput(T context) {
            return (context as SectionContext).output
        }

        static <T extends SectionContext> String getDestDir(T context) {
            return (context as SectionContext).distDir
        }

    }
}
