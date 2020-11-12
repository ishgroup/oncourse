/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package io.swagger.codegen.languages;

import io.swagger.codegen.CodegenOperation;

public class TypeScriptCodegenOperation extends CodegenOperation {

    // our Axios HttpService does not use data for GET and DELETE methods

    public boolean isGetOrDeleteMethod() {
        return "GET".equals(this.httpMethod) || "DELETE".equals(this.httpMethod);
    }
}
