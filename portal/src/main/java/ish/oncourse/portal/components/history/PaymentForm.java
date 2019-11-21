package ish.oncourse.portal.components.history;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tapestry5.annotations.Property;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.payment.Controller;
import ish.oncourse.portal.services.payment.Request;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.TextStreamResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by akoiro on 20/05/2016.
 */
public class PaymentForm {
	private static final Logger LOGGER = LogManager.getLogger(PaymentForm.class);

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IPortalService portalService;

	@Inject
	private INewPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;

	@Inject
	private HttpServletRequest httpRequest;

	@Inject
	private org.apache.tapestry5.services.Request request;

	@Inject
	private PreferenceController preferenceController;

	private ObjectMapper mapper = new ObjectMapper();

	@Property
	private boolean isCreditCardPaymentEnabled;

	@SetupRender
	public void setupRender() {
		isCreditCardPaymentEnabled = preferenceController.isCreditCardPaymentEnabled();
	}


	@OnEvent(value = "process")
	public Object process() {
		if (!this.request.isXHR()) {
			throw new IllegalStateException();
		}
		try {
			String json = IOUtils.toString(httpRequest.getInputStream(), "UTF-8");
			Request request = mapper.readValue(json, Request.class);
			Controller controller = new Controller(cayenneService, portalService, paymentGatewayServiceBuilder.buildService());
			return new TextStreamResponse("text/json", mapper.writeValueAsString(controller.process(request)));
		} catch (Exception e) {
			LOGGER.catching(e);
			throw new IllegalArgumentException(e);
		}
	}
}

