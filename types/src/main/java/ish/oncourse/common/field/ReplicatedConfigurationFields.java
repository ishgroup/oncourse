/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.common.field;

public enum ReplicatedConfigurationFields {

    S3_DOCUMENT_STORAGE_LIMIT ("document.limit");

    private final String value;

    ReplicatedConfigurationFields(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] getValues() {
        return new String[] {
                S3_DOCUMENT_STORAGE_LIMIT.value
        };
    }
}
