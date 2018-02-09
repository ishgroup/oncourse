package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._ApplicationFieldConfiguration;

public class ApplicationFieldConfiguration extends _ApplicationFieldConfiguration {

    private static final long serialVersionUID = 1L;

    @Override
    public FieldConfigurationType getType() {
        return FieldConfigurationType.APPLICATION;
    }
}
