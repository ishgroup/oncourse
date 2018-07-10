package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._ParentFieldConfiguration;

public class ParentFieldConfiguration extends _ParentFieldConfiguration {

    private static final long serialVersionUID = 1L;

    @Override
    public FieldConfigurationType getType() {
        return FieldConfigurationType.PARENT;
    }
}
