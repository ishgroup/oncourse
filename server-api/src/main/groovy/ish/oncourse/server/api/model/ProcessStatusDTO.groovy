/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.model

import io.swagger.annotations.ApiModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import ish.oncourse.server.api.traits._DTOTrait
import ish.oncourse.server.api.traits.ProcessStatusDTOTrait

/**
 * Status of the process
 */
public enum ProcessStatusDTO implements _DTOTrait, ProcessStatusDTOTrait {

    NOT_FOUND("Not found"),

    IN_PROGRESS("In progress"),

    FINISHED("Finished"),

    FAILED("Failed");

    private String value;

    ProcessStatusDTO(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static ProcessStatusDTO fromValue(String text) {
        for (ProcessStatusDTO b : ProcessStatusDTO.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

}

