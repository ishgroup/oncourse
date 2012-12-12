package ish.oncourse.enrol.checkout;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentSource;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.State.paymentProgress;
import static ish.oncourse.enrol.checkout.PurchaseController.State.paymentResult;
import static org.junit.Assert.*;

public abstract class ACheckoutTest extends ServiceTest {

	ICayenneService cayenneService;
	IPurchaseControllerBuilder purchaseControllerBuilder;

	private void assertPurchaseController(PurchaseController purchaseController) {

		assertNotNull(purchaseController.getModel());
		assertTrue(purchaseController.getModel().getContacts().isEmpty());
		assertNull(purchaseController.getModel().getPayer());


		assertNotNull(purchaseController.getModel().getInvoice());
		assertNotNull(purchaseController.getModel().getInvoice().getWebSite());
		assertNotNull(purchaseController.getModel().getInvoice().getCollege());
		assertEquals(PaymentSource.SOURCE_WEB, purchaseController.getModel().getInvoice().getSource());


		assertNotNull(purchaseController.getModel().getPayment());
		assertNotNull(purchaseController.getModel().getPayment().getCollege());
		assertEquals(PaymentSource.SOURCE_WEB, purchaseController.getModel().getPayment().getSource());
		assertNotNull(purchaseController.getVoucherRedemptionHelper().getInvoice());

		assertTrue(purchaseController.getModel().getAllEnabledEnrolments().isEmpty());

		assertEquals(PurchaseController.State.addContact, purchaseController.getState());
	}


	PurchaseModel createModel(ObjectContext context,
							  List<CourseClass> classes,
							  List<Product> products,
							  Discount discount) {
		College college = Cayenne.objectForPK(context, College.class, 1);
		WebSite webSite = Cayenne.objectForPK(context, WebSite.class, 1);

		PurchaseModel model = new PurchaseModel();
		model.setObjectContext(context);
		model.setCollege(college);
		model.setClasses(classes);
		model.setProducts(products);
		model.setWebSite(webSite);
		if (discount != null)
			model.addDiscount(discount);
		return model;
	}


	PurchaseController createPurchaseController(PurchaseModel purchaseModel) {
		PurchaseController purchaseController = purchaseControllerBuilder.build(purchaseModel);
		purchaseController.performAction(new PurchaseController.ActionParameter(PurchaseController.Action.init));
		assertPurchaseController(purchaseController);
		return purchaseController;
	}

	void addContactAction(PurchaseController purchaseController, Contact contact) {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		ContactCredentials contactCredentials = createContactCredentialsBy(contact);
		actionParameter.setValue(contactCredentials);
		purchaseController.performAction(actionParameter);
		assertEquals(PurchaseController.State.editCheckout, purchaseController.getState());
		assertTrue(purchaseController.getModel().getContacts().contains(contact));
	}


	void addContact(PurchaseController controller, Contact newContact) {
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(PurchaseController.Action.startAddContact);
		performAction(controller, param);

		param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		param.setValue(createContactCredentialsBy(newContact));

		performAction(controller, param);
	}

	void proceedToPayment(PurchaseController controller) {
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(PurchaseController.Action.proceedToPayment);
		param.setValue(controller.getModel().getPayment());
		performAction(controller, param);
	}

	void makeInvalidPayment(PurchaseController controller) throws InterruptedException {
		PaymentEditorDelegate delegate = controller.getPaymentEditorDelegate();
		delegate.getPaymentIn().setCreditCardCVV("1111");
		delegate.getPaymentIn().setCreditCardExpiry("12/2020");
		delegate.getPaymentIn().setCreditCardName("NAME NAME");
		delegate.getPaymentIn().setCreditCardNumber("9999990000000378");
		delegate.getPaymentIn().setCreditCardType(CreditCardType.VISA);
		delegate.makePayment();
		assertEquals(paymentProgress, controller.getState());
		updatePaymentStatus(controller);
	}

	void makeValidPayment(PurchaseController controller) throws InterruptedException {
		PaymentEditorDelegate delegate = controller.getPaymentEditorDelegate();
		delegate.getPaymentIn().setCreditCardCVV("1111");
		delegate.getPaymentIn().setCreditCardExpiry("12/2020");
		delegate.getPaymentIn().setCreditCardName("NAME NAME");
		delegate.getPaymentIn().setCreditCardNumber("9999990000000378");
		delegate.getPaymentIn().setCreditCardType(CreditCardType.MASTERCARD);
		delegate.makePayment();
		assertEquals(paymentProgress, controller.getState());
		updatePaymentStatus(controller);
	}

	void updatePaymentStatus(PurchaseController controller) throws InterruptedException {
		PaymentEditorDelegate delegate = controller.getPaymentEditorDelegate();
		delegate.updatePaymentStatus();
		Thread.sleep(20000);
		delegate.updatePaymentStatus();
		assertEquals(paymentResult, controller.getState());
	}



	void performAction(PurchaseController controller, PurchaseController.ActionParameter param) {
		controller.performAction(param);
		assertFalse("State is valid", controller.isIllegalState());
		assertFalse("Model is valid", controller.isIllegalModel());
	}


	ContactCredentials createContactCredentialsBy(Contact newPayer) {
		ContactCredentials contactCredentials = new ContactCredentials();
		contactCredentials.setFirstName(newPayer.getGivenName());
		contactCredentials.setLastName(newPayer.getFamilyName());
		contactCredentials.setEmail(newPayer.getEmailAddress());
		return contactCredentials;
	}
}
