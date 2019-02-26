package ish.oncourse.model;

import ish.common.types.InvoiceType;
import ish.oncourse.model.auto._SaleOrder;

public class SaleOrder extends _SaleOrder {

    private static final long serialVersionUID = 1L;

    @Override
    public InvoiceType getType() {
        return InvoiceType.SALE_ORDER;
    }
}
