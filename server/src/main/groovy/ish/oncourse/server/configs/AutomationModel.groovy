/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.configs

import com.fasterxml.jackson.annotation.JsonInclude
import ish.oncourse.server.cayenne.AutomationTrait

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class AutomationModel {
    private String shortDescription
    private String description
    private String automationTags
    private String category
    private String name

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<VariableModel> variables;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<OptionModel> options;

    protected AutomationModel(AutomationTrait automationTrait) {
        this.description = automationTrait.description
        this.shortDescription = automationTrait.description
        this.category = automationTrait.category
        this.automationTags = automationTrait.automationTags
        this.name = automationTrait.name
        this.variables = automationTrait.variables.collect { it -> new VariableModel(it) }
        this.options = automationTrait.options.collect { it -> new OptionModel(it) }
    }

    String getShortDescription() {
        return shortDescription
    }

    String getDescription() {
        return description
    }

    String getAutomationTags() {
        return automationTags
    }

    String getCategory() {
        return category
    }

    String getName() {
        return name
    }

    List<VariableModel> getVariables() {
        return variables
    }

    List<OptionModel> getOptions() {
        return options
    }
}
