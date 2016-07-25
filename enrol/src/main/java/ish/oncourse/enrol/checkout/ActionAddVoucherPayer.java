package ish.oncourse.enrol.checkout;

import ish.common.types.CourseEnrolmentType;
import ish.oncourse.model.Application;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Product;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.addCompanyPayer;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.addPersonPayer;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.addVoucherCompanyPayer;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.addVoucherPersonPayer;

public class ActionAddVoucherPayer extends APurchaseAction {

    private Voucher voucher;

    @Override
    protected void makeAction() {
		
		Contact contact = voucher.getContact();
        if (contact != null) {
            boolean isEditPayment = false;
			boolean addPurchaces = false;
			
            if (getController().getState().equals(PurchaseController.State.editPayment)) {
                isEditPayment = true;
            }

			if (contact.equals(getModel().getPayer())) {
				return;
			} else if (getModel().getContacts().contains(contact)) {
                ActionChangePayer actionChangePayer = new ActionChangePayer();
                actionChangePayer.setContact(contact);
                getController().performAction(actionChangePayer, PurchaseController.Action.changePayer);
            } else {
				addPurchaces = true;
                getController().setState(PurchaseController.State.editContact);
                PurchaseController.ActionParameter actionParameter;
                if (contact.getIsCompany()) {

                    if (isEditPayment) {
                        actionParameter = new PurchaseController.ActionParameter(addCompanyPayer);
                    } else {
                        //actionParameter = new PurchaseController.ActionParameter(addCompanyPayer);
                        actionParameter = new PurchaseController.ActionParameter(addVoucherCompanyPayer);
                    }
                    actionParameter.setValue(contact);
                    getController().performAction(actionParameter);
                } else {
                    if (isEditPayment) {
                        actionParameter = new PurchaseController.ActionParameter(addPersonPayer);
                    } else {
                        //actionParameter = new PurchaseController.ActionParameter(addPersonPayer);
                        actionParameter = new PurchaseController.ActionParameter(addVoucherPersonPayer);
                    }
                    actionParameter.setValue(contact);
                    getController().performAction(actionParameter);
					
					if (contact.getStudent() == null) {
						contact.createNewStudent();
					}

                }

				if (!isEditPayment) {
					if (!contact.getIsCompany()) {
						for (CourseClass courseClass : getModel().getClasses()) {
							if (CourseEnrolmentType.ENROLMENT_BY_APPLICATION.equals(courseClass.getCourse().getEnrolmentType()) &&
									getController().getApplicationService().findOfferedApplicationBy(courseClass.getCourse(), contact.getStudent()) == null) {
								Application application = getModel().getApplicationBy(contact, courseClass.getCourse());
								if (application == null) {
									application = getController().createApplication(contact.getStudent(), courseClass.getCourse());
									getModel().addApplication(application);
								}
							} else {
								Enrolment enrolment = getModel().createEnrolment(courseClass, contact.getStudent());
								getModel().addEnrolment(enrolment);
							}
						}
					}

					for (Product product : getModel().getProducts()) {
						//vouchers already relinked
						if (!(product instanceof VoucherProduct)) {
							getModel().addProductItem(getController().createProductItem(contact, product));
						}
					}
				}
            }
        }
    }

    @Override
    protected void parse() {

    }

    @Override
    protected boolean validate() {
        return true;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}



