package ish.oncourse.server.cayenne

import ish.common.types.FieldConfigurationType
import ish.oncourse.server.cayenne.glue._VoucherFieldConfiguration

class VoucherFieldConfiguration extends _VoucherFieldConfiguration {

    @Override
    FieldConfigurationType getType() {
        return FieldConfigurationType.VOUCHER
    }
}
