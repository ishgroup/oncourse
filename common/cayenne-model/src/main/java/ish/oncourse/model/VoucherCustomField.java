package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._VoucherCustomField;

public class VoucherCustomField extends _VoucherCustomField {

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((Voucher) relatedObject);
    }
}
