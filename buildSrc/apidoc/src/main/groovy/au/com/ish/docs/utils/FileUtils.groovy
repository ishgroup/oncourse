/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.utils


import org.apache.commons.io.FilenameUtils
import org.gradle.api.Project

import static au.com.ish.docs.Configuration.*
import static org.apache.commons.io.FileUtils.listFiles

class FileUtils {

    static String convertPackageToPath(String packageName) {
        String path = packageName.replace('.', File.separator)
        return path.endsWith(File.separator) ? path : path + File.separator
    }

    static File findFileInFolder(File folder, String file) {
        if (!folder.exists()) {
           throw new RuntimeException('DSL Documentation generation failed: can not find destDir')
        }
        return listFiles(folder, [FilenameUtils.getExtension(file)] as String[] , true).toList().find {
            it.absolutePath.endsWith(file)
        }
    }

    static String getTemplateSourceRootPath(String template, Project project) {
        String rootPackagePath = convertPackageToPath(TEMPLATES_PACKAGE)
        File templatesRootDirectory = project.file(RESOUCRCES_DIRECTORY + rootPackagePath)
        if (!templatesRootDirectory.exists()) {
            throw new RuntimeException("Invalid project configuration. Documentation templates not found.")
        }

        String templateBaseName = FilenameUtils.getBaseName(template)
        File templateFile = new File(templatesRootDirectory.getAbsolutePath() + File.separator + formatTemplatePath(template))
        if (!templateFile.exists()) {
            templateFile = listFiles(templatesRootDirectory, [TEMPLATE_TYPE] as String[], true)
                    .toList()
                    .find { FilenameUtils.getBaseName(it.name) == FilenameUtils.getBaseName(templateBaseName) }
        }
        if (templateFile == null || !templateFile.exists()) {
            throw new RuntimeException("Invalid project configuration. Template '${template}' not found.")
        }

        String templatePackage = FilenameUtils.removeExtension(templateFile.getAbsolutePath()).replaceFirst(".+?${rootPackagePath}", "")
        return rootPackagePath + templatePackage
    }

    static String getTemplatePackage(String template, Project project) {
        String rootPackagePath = convertPackageToPath(TEMPLATES_PACKAGE)
        String path = getTemplateSourceRootPath(template, project).replaceFirst(rootPackagePath, "")

        def tokens = path.split(File.separator).toList()
        tokens.removeLast()

        return String.join(File.separator, tokens) + File.separator
    }

    private static String formatTemplatePath(String template) {
        String templateExtension = FilenameUtils.getExtension(template)
        if (templateExtension.isEmpty()) {
            templateExtension = TEMPLATE_TYPE
            template += "." + templateExtension
        }
        return convertPackageToPath(template).replaceFirst("${File.separator}${templateExtension}${File.separator}", ".${templateExtension}")
    }
}
