package ish.oncourse.webservices.components;

import ish.common.types.CreditCardType;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.webservices.pages.Payment;
import ish.persistence.CommonPreferenceController;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.RadioGroup;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PaymentForm {
    /**
     * Credit card expire date interval
     */
    private static final int EXPIRE_YEAR_INTERVAL = 15;

    @Inject
    @Property
    private CommonPreferenceController preferenceService;


    @Inject
    private Messages messages;

    @Property
    private String cardNumberErrorMessage;

    @Property
    private String ccExpiryMonth;

    @Property
    private Integer ccExpiryYear;

    @Property
    private List<Integer> years;

    @InjectComponent
    @Property
    private Form paymentDetailsForm;

    @InjectComponent
    @Property
    private TextField cardName;

    @InjectComponent
    @Property
    private TextField cardNumber;

    @InjectComponent
    @Property
    private TextField cardcvv;

    @InjectComponent
    @Property
    private RadioGroup cardTypeField;

    @InjectComponent
    @Property
    private Select expiryMonth;

    @InjectComponent
    @Property
    private Select expiryYear;

    /**
     * Indicates if the submit was because of pressing the submit button.
     */
    private boolean isSubmitted;

    @Inject
    private Request request;

    @InjectPage
    private Payment paymentPage;


    @SetupRender
    public void beforeRender() {
        initYears();
    }

    public PaymentIn getPaymentIn()
    {
        return paymentPage.getPaymentProcessController().getPaymentIn();
    }

    private void initYears() {
        years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = 0; i < EXPIRE_YEAR_INTERVAL; i++) {
            years.add(currentYear + i);
        }
    }

    /**
     * Invokes when the {@link #paymentDetailsForm} is attempted to submit.
     */
    void validate() {

        if (!isSubmitted) {
            return;
        }

        if (!paymentPage.getPaymentProcessController().getPaymentIn().validateCCType()) {
            paymentDetailsForm.recordError(cardTypeField,
                    messages.get("cardTypeErrorMessage"));
        }
        if (!paymentPage.getPaymentProcessController().getPaymentIn().validateCCName()) {
            paymentDetailsForm.recordError(cardName,
                    messages.get("cardNameErrorMessage"));
        }

        cardNumberErrorMessage = paymentPage.getPaymentProcessController().getPaymentIn().validateCCNumber();
        if (cardNumberErrorMessage != null) {
            paymentDetailsForm.recordError(cardNumber, cardNumberErrorMessage);
        }
        //we also we need to check that CVV not empty here paymentPage.getCreditCardCVV() == null if we receive this requirement
        if (!paymentPage.getPaymentProcessController().getPaymentIn().validateCVV()) {
            paymentDetailsForm.recordError(cardcvv, messages.get("cardcvv"));
        }

        boolean hasErrorInDate = false;
        if (ccExpiryMonth == null || ccExpiryYear == null) {
            hasErrorInDate = true;
        } else {
            // don't check the format of ccExpiryMonth and ccExpiryYear
            // because
            // they are filled with dropdown lists with predefined
            // values
            paymentPage.getPaymentProcessController().getPaymentIn().setCreditCardExpiry(ccExpiryMonth + "/" + ccExpiryYear);
        }

        hasErrorInDate = !paymentPage.getPaymentProcessController().getPaymentIn().validateCCExpiry();

        if (hasErrorInDate) {
            paymentDetailsForm.recordError(expiryMonth,
                    messages.get("expiryDateError"));
            paymentDetailsForm.recordError(expiryYear,
                    messages.get("expiryDateError"));
        }
    }

    @OnEvent(component = "paymentSubmit", value = "selected")
    void submitButtonPressed() {
        isSubmitted = true;
    }

    @OnEvent(component = "paymentDetailsForm", value = "failure")
    Object submitFailed() {
        return this;
    }

    Object onException(Throwable cause) {
        return paymentPage.handleThrowable(cause);
    }

    @OnEvent(component = "paymentDetailsForm", value = "success")
    Object submitted() {
        paymentDetailsForm.clearErrors();
        synchronized (paymentPage.getPaymentProcessController()) {
            validate();
            if (!paymentDetailsForm.getHasErrors()) {
                if (isSubmitted) {
                    paymentPage.getPaymentProcessController().processAction(PaymentProcessController.PaymentAction.MAKE_PAYMENT);
                } else {
                    paymentPage.getPaymentProcessController().processAction(PaymentProcessController.PaymentAction.CANCEL_PAYMENT);
                }
                return paymentPage.getPageURL();
            }
        }
        return paymentPage.getPageURL();
    }

    public CreditCardType getMasterCard() {
        return CreditCardType.MASTERCARD;
    }

    public CreditCardType getVisa() {
        return CreditCardType.VISA;
    }

    public CreditCardType getAmex() {
        return CreditCardType.AMEX;
    }

    public String getCardTypeClass() {
        return getInputSectionClass(cardTypeField);
    }

    public String getCardNameInputClass() {
        return getInputSectionClass(cardName);
    }

    public String getCardNumberInputClass() {
        return getInputSectionClass(cardNumber);
    }

    public String getCardcvvInputClass() {
        return getInputSectionClass(cardcvv);
    }

    public String getCardExpireClass() {
        return getInputSectionClass(expiryMonth);
    }

    private String getInputSectionClass(Field field) {
        ValidationTracker defaultTracker = paymentDetailsForm.getDefaultTracker();
        return defaultTracker == null || !defaultTracker.inError(field) ? "pay-form-line" : "pay-form-line error";
    }
}
