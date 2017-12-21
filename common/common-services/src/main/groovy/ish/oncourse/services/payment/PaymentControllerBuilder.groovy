package ish.oncourse.services.payment

import groovy.transform.CompileStatic
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder
import ish.oncourse.services.persistence.ICayenneService
import org.apache.tapestry5.ioc.Messages
import org.apache.tapestry5.services.Request

import static ish.oncourse.model.College.REQUESTING_COLLEGE_ATTRIBUTE

@CompileStatic
class PaymentControllerBuilder {

    private INewPaymentGatewayServiceBuilder builder
    private String sessionId
    private ICayenneService cayenneService
    private Messages messages
    private Request request

    PaymentControllerBuilder(String sessionId, INewPaymentGatewayServiceBuilder builder, ICayenneService cayenneService, Messages messages, Request request) {
        this.request = request
        this.messages = messages
        this.cayenneService = cayenneService
        this.sessionId = sessionId
        this.builder = builder
    }

    NewPaymentProcessController build() {
        ExtendedModel model = new ExtendedModelBuilder(sessionId: sessionId, context: cayenneService.sharedContext()).build()
        request.setAttribute(REQUESTING_COLLEGE_ATTRIBUTE, model.paymentIn?.college?.id)
        GetPaymentState.PaymentState state = new GetPaymentState(model: model, cayenneService: cayenneService).state
        NewPaymentProcessController controller = new NewPaymentProcessController(
                messages: messages,
                cayenneService: cayenneService,
                paymentGatewayServiceBuilder: builder)
        controller.model = model
        controller.state = state
        return controller
    }

}
