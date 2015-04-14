package ish.oncourse.enrol.checkout.payment;

import ish.common.types.ConfirmationStatus;
import ish.math.Money;
import ish.oncourse.analytics.Item;
import ish.oncourse.analytics.Transaction;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.*;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.utils.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.enrol.utils.GenerateAnalyticsTransactionUtil.*;
import static ish.oncourse.util.payment.PaymentProcessController.PaymentAction.ABANDON_PAYMENT;
import static ish.oncourse.util.payment.PaymentProcessController.PaymentAction.MAKE_PAYMENT;

public class PaymentEditorController implements PaymentEditorDelegate {

	private static final Logger logger = LogManager.getLogger();

    private PurchaseController purchaseController;
    private PaymentProcessController paymentProcessController;
    private Map<String, String> errors = new HashMap<>();


	public void init()
	{
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

	@Override
	public boolean isEmptyInvoice() {
		return purchaseController.getModel().getInvoice().getInvoiceLines().isEmpty();
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
		return  isCorporatePass() || (isZeroPayment() && isEmptyInvoice()) || paymentProcessController.isFinalState();
	}

	@Override
    public boolean isProcessFinished() {
        return  isCorporatePass() || (isZeroPayment() && isEmptyInvoice()) || paymentProcessController.isProcessFinished();
    }

    public boolean isPaymentSuccess() {
        return  isCorporatePass() || (isZeroPayment() && isEmptyInvoice()) || paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.SUCCESS;
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
				if (paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.INIT)
					paymentProcessController.processAction(PaymentProcessController.PaymentAction.INIT_PAYMENT);
				paymentProcessController.processAction(MAKE_PAYMENT);
            }
        }
    }

	private void initPaymentProcessController()
	{
		paymentProcessController = new PaymentProcessController() {
			@Override
			protected void commitChanges() {
				purchaseController.setConfirmationStatus(ConfirmationStatus.NOT_SENT);
				purchaseController.commitApplications();
				purchaseController.getModel().getObjectContext().commitChanges();
				super.commitChanges();
			}
			
		};
		paymentProcessController.setStartWatcher(false);
		paymentProcessController.setObjectContext(purchaseController.getModel().getObjectContext());
		paymentProcessController.setPaymentIn(purchaseController.getModel().getPayment());
		paymentProcessController.setCayenneService(purchaseController.getCayenneService());
		paymentProcessController.setPaymentGatewayService(purchaseController.getPaymentGatewayServiceBuilder().buildService());
		paymentProcessController.setParallelExecutor(purchaseController.getParallelExecutor());
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
        initPaymentProcessController();
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
        return paymentProcessController.getPaymentIn();
    }

	@Override
	public Invoice getInvoice() {
		return purchaseController.getModel().getInvoice();
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

    @Override
    public Transaction getAnalyticsTransaction() {
		try {
			String googleAnalyticsAccount = purchaseController.getWebSiteService().getCurrentWebSite().getGoogleAnalyticsAccount();

			if (googleAnalyticsAccount != null && StringUtilities.cutToNull(googleAnalyticsAccount) != null) {
				if (isPaymentSuccess()) {
					List<Enrolment> enrolments = purchaseController.getModel().getAllEnabledEnrolments();
					List<Item> transactionItems = new ArrayList<>(enrolments.size());
					for (Enrolment enrolment : enrolments) {
						Tag tag = getTagBy(enrolment);
						CourseClass courseClass = enrolment.getCourseClass();
						Course course = courseClass.getCourse();
						Money unitPrice = Money.ZERO;
						for (InvoiceLine invoiceLine : enrolment.getInvoiceLines()) {
							unitPrice = unitPrice.add(invoiceLine.getDiscountedPriceTotalIncTax());
						}
						Item item = generateTransactionItem(
								getCategoryNameBy(tag == null ? StringUtils.EMPTY: getCategoryNameBy(tag.getDefaultPath())),
								course.getCode(),
								course.getName(),
								courseClass.getCode(),
								unitPrice.toBigDecimal());
						transactionItems.add(item);
					}
					BigDecimal tax = new BigDecimal(0);
					for (PaymentInLine pil : getPaymentIn().getPaymentInLines()) {
						for (InvoiceLine invoiceLine : pil.getInvoice().getInvoiceLines()) {
							tax = tax.add(invoiceLine.getTotalTax().toBigDecimal());
						}
					}
					return generateTransaction(getPaymentIn().getContact().getSuburb(), getPaymentIn().getContact().getState(),
							getPaymentIn().getId(), tax, getPaymentIn().getAmount().toBigDecimal(), transactionItems);
				}
			}
		} catch (Exception e) {
			logger.error("Cannot create Analytics Transaction.", e);
		}
		return null;
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
}
