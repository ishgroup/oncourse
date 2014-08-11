package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Voucher;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.*;

public class PurchaseModelValidator {

    private PurchaseController purchaseController;

    private boolean validateEnrolments(boolean showErrors) {
        ActionEnableEnrolment actionEnableEnrolment = enableEnrolment.createAction(purchaseController);
        List<Enrolment> enrolments = this.getModel().getAllEnabledEnrolments();
        boolean result = true;
        for (Enrolment enrolment : enrolments) {
            actionEnableEnrolment.setEnrolment(enrolment);
            boolean valid = actionEnableEnrolment.validateEnrolment(showErrors);
            if (!valid) {
                ActionDisableEnrolment actionDisableEnrolment = disableEnrolment.createAction(purchaseController);
                actionDisableEnrolment.setEnrolment(enrolment);
                actionDisableEnrolment.action();
                getModel().removeEnrolment(enrolment);
            }
            result = result && valid;
        }
        return result;
    }

    private boolean validateProductItems() {
        ActionEnableProductItem actionEnableProductItem = PurchaseController.Action.enableProductItem.createAction(purchaseController);
        List<ProductItem> items = this.getModel().getAllEnabledProductItems();
        boolean result = true;
        for (ProductItem item : items) {
            actionEnableProductItem.setProductItem(item);
            //this step needed, because possible exist voucher product without price
            actionEnableProductItem.setPrice(item.getInvoiceLine().getPriceEachExTax());
            boolean valid = actionEnableProductItem.validateProductItem();
            if (!valid) {
                ActionDisableProductItem actionDisableProductItem = disableProductItem.createAction(purchaseController);
                actionDisableProductItem.setProductItem(item);
                actionDisableProductItem.action();
                getModel().removeProductItem(getModel().getPayer(), item);
            }
            result = result && valid;
        }
        return result;
    }

    private boolean validateRedeemingVouchers() {

        ObjectContext testOC = purchaseController.getCayenneService().newNonReplicatingContext();

        ActionSelectVoucher actionSV = PurchaseController.Action.selectVoucher.createAction(purchaseController);

        List<Voucher> vouchers = this.getModel().getVouchers();
        boolean result = true;
        for (Voucher voucher : vouchers) {

            if (this.getModel().isSelectedVoucher(voucher)) {
                //we need to load the v form the data base to be sure that other process does not use the voucher.
                Voucher dbVoucher = Cayenne.objectForPK(testOC, Voucher.class, voucher.getId());
                actionSV.setVoucher(dbVoucher);
                actionSV.setContact(testOC.localObject(getModel().getPayer()));
                boolean valid = actionSV.validate();
                if (!valid) {
                    ActionDeselectVoucher actionDV = deselectVoucher.createAction(purchaseController);
                    actionDV.setVoucher(voucher);
                    actionDV.action();
                }
                result = result && valid;
            }
        }
        return result;
    }

    public PurchaseController getPurchaseController() {
        return purchaseController;
    }

    public void setPurchaseController(PurchaseController purchaseController) {
        this.purchaseController = purchaseController;
    }

    public PurchaseModel getModel() {
        return purchaseController.getModel();
    }

    public boolean validate() {
        return validateContacts() && validateEnrolments(true) && validateProductItems() && validateRedeemingVouchers();
    }

    public boolean validateContacts() {
        boolean result = true;
        List<Contact> contacts = getModel().getContacts();
        for (Contact contact : contacts) {
            if (purchaseController.needGuardianFor(contact) && purchaseController.getGuardianFor(contact) == null)
            {
                getPurchaseController().addError(PurchaseController.Message.contactNeedsGuardian, contact.getFullName());
                result = false;
            }
        }

        return result;
    }
}
