package ish.oncourse.portal.services.payment

import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentInLine
import ish.oncourse.portal.services.IPortalService
import ish.oncourse.services.paymentexpress.IPaymentGatewayService
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.util.payment.CreditCardParser
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder
import org.apache.cayenne.ObjectContext

import static ish.common.types.PaymentSource.SOURCE_WEB
import static ish.common.types.PaymentStatus.IN_TRANSACTION
import static ish.common.types.PaymentType.CREDIT_CARD

/**
 * User: akoiro
 * Date: 1/07/2016
 */
class Controller implements IController {
    private IPaymentGatewayService paymentGatewayService
    private Contact contact
    private ObjectContext objectContext

    private Context context

    public Controller(ICayenneService cayenneService, IPortalService portalService, IPaymentGatewayService paymentGatewayService) {
        this(portalService.contact, cayenneService.newContext(), paymentGatewayService)
    }

    Controller(Contact contact, ObjectContext objectContext, IPaymentGatewayService paymentGatewayService) {
        this.contact = contact
        this.objectContext = objectContext
        this.paymentGatewayService = paymentGatewayService
    }

    public Response process(Request request) {
        context = Context.valueOf(contact, request);
        AProcess process = null

        switch (request.action) {
            case Action.init:
                process = new ProcessInit(request: request, context: context)
                break
            case Action.make:
                process = new ProcessMake(request: request, context: context,
                        createPaymentInClosure: createPaymentIn, processPaymentInClosure: processPaymentIn)
                break
            case Action.update:
                process = new ProcessUpdate(request: request, context: context)
                break
            default:
                throw new IllegalArgumentException()
        }
        return process.process()
    }

    def createPaymentIn = { Request request ->
        PaymentIn paymentIn = context.objectContext.newObject(PaymentIn.class).with {
            it.status = IN_TRANSACTION
            it.source = SOURCE_WEB
            it.type = CREDIT_CARD
            it.college = context.invoice.college
            it.contact = context.contact
            it.amount = Money.valueOf(BigDecimal.valueOf(request.card.amount))
            it.creditCardName = request.card.name
            it.creditCardNumber = request.card.number
            it.creditCardType = new CreditCardParser().parser(request.card.number)
            it.creditCardCVV = request.card.cvv
            it.creditCardExpiry = request.card.date
            return it
        }

        context.objectContext.newObject(PaymentInLine.class).with {
            it.invoice = context.invoice
            it.paymentIn = paymentIn
            it.college = context.invoice.college
            it.amount = paymentIn.amount;
        }
        context.objectContext.commitChanges()
        return paymentIn
    }

    def processPaymentIn = { PaymentIn paymentIn ->
        paymentGatewayService.performGatewayOperation(PaymentInModelFromPaymentInBuilder.valueOf(paymentIn).build().model)
        context.objectContext.commitChanges()
        return paymentIn
    }
}
