package ish.oncourse.enrol.components.checkout;

import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Voucher;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.HTMLUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.Format;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.deselectVoucher;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.selectVoucher;
import static ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;

public class RedeemedVoucher {

    public static final String FIELD_selected = "selected";

    @Inject
    private Request request;

    @Property
    @Parameter (required = true)
    private Voucher voucher;

    @Parameter(required = true)
    private PurchaseController purchaseController;

    @Parameter(required = true)
    @Property
    private Block blockToRefresh;

    public Money getAmount()
    {
        PaymentIn paymentIn = purchaseController.getModel().getVoucherPaymentBy(voucher);

        return (paymentIn != null ? purchaseController.getModel().getVoucherPaymentBy(voucher).getAmount() : Money.ZERO);
    }

    public boolean isSelected()
    {
        return purchaseController.getModel().isSelectedVoucher(voucher);
    }

    public Format moneyFormat(Money money) {
        return FormatUtils.chooseMoneyFormat(money);
    }

    @OnEvent(value = "selectVoucherEvent")
    public Object selectVoucher(Long voucherId) {
        if (!request.isXHR())
            return null;

        boolean selected = HTMLUtils.parserBooleanValue(request.getParameter(FIELD_selected));
        ActionParameter parameter = selected ? new ActionParameter(selectVoucher): new ActionParameter(deselectVoucher);
        parameter.setValue(voucherId);
        purchaseController.performAction(parameter);

        return blockToRefresh;
    }
}
