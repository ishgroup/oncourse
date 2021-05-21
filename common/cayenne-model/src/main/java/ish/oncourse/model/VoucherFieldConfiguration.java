package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._VoucherFieldConfiguration;

public class VoucherFieldConfiguration extends _VoucherFieldConfiguration {

    @Override
    public FieldConfigurationType getType() {
        return FieldConfigurationType.VOUCHER;
    }
}
