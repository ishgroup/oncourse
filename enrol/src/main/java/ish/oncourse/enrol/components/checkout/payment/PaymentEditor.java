package ish.oncourse.enrol.components.checkout.payment;

import ish.common.types.CreditCardType;
import ish.math.Money;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.checkout.payment.PaymentEditorParser;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.ValidateHandler;
import ish.persistence.CommonPreferenceController;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static ish.oncourse.enrol.services.Constants.*;

public class PaymentEditor implements IPaymentControlDelegate {

	/**
	 * Credit card expire date interval
	 */
	private static final int EXPIRE_YEAR_INTERVAL = 15;

	@Parameter(required = true)
	@Property
	private PaymentEditorDelegate delegate;

	private ListValueEncoder<Contact> payersEncoder;

	private ISHEnumSelectModel cardTypeModel;

	private List<Integer> years;

	private ValidateHandler validateHandler;

	@Property
	private String expiryMonth;

	@Property
	private Integer expiryYear;


	@Inject
	private PropertyAccess propertyAccess;

	@Inject
	private Messages messages;

	@Inject
	private Request request;

	@Inject
	private PreferenceController preferenceController;

    @Parameter
    @Property
    private Block blockToRefresh;

    @Property
    private Contact payerValue;


    @SetupRender
	void beforeRender() {
        initExpiry();
    }

    private void initExpiry() {
        if (delegate.getPaymentIn().getCreditCardExpiry() != null)
        {
            String[] values =  delegate.getPaymentIn().getCreditCardExpiry().split("/");
            if (values.length == 2)
            {
                expiryMonth = values[0];
                if (StringUtils.isNumeric(values[1]))
                    expiryYear = Integer.valueOf(values[1]);
            }
        }
    }

    public ValidateHandler getValidateHandler()
	{
		if (validateHandler == null)
		{
			validateHandler = new ValidateHandler();
			validateHandler.setErrors(delegate.getErrors());
		}
		return validateHandler;
	}


	public  ListValueEncoder<Contact> getPayersEncoder()
	{
		if (payersEncoder == null)
		{
			payersEncoder =  new ListValueEncoder<>(getPayers(), "id", propertyAccess);
		}
		return payersEncoder;
	}

	public ISHEnumSelectModel getCardTypeModel()
	{
		if (cardTypeModel == null)
		{
			cardTypeModel = new ISHEnumSelectModel(CreditCardType.class, messages, CommonPreferenceController
					.getCCAvailableTypes(preferenceController).values().toArray(new CreditCardType[] {}));
		}
		return cardTypeModel;
	}


	public List<Integer> getYears()
	{
		if (years == null)
		{
			years = new ArrayList<>();
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);

			for (int i = 0; i < EXPIRE_YEAR_INTERVAL; i++) {
				years.add(currentYear + i);
			}
		}
		return years;
	}

	public PaymentIn getPaymentIn()
	{
		return delegate.getPaymentIn();
	}

	public Money getTotalPayment()
	{
		return getPaymentIn().getAmount();
	}

	public boolean isZeroPayment() {
		return delegate.getPaymentIn().isZeroPayment();
	}


	public Format getMoneyFormat()
	{
		return FormatUtils.chooseMoneyFormat(delegate.getPaymentIn().getAmount());
	}

	@OnEvent(value = EVENT_changePayerEvent)
	public Object changePayer(Long id) {
        if (!request.isXHR())
            return null;
		
		for (Contact contact : getPayers()) {
			if (contact.getId().equals(id)) {
				getPaymentIn().setContact(contact);
				delegate.changePayer();
				return blockToRefresh;
			}
		}
		throw new IllegalArgumentException();
    }

	@OnEvent(value = EVENT_addPersonPayerEvent)
	public Object addPersonPayer() {
		if (!request.isXHR())
			return null;
		delegate.addPayer(false);
		return blockToRefresh;
	}

	@OnEvent(value = EVENT_addCompanyPayerEvent)
	public Object addPCompanyPayer() {
		if (!request.isXHR())
			return null;
		delegate.addPayer(true);
		return blockToRefresh;
	}

	public Object makePayment()
	{
        PaymentEditorParser paymentEditorParser = getPaymentEditorParser();
		paymentEditorParser.parse();
		delegate.setErrors(paymentEditorParser.getErrors());
		delegate.makePayment();
		getValidateHandler().setErrors(delegate.getErrors());
		return this;
	}

    private PaymentEditorParser getPaymentEditorParser() {
        PaymentEditorParser paymentEditorParser = new PaymentEditorParser();
        paymentEditorParser.setRequest(request);
        paymentEditorParser.setContacts(delegate.getContacts());
        paymentEditorParser.setMessages(messages);
        paymentEditorParser.setPaymentIn(delegate.getPaymentIn());
		//when we parse payment page we should ignore entered corporate pass
		paymentEditorParser.setCorporatePass(false);
        return paymentEditorParser;
    }


    public boolean isSelected()
    {
        return payerValue.getId().equals(delegate.getPaymentIn().getContact().getId());
    }

    @Cached
    public List<Contact> getPayers()
    {
        return delegate.getContacts();
    }

	public boolean isAmexAvailable()
	{
		return preferenceController.getServicesAmexEnabled();
	}

}
