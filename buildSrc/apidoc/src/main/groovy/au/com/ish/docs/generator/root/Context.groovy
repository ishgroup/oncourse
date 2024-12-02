/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.generator.root

import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.OutputTool
import org.gradle.api.Project

class Context {

    private String templateLocation

    private Collection<GroovyClassDoc> classes
    private String distDir
    private OutputTool output
    private Project project

    private Context(Builder builder) {
        this.templateLocation = builder.templateLocation
        this.classes = builder.classes
        this.distDir = builder.distDir
        this.output = builder.output
        this.project = builder.project
    }

    String getTemplateLocation() {
        return templateLocation
    }

    Collection<GroovyClassDoc> getClasses() {
        return classes
    }

    String getDistDir() {
        return distDir
    }

    OutputTool getOutput() {
        return output
    }

    Project getProject() {
        return project
    }

    static class Builder {
        private String templateLocation
        private Collection<GroovyClassDoc> classes
        private String distDir
        private OutputTool output
        private Project project

        Builder setTemplateLocation(String templateLocation) {
            this.templateLocation = templateLocation
            return this
        }

        Builder setClasses(Collection<GroovyClassDoc> classes) {
            this.classes = classes
            return this
        }

        Builder setDistDir(String distDir) {
            this.distDir = distDir
            return this
        }

        Builder setOutput(OutputTool output) {
            this.output = output
            return this
        }

        Builder setProject(Project project) {
            this.project = project
            return this
        }

        Context build() {
            return new Context(this)
        }
    }

}
