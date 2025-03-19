/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.groovy.antlr.override;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.util.ArrayIterator;

/**
 * Defines new groovy methods which appear on arrays inside the Groovy environment.
 * Static methods are used with the first parameter being the destination class,
 * i.e. <code>public static int[] each(int[] self, Closure closure)</code>
 * provides an <code>each({i -> })</code> method for <code>int[]</code>.
 * <p>
 * NOTE: While this class contains many 'public' static methods, it is
 * primarily regarded as an internal class (its internal package name
 * suggests this also). We value backwards compatibility of these
 * methods when used within Groovy but value less backwards compatibility
 * at the Java method call level. I.e. future versions of Groovy may
 * remove or move a method call in this file but would normally
 * aim to keep the method available from within Groovy.
 */

// TODO: Remove after update Groovy v4, this part of code is taken from this version

public class ArrayGroovyMethods extends DefaultGroovyMethodsSupport {

    private ArrayGroovyMethods() {
    }

    /**
     * Concatenates the string representation of each item in this array,
     * with the given String as a separator between each item.
     *
     * <pre class="groovyTestCase">
     * Serializable[] array = [1,2L,-3G]
     * assert array.join("+") == "1+2+-3"
     * </pre>
     *
     * @param self      an array of Object
     * @param separator a String separator
     * @return the joined String
     * @since 1.0
     */
    public static String join(Object[] self, String separator) {
        return DefaultGroovyMethods.join(new ArrayIterator<>(self), separator);
    }
}