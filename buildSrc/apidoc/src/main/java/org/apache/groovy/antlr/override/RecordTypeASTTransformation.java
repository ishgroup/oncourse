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

import org.apache.groovy.lang.annotation.Incubating;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.transform.GroovyASTTransformation;

/**
 * Handles generation of code for the @RecordType annotation.
 */

// TODO: Remove after update Groovy v4, this part of code is taken from this version

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class RecordTypeASTTransformation {

    private static final String RECORD_CLASS_NAME = "java.lang.Record";

    /**
     * Indicates that the given classnode is a native JVM record class.
     * For classes being compiled, this will only be valid after the
     * {@code RecordTypeASTTransformation} transform has been invoked.
     */
    @Incubating
    public static boolean recordNative(final ClassNode node) {
        return node.getUnresolvedSuperClass() != null && RECORD_CLASS_NAME.equals(node.getUnresolvedSuperClass().getName());
    }

}
