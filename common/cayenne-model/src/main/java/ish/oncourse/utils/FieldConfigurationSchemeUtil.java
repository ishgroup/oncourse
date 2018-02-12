package ish.oncourse.utils;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.FieldConfigurationLink;
import ish.oncourse.model.FieldConfigurationScheme;

public class FieldConfigurationSchemeUtil {
    
    public static FieldConfiguration getConfiguration(FieldConfigurationScheme self, FieldConfigurationType type, FieldConfiguration configuration) {
        return self.getFieldConfigurationLinks().stream()
                .filter(fcl -> type.equals(fcl.getFieldConfiguration().getType()))
                .map(FieldConfigurationLink::getFieldConfiguration)
                .findFirst()
                .orElse(configuration);
    }
}
