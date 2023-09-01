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

package ish.oncourse.server.api.function

import ish.oncourse.server.api.traits.AutomationDTOTrait
import ish.oncourse.server.api.v1.model.BindingDTO
import ish.oncourse.server.api.v1.model.DataTypeDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.AutomationBinding
import ish.oncourse.server.cayenne.AutomationTrait
import ish.print.AdditionalParameters
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import static   ish.oncourse.server.export.ExportService.*
import static org.apache.http.util.TextUtils.isBlank

class BindingFunctions {

    public static final List<String> RESERVED_VARIABLES = [CONTEXT,OUTPUT,RESULT,PREFERENCE,RECORDS,XML,CSV,JSON]
    static {
        AdditionalParameters.values().each { RESERVED_VARIABLES << it.toString() }
    }

    static void  populateAutomationBindings(AutomationDTOTrait automationDto, AutomationTrait automation) {
        automation.automationBindings.sort { it.id }.each { binding ->
            BindingDTO dto = new BindingDTO()
            dto.value = binding.value
            dto.type = DataTypeDTO.values()[0].fromDbType(binding.dataType)
            dto.name = binding.name
            dto.label = binding.lable
            if (dto.value != null) {
                automationDto.options << dto
            } else {
                automationDto.variables << dto
            }
        }
    }

    static void updateBuiltInOptions(AutomationTrait automation, List<BindingDTO> options) {
        automation.options.eachWithIndex { opt,i ->
            BindingDTO dtoOpt = options.find {it.name == opt.name}
            if (dtoOpt) {
                if (dtoOpt.value == null) {
                    EntityValidator.throwClientErrorException(automation.id, "options[$i].value", "Value is required for $opt.name option")
                }

                if (opt.parseValue(dtoOpt.value) != null) {
                    opt.value = dtoOpt.value
                } else {
                    EntityValidator.throwClientErrorException(automation.id, "options[$i].value", "$dtoOpt.value is not valid for $dtoOpt.name option")
                }
            }
        }
    }

    static void updateAutomationBindings(AutomationTrait automation, AutomationDTOTrait automationDTOTrait) {
        ObjectContext context = (automation as PersistentObject).objectContext
        updateBindings(automation, automation.options, automationDTOTrait.options,context, true)
        updateBindings(automation, automation.variables, automationDTOTrait.variables, context, false)
    }

    static private void updateBindings(AutomationTrait automation, List<AutomationBinding> dbBindings, List<BindingDTO> dtoBindings, ObjectContext context, boolean isOpt) {
        context.deleteObjects(dbBindings.findAll { opt -> !(opt.name in dtoBindings*.name) })

        dtoBindings.eachWithIndex { dtoBinding, i ->
            AutomationBinding dbBinding = dbBindings.find { it -> it.name == dtoBinding.name }
            if (!dbBinding) {
                dbBinding = context.newObject(automation.automationBindingClass)
                dbBinding.automation = automation
            }
            dbBinding.name = dtoBinding.name
            dbBinding.dataType = dtoBinding.type.dbType

            if (isOpt) {
                if (dbBinding.parseValue(dtoBinding.value) != null) {
                    dbBinding.value = dtoBinding.value
                } else {
                    EntityValidator.throwClientErrorException(automation.id, "options[$i].value", "$dtoBinding.value is not valid for $dtoBinding.name option")
                }
            }

            dbBinding.lable = dtoBinding.label
        }
    }

    static void validate(AutomationDTOTrait dto, EntityValidator validator) {
        dto.variables.eachWithIndex { var, i ->
            if (isBlank(var.name)) {
                validator.throwClientErrorException("variables[$i].name", 'Variable name is required')
            }

            if (isBlank(var.label)) {
                validator.throwClientErrorException("variables[$i].lable", 'Variable lable is required')
            }
            if (!var.type) {
                validator.throwClientErrorException("variables[$i].type", 'Variable type is required')
            }
        }

        dto.options.eachWithIndex { opt, i ->
            if (isBlank(opt.name)) {
                validator.throwClientErrorException("options[$i].name", 'Option name is required')
            }

            if (!opt.type) {
                validator.throwClientErrorException("options[$i].type", 'Option type is required')
            }

            if (opt.value == null && opt.type == DataTypeDTO.MONEY) {
                validator.throwClientErrorException("options[$i].lable", 'Option value with Money type is required')
            }
        }

        List<BindingDTO> bindings = dto.options + dto.variables
        String duplicate = bindings.countBy { it.name.toLowerCase() }.find { it.value > 1 }?.key
        if (duplicate) {
            validator.throwClientErrorException(null, "Option/Variable name should be unique: $duplicate")
        }

        String reserved = bindings.find { it.name in RESERVED_VARIABLES }
        if (reserved) {
            validator.throwClientErrorException(null, " $reserved reserved key word.")
        }

    }
}
