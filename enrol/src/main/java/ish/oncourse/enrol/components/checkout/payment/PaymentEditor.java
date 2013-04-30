package ish.oncourse.enrol.components.checkout.payment;

import ish.common.types.CreditCardType;
import ish.math.Money;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.checkout.payment.PaymentEditorParser;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.ValidateHandler;
import ish.persistence.CommonPreferenceController;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PaymentEditor implements IPaymentControlDelegate {

	/**
	 * Credit card expire date interval
	 */
	private static final int EXPIRE_YEAR_INTERVAL = 15;

	@Parameter(required = true)
	@Property
	private PaymentEditorDelegate delegate;

	private ListSelectModel<Contact> payersModel;

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

    private Contact dummyPayer;
    private List<Contact> payers;


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

	public ListSelectModel<Contact> getPayersModel()
	{
		if (payersModel == null)
		{
			payersModel = new ListSelectModel<>(getPayers(), "fullName",propertyAccess);
		}
		return payersModel;
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

    @OnEvent(value = "changePayerEvent")
    public Object changePayer()
    {
        if (!request.isXHR())
            return null;
        PaymentEditorParser paymentEditorParser = getPaymentEditorParser();
        paymentEditorParser.parse();
        if (paymentEditorParser.isNewPayer())
            delegate.addPayer();
        else
            delegate.changePayer();
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
        return paymentEditorParser;
    }

    private Contact getDummyPayer()
    {
        if (dummyPayer == null)
        {
            dummyPayer = new Contact(){
                @Override
                public String getFullName() {
                    return messages.get("message-addDifferentPayer");
                }

                @Override
                public Long getId() {
                    return Long.MIN_VALUE;
                }
            };
        }
        return dummyPayer;
    }

    private List<Contact> getPayers()
    {
        if (payers == null)
        {
            payers = new ArrayList<>(delegate.getContacts());
            payers.add(getDummyPayer());
        }
        return payers;
    }

	public boolean isAmexAvailable()
	{
		return preferenceController.getServicesAmexEnabled();
	}

	/**
	 * 	The method is used as setter for value attribute of select payer combobox.
	 * 	we cannot use direct set PaymentIn.contact because sometime DUMMY contact can be passed to this
	 * 	method and our application throws exception in this case. Reason is unknow. It is workaround.
	 */
	public void setPayer(Contact contact)
	{

	}

	public Contact getPayer()
	{
		return delegate.getPaymentIn().getContact();
	}
}
