package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._EnrolmentFieldConfiguration;

public class EnrolmentFieldConfiguration extends _EnrolmentFieldConfiguration {

    private static final long serialVersionUID = 1L;

    @Override
    public FieldConfigurationType getType() {
        return FieldConfigurationType.ENROLMENT;
    }
}
