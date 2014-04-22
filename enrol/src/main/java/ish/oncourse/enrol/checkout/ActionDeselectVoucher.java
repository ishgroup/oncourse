package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Voucher;
import org.apache.cayenne.Cayenne;

public class ActionDeselectVoucher extends APurchaseAction {

    private Voucher voucher;

    @Override
    protected void makeAction() {
        getModel().deselectVoucher(voucher);
    }


    @Override
    protected boolean validate() {
        return true;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    @Override
    protected void parse() {
        if (getParameter() != null)
        {
            Long voucherId = getParameter().getValue(Long.class);
            if (voucherId != null)
            {
                this.voucher = Cayenne.objectForPK(getModel().getObjectContext(), Voucher.class, voucherId);
            }
        }
    }
}