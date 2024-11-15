/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.helpers

import com.github.jknack.handlebars.Options
import org.apache.commons.collections4.CollectionUtils
import org.codehaus.groovy.groovydoc.GroovyMethodDoc

/**
 * The {@code CollectionHelper} class provides utility methods for working with collections, specifically
 * tailored for use within Handlebars template generators. This class groups functionality that enhancing
 * the flexibility and customization of template rendering.
 */
class CollectionHelper {

    /**
     * Sorts a collection of {@code GroovyMethodDoc} objects based on a specified field from the {@code Options}.
     * If no field is provided in the {@code Options}, the collection is sorted by the default field, {@code 'name'}.
     * <p>
     * This method is particularly useful in Handlebars templates where dynamic sorting of data is required
     * during template generation.
     * <p>
     * Example usage in Handlebars:
     * <pre>{@code
     *  {{#each (sort enums "name")}}
     *      ...
     *  {{/each}}
     * }</pre>
     *
     * @param methods the collection of {@code GroovyMethodDoc} objects to be sorted
     * @param options the {@code Options} object containing parameters, including the sorting field
     * @return the sorted collection of {@code GroovyMethodDoc} objects, or the original collection if empty or null
     */
    @Helper
    def static sort(Collection<GroovyMethodDoc> methods, Options options) {
        String field = options.params.length >= 1 ? options.param(0) as String : 'name'
        if (CollectionUtils.isNotEmpty(methods)) {
            methods.sort { a, b ->
                def aValue = field == 'name' ? a.name() : a[field]
                def bValue = field == 'name' ? b.name() : b[field]
                aValue <=> bValue
            }
        }
        return methods
    }

}
