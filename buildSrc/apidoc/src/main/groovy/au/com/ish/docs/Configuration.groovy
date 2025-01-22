/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs

import org.gradle.api.file.FileCollection

class Configuration {

    public static final DSL_ANNONATION = 'ish.oncourse.API'
    protected static final DSL_ANNOTATION_LABEL = '@API'

    public static final TEMPLATE_TYPE = 'hbs'
    public static final RESULT_DOC_TYPE = 'md'

    public static final NULLABLE_ANNONATION = 'javax/annotation/Nullable'
    public static final NOTNULL_ANNONATION = 'javax/annotation/Nonnull'
    public static final CAYENNE_ENTITIES_PACKAGE = 'ish/oncourse/server/cayenne/'

    public static final TEMPLATES_PACKAGE = 'au.com.ish.docs.templates'
    public static final RESOUCRCES_DIRECTORY = '../buildSrc/apidoc/src/main/resources/'
    public static final CHAPTER_TEMPLATE = '/au/com/ish/docs/templates/chapter'

    private DslDocsPluginExtension dslDocsPluginExtension

    Configuration(DslDocsPluginExtension dslDocsPluginExtension) {
        this.dslDocsPluginExtension = dslDocsPluginExtension
    }

    String getDistationDir() {
        return dslDocsPluginExtension.destinationDir.canonicalPath
    }

    FileCollection getSourceFiles() {
        // by filtering out all files which don't contain @API before trying to parse them this runs much faster
        // we also need to grab _class files in order to get the Cayenne superclasses
        return dslDocsPluginExtension.source
                .filter { it.absolutePath.endsWith(".java") || it.absolutePath.endsWith(".groovy")}
                .filter { it.text.contains(DSL_ANNOTATION_LABEL) || (it.absolutePath.contains(CAYENNE_ENTITIES_PACKAGE)
                        && (it.text.contains('public class') || it.text.contains('public abstract class')))
                }
    }

}
