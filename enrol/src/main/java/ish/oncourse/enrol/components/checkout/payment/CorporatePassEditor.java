package ish.oncourse.enrol.components.checkout.payment;

import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.checkout.payment.PaymentEditorParser;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Map;

public class CorporatePassEditor implements IPaymentControlDelegate {

    @Parameter(required = true)
    @Property
    private PaymentEditorDelegate delegate;

	@Parameter
	@Property
	private Block blockToRefresh;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	@Property
	private String successMessage;

	@Property
	private String errorMessage;



	@SetupRender
	void beforeRender() {
		if (delegate.getInvoice().getCorporatePassUsed() != null)
		{
			successMessage = messages.get("message-corporatePassAdded");
		}
	}


	@OnEvent(value = "addCorporatePass")
	public Object addCorporatePass()
	{
		String password = StringUtils.trimToNull(request.getParameter("corporatePass"));
		if (password == null)
			errorMessage = messages.get("message-password");
		else
		{
			Map<String,String> errors = delegate.addCorporatePass(password);
			if (!errors.isEmpty())
				errorMessage = errors.values().iterator().next();
		}
		return blockToRefresh;
	}

    @Override
    public Object makePayment() {
		PaymentEditorParser parser = getPaymentEditorParser();
		parser.parse();
		delegate.setErrors(parser.getErrors());
		delegate.makePayment();
        return blockToRefresh;
    }


	private PaymentEditorParser getPaymentEditorParser() {
		PaymentEditorParser paymentEditorParser = new PaymentEditorParser();
		paymentEditorParser.setRequest(request);
		paymentEditorParser.setContacts(delegate.getContacts());
		paymentEditorParser.setMessages(messages);
		paymentEditorParser.setPaymentIn(delegate.getPaymentIn());
		return paymentEditorParser;
	}

}
