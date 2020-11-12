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

public class LinkRestCodegenOperation extends CodegenOperation {

    public boolean crudOperation;
    public String superClassName;
    public String uniqueMapper;


    public String getUniqueMapper() {
        return uniqueMapper;
    }

    public void setUniqueMapper(String uniqueMapper) {
        this.uniqueMapper = uniqueMapper;
    }

    public boolean isCrudOperation() {
        return crudOperation;
    }

    public void setCrudOperation(boolean crudOperation) {
        this.crudOperation = crudOperation;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public boolean isRestfulShow() {
        return crudOperation && "GET".equalsIgnoreCase(httpMethod);
    }

    public boolean isRestfulCreate() {
        return crudOperation && "POST".equalsIgnoreCase(httpMethod);
    }

}
