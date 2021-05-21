package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._MembershipFieldConfiguration;

public class MembershipFieldConfiguration extends _MembershipFieldConfiguration {

    @Override
    public FieldConfigurationType getType() {
        return FieldConfigurationType.MEMBERSHIP;
    }
}
