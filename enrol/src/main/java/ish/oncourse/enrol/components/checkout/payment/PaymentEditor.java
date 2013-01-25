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

public class PaymentEditor {

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
	private Boolean userAgreed;

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
		userAgreed = false;
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
			payersModel = new ListSelectModel<Contact>(getPayers(), "fullName",propertyAccess);
		}
		return payersModel;
	}

	public  ListValueEncoder<Contact> getPayersEncoder()
	{
		if (payersEncoder == null)
		{
			payersEncoder =  new ListValueEncoder<Contact>(getPayers(), "id", propertyAccess);
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
			years = new ArrayList<Integer>();
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
		return Money.valueOf(getPaymentIn().getAmount());
	}

	public boolean isZeroPayment() {
		return Money.valueOf(delegate.getPaymentIn().getAmount()).isZero();
	}


	public String getSubmitButtonText() {
		return isZeroPayment() ? messages.get("submit.button.text.enrol") : messages.get("submit.button.text.payment");
	}


	public String getEnrolmentDisclosure() {
		 return StringUtils.trimToNull(preferenceController.getFeatureEnrolmentDisclosure());
	}

	public Format getMoneyFormat()
	{
		return FormatUtils.chooseMoneyFormat(Money.valueOf(delegate.getPaymentIn().getAmount()));
	}

    @OnEvent(value = "changePayerEvent")
    public Object changePayer()
    {
        if (!request.isXHR())
            return null;
        PaymentEditorParser paymentEditorParser = getPaymentEditorParser();
        paymentEditorParser.parse();
        delegate.changePayer();
        return blockToRefresh;
    }

	@OnEvent(component = "paymentSubmit", value = "selected")
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
            payers = new ArrayList<Contact>(delegate.getContacts());
            payers.add(getDummyPayer());
        }
        return payers;
    }
}
