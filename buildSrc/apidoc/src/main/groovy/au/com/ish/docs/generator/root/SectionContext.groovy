/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.generator.root

import au.com.ish.docs.helpers.Helper
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.OutputTool
import org.gradle.api.Project

class SectionContext {

    private Collection<GroovyClassDoc> classes
    private String distDir
    private OutputTool output
    private Project project

    protected SectionContext(Builder builder) {
        this.classes = builder.classes
        this.distDir = builder.distDir
        this.output = builder.output
        this.project = builder.project
    }

    @Helper
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

    static class Builder<T extends Builder<T>> {

        protected Collection<GroovyClassDoc> classes
        protected String distDir
        protected OutputTool output
        protected Project project

        protected T self() {
            return (T) this
        }

        T setClasses(Collection<GroovyClassDoc> classes) {
            this.classes = classes
            return self()
        }

        T setDistDir(String distDir) {
            this.distDir = distDir
            return self()
        }

        T setOutput(OutputTool output) {
            this.output = output
            return self()
        }

        T setProject(Project project) {
            this.project = project
            return self()
        }

        T init(SectionContext context) {
            this.classes = context.classes
            this.output = context.output
            this.distDir = context.distDir
            this.project = context.project
            return self()
        }

        SectionContext build() {
            return new SectionContext(this)
        }
    }
}
