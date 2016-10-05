package ish.oncourse.enrol.checkout;

import ish.common.types.CourseEnrolmentType;
import ish.oncourse.model.Application;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Product;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;

import static ish.oncourse.enrol.checkout.PurchaseController.Action;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.addCompanyPayer;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.addPersonPayer;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.addVoucherCompanyPayer;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.addVoucherPersonPayer;
import static ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;

public class ActionAddVoucherPayer extends APurchaseAction {

    private Voucher voucher;

    @Override
    protected void makeAction() {
		
		Contact contact = voucher.getContact();
        if (contact != null) {
            boolean isEditPayment = false;
			
            if (getController().getState().equals(PurchaseController.State.editPayment)) {
                isEditPayment = true;
            }

			if (contact.equals(getModel().getPayer())) {
				return;
			} else if (getModel().getContacts().contains(contact)) {
                ActionChangePayer actionChangePayer = new ActionChangePayer();
                actionChangePayer.setContact(contact);
                getController().performAction(actionChangePayer, Action.changePayer);
            } else {
                getController().setState(PurchaseController.State.editContact);
				
				Action action = contact.getIsCompany() ? (isEditPayment ? addCompanyPayer : addVoucherCompanyPayer) : (isEditPayment ? addPersonPayer : addVoucherPersonPayer);
				ActionParameter actionParameter = new ActionParameter(action);
				actionParameter.setValue(getController().getCayenneService().newContext().localObject(contact));
				getController().performAction(actionParameter);

				if (contact.getStudent() == null) {
					contact.createNewStudent();
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



