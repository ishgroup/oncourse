package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._VoucherProductCustomField;

public class VoucherProductCustomField extends _VoucherProductCustomField {

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((VoucherProduct) relatedObject);
    }
}
