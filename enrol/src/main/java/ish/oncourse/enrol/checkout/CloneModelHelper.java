package ish.oncourse.enrol.checkout;

import ish.oncourse.model.*;
import ish.oncourse.services.voucher.VoucherRedemptionHelper;
import org.apache.cayenne.ObjectContext;

import java.util.List;

public class CloneModelHelper {

    private ObjectContext objectContext;
    private PurchaseController purchaseController;

    public void cloneModel() {
        PurchaseModel oldModel = purchaseController.getModel();
        PurchaseModel newModel = new PurchaseModel();
        purchaseController.setModel(newModel);

        newModel.setObjectContext(objectContext);
        newModel.setCollege(newModel.localizeObject(oldModel.getCollege()));
        newModel.setWebSite(newModel.localizeObject(oldModel.getWebSite()));
        newModel.setDiscounts(newModel.localizeObjects(oldModel.getDiscounts()));
        newModel.setPayer(newModel.localizeObject(oldModel.getPayer()));
        newModel.setClasses(newModel.localizeObjects(oldModel.getClasses()));
        newModel.setProducts(newModel.localizeObjects(oldModel.getProducts()));

        List<Contact> oldContacts = oldModel.getContacts();
        List<CourseClass> oldClasses = oldModel.getClasses();
        List<Product> oldProducts = oldModel.getProducts();
        for (Contact oldContact : oldContacts) {
            Contact contact = newModel.localizeObject(oldContact);
            newModel.addContact(contact);
			
			if (!contact.getIsCompany()) {
				for (CourseClass oldCourseClass : oldClasses) {
					CourseClass courseClass = newModel.localizeObject(oldCourseClass);
					Enrolment enrolment = purchaseController.createEnrolment(courseClass,
							contact.getStudent());
					newModel.addEnrolment(enrolment);
					Enrolment oldEnrolment = oldModel.getEnrolmentBy(oldContact, oldCourseClass);
					if (oldEnrolment != null) {
						ActionEnableEnrolment actionEnableEnrolment = new ActionEnableEnrolment();
						actionEnableEnrolment.setController(purchaseController);
						actionEnableEnrolment.setEnrolment(enrolment);
						actionEnableEnrolment.action();
					}
				}
			}

            for (Product oldProduct : oldProducts) {
                Product product = newModel.localizeObject(oldProduct);
                ProductItem productItem = purchaseController.createProductItem(contact, product);
                newModel.addProductItem(productItem);
                ProductItem oldProductItem = oldModel.getProductItemBy(oldContact, oldProduct);
                if (oldProductItem != null) {
                    ActionEnableProductItem actionEnableProductItem = new ActionEnableProductItem();
                    actionEnableProductItem.setController(purchaseController);
                    actionEnableProductItem.setProductItem(productItem);
                    actionEnableProductItem.action();
                }
            }

            //adding vouchers to new model
            List<Voucher> oldVouchers = oldModel.getVouchers();
            for (Voucher oldVoucher : oldVouchers) {
                ActionAddVoucher action = new ActionAddVoucher();
                action.setVoucher(newModel.localizeObject(oldVoucher));
                action.setController(purchaseController);
                action.action();
            }
        }

        VoucherRedemptionHelper voucherRedemptionHelper = new VoucherRedemptionHelper(newModel.getObjectContext(),
                newModel.getCollege());
        purchaseController.setVoucherRedemptionHelper(voucherRedemptionHelper);
        voucherRedemptionHelper.setInvoice(newModel.getInvoice());
    }

    public ObjectContext getObjectContext() {
        return objectContext;
    }

    public void setObjectContext(ObjectContext objectContext) {
        this.objectContext = objectContext;
    }

    public PurchaseController getPurchaseController() {
        return purchaseController;
    }

    public void setPurchaseController(PurchaseController purchaseController) {
        this.purchaseController = purchaseController;
    }
}
