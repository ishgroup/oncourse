package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.ActionAddGuardian;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.ValidationResult;
import ish.oncourse.enrol.pages.Checkout;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class NextButton {

    @InjectPage
    private Checkout checkout;

    @Inject
    private Block proceedToPayment, addGuardian;

    @Property
    private ValidationResult validationResult;

    @SetupRender
    public void setupRender()
    {
        validationResult = new ValidationResult();
        validationResult.setMessages(checkout.getPurchaseController().getMessages());
    }

    @OnEvent(value = "proceedToPaymentEvent")
    public Object proceedToPayment() {
        return checkout.proceedToPayment();
    }

    @OnEvent(value = "addGuardianEvent")
    public Object addGuardian() {
        ActionAddGuardian actionAddGuardian = new ActionAddGuardian();
        checkout.getPurchaseController().performAction(actionAddGuardian, PurchaseController.Action.addGuardian);
        return null;
    }

    public Block getCurrentBlock()
    {
        if (checkout.getPurchaseController().getModelValidator().validateContacts(validationResult))
        {
            return proceedToPayment;
        }
        else
        {
           return addGuardian;
        }
    }


}
