/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package org.codehaus.groovy.tools.groovydoc

import org.codehaus.groovy.groovydoc.GroovyClassDoc

/**
 * A wrapper class for {@link SimpleGroovyMethodDoc} that simplifies the interaction by encapsulating
 * its functionality and exposing a convenient interface for retrieving the associated class documentation.</p>
 *
 * <h2>Usage Example:</h2>
 * <p>
 * {@code
 * SimpleGroovyMethodDoc methodDoc = ... ; // Initialize method documentation
 * SimpleGroovyMethodDocWrapper wrapper = SimpleGroovyMethodDocWrapper.valueOf(methodDoc);
 * GroovyClassDoc belongsToClass = wrapper.getBelongsToClass();
 * }
 * </p>
 *
 * <h2>Key Methods:</h2>
 * <ul>
 *   <li>{@link SimpleGroovyMethodDocWrapper#valueOf(SimpleGroovyMethodDoc)} - Static factory method to instantiate the wrapper.</li>
 *   <li>{@link SimpleGroovyMethodDocWrapper#getBelongsToClass()} - Retrieves the {@link GroovyClassDoc} representing the class the method belongs to.</li>
 * </ul>
 *
 * <h2>Dependencies:</h2>
 * <ul>
 *   <li>{@link SimpleGroovyMethodDoc} - Encapsulates documentation of a Groovy method.</li>
 *   <li>{@link GroovyClassDoc} - Represents the documentation for the Groovy class containing the method.</li>
 * </ul>
 */
class SimpleGroovyMethodDocWrapper {

    private SimpleGroovyMethodDoc methodDoc

    static SimpleGroovyMethodDocWrapper valueOf(SimpleGroovyMethodDoc methodDoc) {
        return new SimpleGroovyMethodDocWrapper().with {
            it.methodDoc = methodDoc
            it
        }
    }

    GroovyClassDoc getBelongsToClass () {
        return methodDoc.belongsToClass
    }

}
