package ish.oncourse.webservices.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.*;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.webservices.components.PaymentResult;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static ish.oncourse.webservices.pages.Payment.GetPaymentProperty.valueForContact;
import static ish.oncourse.webservices.pages.Payment.GetPaymentProperty.valueForMoney;

@Import(library = {"js/jquery.min.js", "js/jquery.inputmask.bundle.min.js", "js/payment.js"}, stylesheet = "css/screen.css")
public class Payment {
	private static final Logger logger = LogManager.getLogger();
	private static final int EXPIRE_YEAR_INTERVAL = 15;


	@Property
	private List<Integer> years;

	@Property
	private Integer year;

	@Inject
	private Messages messages;

	@Inject
	private Request request;

	@Property
	private Contact payer;

	@Property
	private Money totalIncGst;

	@Property
	private Format moneyFormat;

	@Property
	private Invoice invoice;

	@Property
	private List<Invoice> invoices;

	private NewPaymentProcessController paymentProcessController;

	@Inject
	private HttpServletRequest httpRequest;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private INewPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;

	@Property
	private String errorMessage;

	@InjectComponent
	private PaymentResult result;

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Finds and init payment and payment transaciton by referenceId.
	 *
	 * @param sessionId
	 */
	void onActivate(String sessionId) throws Exception {

		paymentProcessController = new PaymentControllerBuilder(sessionId, paymentGatewayServiceBuilder, cayenneService, messages, request).build();
		if (paymentProcessController.isError()) {
			errorMessage = messages.format("payment.has.finalstatus", sessionId);
			logger.error(errorMessage);
			return;
		} else if (paymentProcessController.readyToProcess()) {
			paymentProcessController.proceedToDetails();
		}
		initProperties();
	}

	private void initProperties() {
		this.moneyFormat = FormatUtils.getFeeFormatWithCents();
		this.totalIncGst =  valueForMoney(paymentProcessController.getState(), paymentProcessController.getModel()).get();
		this.payer = valueForContact(paymentProcessController.getState(), paymentProcessController.getModel()).get();
		this.invoices = paymentProcessController.getModel().getInvoices();
		this.result.setState(paymentProcessController.getState());
		initYears();
	}

	private void initYears() {
		years = new ArrayList<>();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);

		for (int i = 0; i < EXPIRE_YEAR_INTERVAL; i++) {
			years.add(currentYear + i);
		}
	}

	public String getPaymentTitle() {
		return messages.format("payment.title", payer.getFullName());
	}

	public String getInvoiceDescription() {
		return messages.format("invoice.desc", invoice.getInvoiceNumber());
	}

	public boolean isShowForm() {
		return paymentProcessController.fillCCDetails();
	}

	public boolean isShowProcessing() {
		return paymentProcessController.inProgress();
	}

	public boolean isShowResult() {
		return GetPaymentState.PaymentState.RESULT_STATES.contains(paymentProcessController.getState());
	}

	@OnEvent(value = "processAction")
	Object processAction() {
		if (!this.request.isXHR()) {
			throw new IllegalStateException();
		}
		try {
			PaymentResponse response = new PaymentResponse();
			String json = IOUtils.toString(httpRequest.getInputStream(), "UTF-8");
			PaymentRequest request = mapper.readValue(json, PaymentRequest.class);
			NewPaymentProcessController controller = new PaymentControllerBuilder(request.getSessionId(), paymentGatewayServiceBuilder, cayenneService, messages, this.request).build();
			controller.processRequest(request, response);
			return new TextStreamResponse("text/json", mapper.writeValueAsString(response));
		} catch (Exception e) {
			logger.catching(e);
			result.setState(GetPaymentState.PaymentState.ERROR);
			return result;
		}
	}

	@OnEvent(value = "getResultBlock")
	Object getResultBlock(String sessionId) {
		NewPaymentProcessController controller = new PaymentControllerBuilder(sessionId, paymentGatewayServiceBuilder, cayenneService, messages, this.request).build();
		result.setState(controller.getState());
		return result;
	}

	static abstract class GetPaymentProperty<T> {
		private GetPaymentState.PaymentState state;
		private ExtendedModel model;

		private GetPaymentProperty(GetPaymentState.PaymentState state, ExtendedModel model) {
			this.state = state;
			this.model = model;
		}

		protected abstract T getProperty(PaymentIn it);

		public T get() {
			switch (state) {
				case READY_TO_PROCESS:
				case FILL_CC_DETAILS:
				case CHOOSE_ABANDON_OTHER:
				case DPS_PROCESSING:
				case DPS_ERROR:
				case WARNING:
				case ERROR:
					return getProperty(model.getPaymentIn());
				case FAILED:
					return getProperty(model.getFailedPayments().get(0));
				case SUCCESS:
					return getProperty(model.getSuccessPayment());
				default:
					throw new IllegalArgumentException();
			}
		}

		static GetPaymentProperty<Money> valueForMoney(GetPaymentState.PaymentState state, ExtendedModel model) {
			return new GetPaymentProperty<Money>(state, model) {
				@Override
				protected Money getProperty(PaymentIn it) {
					return it.getAmount();
				}
			};
		}

		static GetPaymentProperty<Contact> valueForContact(GetPaymentState.PaymentState state, ExtendedModel model) {
			return new GetPaymentProperty<Contact>(state, model) {
				@Override
				protected Contact getProperty(PaymentIn it) {
					return it.getContact();
				}
			};
		}

	}
	
	
}
