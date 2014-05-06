package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Voucher;
import org.apache.cayenne.Cayenne;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;

public class ActionSelectVoucher extends APurchaseAction {

    /**
     * the property was introduce to allow to use the action as validator.
     * we use the property in PurchaseModelValidator to be sure that contact is in the same context as voucher
     */
    private Contact contact;
    private Voucher voucher;

    @Override
    protected void makeAction() {
        getModel().selectVoucher(voucher);
    }

    @Override
    protected void parse() {

        if (getParameter() != null) {
            Long voucherId = getParameter().getValue(Long.class);
            if (voucherId != null) {
                this.voucher = Cayenne.objectForPK(getModel().getObjectContext(), Voucher.class, voucherId);
            }
        }

        if (contact == null) {
            contact = getModel().getPayer();
        }
    }

    @Override
    protected boolean validate() {
        if (!voucher.canBeUsedBy(contact)) {
            getController().addError(voucherWrongPayer, voucher.getContact().getFullName());
            return false;
        }

        if (voucher.isFullyRedeemed()) {
            getController().addError(voucherRedeemed);
            return false;
        }

        if (voucher.isInUse()) {
            getController().addError(voucherAlreadyBeingUsed);
            return false;
        }
        return true;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
