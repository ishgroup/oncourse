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

package ish.oncourse.server.upgrades;

import ish.common.types.DataType;
import ish.oncourse.common.ResourceProperty;
import ish.oncourse.server.cayenne.AutomationBinding;
import ish.oncourse.server.cayenne.AutomationTrait;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ish.oncourse.common.ResourceProperty.*;
import static ish.oncourse.server.upgrades.DataPopulationUtils.get;
import static ish.oncourse.server.upgrades.DataPopulationUtils.getString;

public class BindingUtils {


    private static final Logger logger = LogManager.getLogger();

    public static void updateOptions(ObjectContext objectContext, List<Map<String, Object>> options, AutomationTrait automation, Class<? extends AutomationBinding> clazz) {
        List<AutomationBinding> updatedOptions = new ArrayList<>();

        if (options != null) {
            for (var option : options) {
                var name = option.get(NAME.getDisplayName()).toString();
                var value = option.get(ResourceProperty.VALUE.getDisplayName()).toString();
                var dataType = DataType.valueOf(option.get(ResourceProperty.DATA_TYPE.getDisplayName()).toString());

                if (StringUtils.isEmpty(name) ||  value == null  || dataType == null) {
                    logger.debug("Options not configured correctly");
                    throw new IllegalStateException("Options not configured correctly");
                }

                AutomationBinding binding = null;
                if (automation.getOptions().size() > 0) {
                    binding = automation.getOptions().stream().filter(b -> name.equals(b.getName())).findFirst().orElse(null);
                }
                if (binding == null) {
                    binding = objectContext.newObject(clazz);
                    binding.setAutomation(automation);
                    binding.setValue(value);
                } else {
                    updatedOptions.add(binding);
                }

                binding.setName(name);
                binding.setDataType(dataType);
            }
        }

        deleteNotActualBindings(objectContext, updatedOptions, automation.getOptions());
        objectContext.commitChanges();

    }

    public static void updateVariables(ObjectContext objectContext, List<Map<String, Object>> variables, AutomationTrait automation, Class<? extends AutomationBinding> clazz) {
        objectContext.deleteObjects(automation.getVariables());
        objectContext.commitChanges();
        if (variables == null) {
            return;
        }
        variables.forEach( var -> {
            AutomationBinding binding = objectContext.newObject(clazz);
            binding.setAutomation(automation);

            binding.setName(getString(var, NAME));
            binding.setLable(getString(var, LABEL));
            binding.setDataType(get(var, DATA_TYPE, DataType.class));

            if (StringUtils.isEmpty(binding.getName()) || StringUtils.isEmpty(binding.getLable())  || binding.getDataType() == null) {
                logger.debug("Variables not configured correctly");
                throw new IllegalStateException("Variables not configured correctly");
            }
            objectContext.commitChanges();
        });
    }

    private static void deleteNotActualBindings(ObjectContext context, List<AutomationBinding> updatedBindings, List<? extends AutomationBinding> allBindings) {
        var notActualBindings = allBindings.stream()
                .filter(b -> b.getId() != null && updatedBindings.stream().noneMatch(upd -> upd.getId().equals(b.getId())))
                .collect(Collectors.toList());

        context.deleteObjects(notActualBindings);
    }
}
