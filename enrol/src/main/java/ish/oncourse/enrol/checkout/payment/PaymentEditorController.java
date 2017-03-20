package ish.oncourse.enrol.checkout.payment;

import ish.common.types.ConfirmationStatus;
import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.model.InvoiceNode;
import ish.oncourse.model.*;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentProcessController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.util.payment.PaymentProcessController.PaymentAction.ABANDON_PAYMENT;
import static ish.oncourse.util.payment.PaymentProcessController.PaymentAction.MAKE_PAYMENT;

public class PaymentEditorController implements PaymentEditorDelegate {

	private static final Logger logger = LogManager.getLogger();

    private PurchaseController purchaseController;
    private PaymentProcessController paymentProcessController;
    private Map<String, String> errors = new HashMap<>();


	public void init() {
		initPaymentProcessController();
	}

	@Override
	public College getCollege() {
		return purchaseController.getModel().getCollege();
	}

	public boolean isCorporatePass()
	{
		return purchaseController.getModel().getCorporatePass() != null;
	}

	public boolean isPaymentPlanZeroPayment() {
		return isZeroPayment() &&
				purchaseController.getModel().getVoucherPayments().isEmpty() &&
				(!purchaseController.getModel().getInvoice().getInvoiceLines().isEmpty() ||
				!purchaseController.getModel().getPaymentPlanInvoices().isEmpty());
	}

	@Override
	public boolean isEmptyInvoice() {
		return purchaseController.getModel().getInvoice().getInvoiceLines().isEmpty() && purchaseController.getModel().getPaymentPlanInvoices().isEmpty();
	}

	@Override
	public boolean isZeroPayment() {
		return getPaymentIn().isZeroPayment();
	}

	@Override
	public List<Application> getApplications() {
		return purchaseController.getModel().getAllEnabledApplications();
	}

	@Override
	public boolean isFinalState() {
		return  isPaymentPlanZeroPayment() || isCorporatePass() || (isZeroPayment() && isEmptyInvoice()) || paymentProcessController.isFinalState();
	}

	@Override
    public boolean isProcessFinished() {
        return  isPaymentPlanZeroPayment() || isCorporatePass() || (isZeroPayment() && isEmptyInvoice()) || paymentProcessController.isProcessFinished();
    }

    public boolean isPaymentSuccess() {
        return  isPaymentPlanZeroPayment() || isCorporatePass() || (isZeroPayment() && isEmptyInvoice()) || paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.SUCCESS;
    }

	public boolean isWrongPaymentExpressResult() {
		return paymentProcessController.isWrongPaymentExpressResult();
	}

    public void updatePaymentStatus() {
        paymentProcessController.processAction(PaymentProcessController.PaymentAction.UPDATE_PAYMENT_GATEWAY_STATUS);
        if (paymentProcessController.isFinalState()) {
            finalizeProcess();
        }
    }

    public void changePayer() {
        if (!getPaymentIn().getContact().getId().equals(purchaseController.getModel().getPayer().getId())) {

            PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.changePayer);
            actionParameter.setValue(getPaymentIn().getContact());
            purchaseController.performAction(actionParameter);
        }
    }

    @Override
    public void addPayer(Boolean isCompany) {
        PurchaseController.ActionParameter parameter; 
		
		if (isCompany) {
			parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addCompanyPayer);
		} else {
			parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addPersonPayer);
		}
		
        purchaseController.performAction(parameter);
    }

	@Override
	public Map<String,String> addCorporatePass(String corporatePass) {
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addCorporatePass);
		parameter.setValue(corporatePass);
		purchaseController.performAction(parameter);
		return purchaseController.getErrors();
	}

	public void makePayment() {
        purchaseController.setErrors(errors);
        if (errors.isEmpty()) {
            PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.makePayment);
            purchaseController.performAction(actionParameter);

            if (purchaseController.getErrors().isEmpty() &&
					!purchaseController.isPaymentResult()) {
				//we need refresh PaymentInModel before starting the process because VoucherPayments are recreated every enrol action
				initPaymentProcessController();
				paymentProcessController.processAction(PaymentProcessController.PaymentAction.INIT_PAYMENT);
				paymentProcessController.processAction(MAKE_PAYMENT);
            }
        }
    }

	private PaymentInModel getPaymentInModel() {
		PaymentInModel model = new PaymentInModel();
		model.setPaymentIn(purchaseController.getModel().getPayment());

		if (!purchaseController.getModel().getInvoice().getInvoiceLines().isEmpty()) {
			model.getInvoices().add(purchaseController.getModel().getInvoice());
		}
		for (InvoiceNode invoiceNode: purchaseController.getModel().getPaymentPlanInvoices()) {
			model.getInvoices().add(invoiceNode.getInvoice());
		}
		model.getEnrolments().addAll(purchaseController.getModel().getAllEnabledEnrolments());
		model.getVoucherPayments().addAll(purchaseController.getModel().getVoucherPayments());
		return model;
	}

	private void initPaymentProcessController()
	{
		paymentProcessController = PaymentProcessController.valueOf(purchaseController.getParallelExecutor(),
				purchaseController.getPaymentGatewayServiceBuilder().buildService(),
				purchaseController.getCayenneService(),
				purchaseController.getPaymentService(),
				getPaymentInModel(),
				new Runnable() {
					@Override
					public void run() {
						purchaseController.setConfirmationStatus(ConfirmationStatus.NOT_SENT);
						purchaseController.commitApplications();
						purchaseController.getModel().getObjectContext().commitChanges();
					}
				});
	}

    private void finalizeProcess() {
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.showPaymentResult);
        actionParameter.setErrors(errors);
        purchaseController.performAction(actionParameter);
    }

    public void tryAgain() {
        paymentProcessController.processAction(ABANDON_PAYMENT);
		purchaseController.setPaymentEditorController(null);
		purchaseController.cloneModel();
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.proceedToPayment);
        actionParameter.setValue(paymentProcessController.getPaymentIn());
        purchaseController.performAction(actionParameter);
    }

    public void abandon() {
        paymentProcessController.processAction(ABANDON_PAYMENT);
        finalizeProcess();
    }

    public boolean isNeedConcessionReminder() {
        return purchaseController.isNeedConcessionReminder();
    }

    @Override
    public boolean isEnrolmentFailedNoPlaces() {
        return false;
    }

    public List<Contact> getContacts() {
        return purchaseController.getModel().getContacts();
    }

    public PaymentIn getPaymentIn() {
        return purchaseController.getModel().getPayment();
    }

	@Override
	public Invoice getInvoice() {
		return purchaseController.getModel().getInvoice();
	}

	@Override
	public Money getTotalDiscountAmountIncTax() {
		return purchaseController.getTotalDiscountAmountIncTax();
	}
	

	public void setPurchaseController(PurchaseController purchaseController) {
        this.purchaseController = purchaseController;
    }

    public PaymentProcessController getPaymentProcessController() {
        return paymentProcessController;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

	private Tag getTagBy(Enrolment enrolment) {

		for (Tag tag : purchaseController.getTagService().getTagsForEntity(Course.class.getSimpleName(),
			enrolment.getCourseClass().getCourse().getId())) {
			if (Tag.SUBJECTS_TAG_NAME.equalsIgnoreCase(tag.getRoot().getName())) {
				return tag;
			}
		}
		return null;
	}

	@Override
	public boolean payerCanBeChanged() {
		return purchaseController.payerCanBeChanged();
	}
}
