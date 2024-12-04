/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.generator.chapter

import au.com.ish.docs.generator.root.SectionContext
import au.com.ish.docs.helpers.Helper
import org.codehaus.groovy.groovydoc.GroovyClassDoc

import static au.com.ish.docs.utils.GroovyDocUtils.getIsVisible

class ChapterContext extends SectionContext {

    protected GroovyClassDoc classDoc
    protected def visibleMethods
    protected def visibleConstructors

    protected ChapterContext(Builder builder) {
        super(builder)
        this.classDoc = builder.classDoc
        this.visibleMethods =  getVisibleMethods.call(classDoc)
        this.visibleConstructors = getVisibleConstructors.call(classDoc)
    }

    @Helper
    GroovyClassDoc getClassDoc() {
        return classDoc
    }

    @Helper
    def getVisibleMethods() {
        return visibleMethods
    }

    @Helper
    def getVisibleConstructors() {
        return visibleConstructors
    }

    private static getVisibleMethods = { doc ->
        (doc.methods().findAll(isVisible) + doc.superclass()?.methods()?.findAll(isVisible)).findAll()
    }

    private static getVisibleConstructors = { doc ->
        doc.constructors().findAll(isVisible)
    }

    static class Builder extends SectionContext.Builder<Builder> {

        protected GroovyClassDoc classDoc

        Builder setClassDoc(GroovyClassDoc classDoc) {
            this.classDoc = classDoc
            return this
        }

        ChapterContext build() {
            ChapterContext context = new ChapterContext(this)
            validate(context)
            return context
        }

        Builder init(SectionContext context) {
            super.init(context)
            return this
        }

        private static void validate(ChapterContext context) {
            if (context.classDoc == null) {
                throw new IllegalArgumentException("'classDoc' field is required.")
            }
        }
    }
}
