package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._PayerFieldConfiguration;

public class PayerFieldConfiguration extends _PayerFieldConfiguration {

    private static final long serialVersionUID = 1L;

    @Override
    public FieldConfigurationType getType() {
        return FieldConfigurationType.PAYER;
    }
}
