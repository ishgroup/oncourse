package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.RealDiscountsPolicy;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class EnrolmentList {

    @Parameter(required = true)
    private PurchaseController purchaseController;

    @Parameter(required = true)
    @Property
    private Contact contact;

    @Property
    private Enrolment enrolment;

    @Property
    private Integer index;

    @Inject
    private Request request;

    @Property
    @Parameter(required = false)
    private Block blockToRefresh;


    public Integer getContactIndex() {
        return purchaseController.getModel().getContacts().indexOf(contact);
    }

    public Boolean getChecked() {
        return purchaseController.getModel().isEnrolmentEnabled(enrolment);
    }

    public String getEnrolmentError() {
        return purchaseController.getModel().getErrorBy(enrolment);
    }

    public List<Enrolment> getEnrolments() {
        return purchaseController.getModel().getAllEnrolments(contact);
    }

    public EnrolmentItem.EnrolmentItemDelegate getEnrolmentItemDelegate() {
        return new EnrolmentItem.EnrolmentItemDelegate() {
            @Override
            public void onChange(Integer contactIndex, Integer enrolmentIndex) {
                Contact contact = purchaseController.getModel().getContacts().get(contactIndex);
                Enrolment enrolment = purchaseController.getModel().getAllEnrolments(contact).get(enrolmentIndex);
                Boolean isSelected = purchaseController.getModel().isEnrolmentEnabled(enrolment);
                ActionParameter actionParameter = new ActionParameter(isSelected ? Action.disableEnrolment : Action.enableEnrolment);
                actionParameter.setValue(enrolment);
                purchaseController.performAction(actionParameter);
            }

            @Override
            public List<Discount> getDiscounts() {
                return enrolment.getCourseClass().getDiscountsToApply(
                        new RealDiscountsPolicy(purchaseController.getModel().getDiscounts(),
								enrolment.getStudent(), purchaseController.getModel().getNewInvoices()));
            }
		};
    }
}
