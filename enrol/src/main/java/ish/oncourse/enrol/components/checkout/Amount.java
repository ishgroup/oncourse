package ish.oncourse.enrol.components.checkout;

import ish.math.Money;
import ish.oncourse.enrol.checkout.ActionChangePayNow;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.Voucher;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.MoneyFormatter;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;

public class Amount {
    private static final Logger logger = LogManager.getLogger();

    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_PAYNOW = "payNow";

    @Property
    @Parameter(required = true)
    private PurchaseController purchaseController;

    @Parameter(required = true)
    @Property
    private Block blockToRefresh;

    @Property
    @Parameter
    private boolean showPayerFields;

    @Inject
    private Request request;

    @Property
    private Voucher voucher;


    @OnEvent(value = "creditAccessEvent")
    public Object creditAccess() {
        if (!request.isXHR())
            return null;

        String password = StringUtils.trimToEmpty(request.getParameter(FIELD_PASSWORD));
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.creditAccess);
        actionParameter.setValue(password);
        purchaseController.performAction(actionParameter);
        return blockToRefresh;
    }

    @OnEvent(value = "payNowEvent")
    public Object payNow() {
        String value = StringUtils.trimToEmpty(request.getParameter(FIELD_PAYNOW));
        Money amount  = Money.ZERO;
        try {
            MoneyFormatter formatter = MoneyFormatter.getInstance();
            amount = (Money) formatter.stringToValue(value);
        } catch (Exception e) {
            logger.error("Wrong playNow value: {}", value, e);
        }
        ActionChangePayNow action = PurchaseController.Action.changePayNow.createAction(purchaseController);
        action.setPayNow(amount);
        purchaseController.performAction(PurchaseController.Action.changePayNow.createAction(purchaseController), PurchaseController.Action.changePayNow);
        return blockToRefresh;
    }

    @OnEvent(value = "removeOwingEvent")
    public Object removeOwing() {
        if (!request.isXHR())
            return null;

        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.owingApply);
        purchaseController.performAction(actionParameter);
        return blockToRefresh;
    }

    public Format moneyFormat(Money money) {
        return FormatUtils.chooseMoneyFormat(money);
    }

    public boolean showPrevOwing() {
        return purchaseController.hasPreviousOwing() && purchaseController.isApplyPrevOwing();
    }

    public URL getPortalUrl() throws MalformedURLException {
        return new URL(String.format("https://skillsoncourse.com.au/portal/login?firstName=%s&lastName=%s&emailAddress=%s",
                purchaseController.getModel().getPayer().getGivenName(),
                purchaseController.getModel().getPayer().getFamilyName(),
                purchaseController.getModel().getPayer().getEmailAddress()));
    }

    public boolean hasVoucers() {
        return !purchaseController.getModel().getVouchers().isEmpty();
    }
}
