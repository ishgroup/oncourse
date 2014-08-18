package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.ValidationResult;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class Progress {

    private static final String CLASS_disable = "disable";
    private static final String CLASS_active = "active";
    private static final String URL_current = "#";
    private static final String URL_checkout = "/enrol/checkout";

    @Parameter
    private PurchaseController purchaseController;

    @Property
    private String detailsClass;

    @Property
    private String detailsUrl;

    @Property
    private String checkoutClass;

    @Property
    private String checkoutUrl;

    @Property
    private String paymentClass;

    @Property
    private String paymentUrl;


    @Property
    private String parentClass;

    @Property
    private String parentUrl;

    @Property
    private boolean showParent;

    @SetupRender
    void beforeRender() {
        showParent = false;

        if (purchaseController == null)
        {
            detailsClass = CLASS_disable;
            checkoutClass = CLASS_disable;
            paymentClass = CLASS_disable;
            parentClass = CLASS_disable;

            detailsUrl = URL_current;
            checkoutUrl = URL_current;
            paymentUrl = URL_current;
            parentUrl = URL_current;
            return;
        }

        switch (purchaseController.getState()) {

            case init:
            case paymentProgress:
            case paymentResult:
                detailsClass = CLASS_disable;
                checkoutClass = CLASS_disable;
                paymentClass = CLASS_disable;
                parentClass = CLASS_disable;

                detailsUrl = URL_current;
                checkoutUrl = URL_current;
                paymentUrl = URL_current;
                parentClass = URL_current;
                break;
            case editCheckout:
                detailsClass = CLASS_disable;
                checkoutClass = CLASS_active;
                paymentClass = CLASS_disable;
                parentClass = CLASS_disable;

                detailsUrl = URL_current;
                checkoutUrl = URL_current;
                paymentUrl = URL_current;
                parentClass = URL_current;
                showParent = showParent();
                break;
            case addContact:
            case editContact:
            case editConcession:
                detailsClass = CLASS_active;
                parentClass = CLASS_disable;
                checkoutClass = CLASS_disable;
                paymentClass = CLASS_disable;

                detailsUrl = URL_current;
                checkoutUrl = URL_current;
                paymentUrl = URL_current;
                parentUrl = URL_current;
                break;
            case editPayment:
            case editCorporatePass:
                detailsClass = CLASS_disable;
                checkoutClass = FormatUtils.EMPTY_STRING;
                paymentClass = CLASS_active;
                parentClass = CLASS_disable;

                detailsUrl = URL_current;
                checkoutUrl = URL_checkout;
                paymentUrl = URL_current;
                parentUrl = URL_checkout;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private boolean showParent()
    {
        if (purchaseController == null)
            return false;
        ValidationResult validationResult = new ValidationResult();
        validationResult.setMessages(purchaseController.getMessages());
        return !purchaseController.getModelValidator().validateContacts(validationResult);
    }


}
