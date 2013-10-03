package ish.oncourse.enrol.checkout;

import ish.common.types.CreditCardType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.services.EnrolTestModule;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.model.*;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.InitialContextFactoryMock;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.ContextUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.State.paymentProgress;
import static ish.oncourse.enrol.checkout.PurchaseController.State.paymentResult;
import static org.junit.Assert.*;

public abstract class ACheckoutTest extends ServiceTest {

	protected ICayenneService cayenneService;
	IPurchaseControllerBuilder purchaseControllerBuilder;
    PurchaseController purchaseController;

	protected void setup(String dbResource) throws Exception {
		InitialContext context = new InitialContext();
		context.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);
		InitialContextFactoryMock.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);

		initTest("ish.oncourse.enrol", "enrol", EnrolTestModule.class);
		if (dbResource != null)
		{
			InputStream st = ACheckoutTest.class.getClassLoader().getResourceAsStream(
					dbResource);

			FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
			builder.setColumnSensing(true);
			FlatXmlDataSet dataSet = builder.build(st);

            ReplacementDataSet rDataSet;
            rDataSet = new ReplacementDataSet(dataSet);

            rDataSet.addReplacementObject("[null]", null);

            DataSource refDataSource = getDataSource("jdbc/oncourse");
			DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), rDataSet);
		}


		this.cayenneService = getService(ICayenneService.class);
		this.purchaseControllerBuilder = getService(IPurchaseControllerBuilder.class);

	}

	private void assertPurchaseController() {

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
							  List<Discount> discounts) {
		College college = Cayenne.objectForPK(context, College.class, 1);
		WebSite webSite = Cayenne.objectForPK(context, WebSite.class, 1);

		PurchaseModel model = new PurchaseModel();
		model.setObjectContext(context);
		model.setCollege(college);
		model.setClasses(classes);
		model.setProducts(products);
		model.setWebSite(webSite);
		model.setAllowToUsePrevOwing(true);
		for (Discount discount : discounts) {
			model.addDiscount(discount);
		}
		return model;
	}


	void createPurchaseController(PurchaseModel purchaseModel) {
		purchaseController = purchaseControllerBuilder.build(purchaseModel);
		purchaseController.performAction(new PurchaseController.ActionParameter(PurchaseController.Action.init));
		assertPurchaseController();
	}

	CourseClass createPurchaseController(long courseClassId)
	{
		ObjectContext context = cayenneService.newContext();
		CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, courseClassId);
		PurchaseModel model = createModel(context, Arrays.asList(courseClass), Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		createPurchaseController(model);
		return courseClass;

	}

	Contact addFirstContact(long contactId)
	{
		Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, contactId);
		addFirstContact(contact);
		return contact;
	}

	void addFirstContact(Contact contact) {
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
        purchaseController.performAction(actionParameter);

        actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		ContactCredentials contactCredentials = createContactCredentialsBy(contact);
		actionParameter.setValue(contactCredentials);
		purchaseController.performAction(actionParameter);
		assertEquals(PurchaseController.State.editCheckout, purchaseController.getState());
		assertTrue(purchaseController.getModel().getContacts().contains(contact));
	}


	void addContact(Contact newContact) {
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		performAction(param);

		param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		param.setValue(createContactCredentialsBy(newContact));

		performAction(param);
	}

	void proceedToPayment() {
        assertNull(purchaseController.getPaymentEditorDelegate());
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(PurchaseController.Action.proceedToPayment);
		param.setValue(purchaseController.getModel().getPayment());
		performAction(param);
	}

	void makeInvalidPayment() throws InterruptedException {
		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		delegate.getPaymentIn().setCreditCardCVV("1111");
		delegate.getPaymentIn().setCreditCardExpiry("12/2020");
		delegate.getPaymentIn().setCreditCardName("NAME NAME");
		delegate.getPaymentIn().setCreditCardNumber("9999990000000378");
		delegate.getPaymentIn().setCreditCardType(CreditCardType.VISA);
		delegate.makePayment();
		assertEquals(paymentProgress, purchaseController.getState());
        assertTrue(purchaseController.isPaymentProgress());
        assertFalse(purchaseController.isFinished());
        updatePaymentStatus();
    }

	void makeValidPayment() throws InterruptedException {
		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		delegate.getPaymentIn().setCreditCardCVV(TestPaymentGatewayService.VISA.getCvv());
		delegate.getPaymentIn().setCreditCardExpiry(TestPaymentGatewayService.VISA.getExpiry());
		delegate.getPaymentIn().setCreditCardName(TestPaymentGatewayService.VISA.getName());
		delegate.getPaymentIn().setCreditCardNumber(TestPaymentGatewayService.VISA.getNumber());
		delegate.getPaymentIn().setCreditCardType(TestPaymentGatewayService.VISA.getType());
		delegate.makePayment();
		assertEquals(paymentProgress, purchaseController.getState());
        assertTrue(purchaseController.isPaymentProgress());
        updatePaymentStatus();
	}

	void updatePaymentStatus() throws InterruptedException {
		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		delegate.updatePaymentStatus();
		Thread.sleep(20000);
		delegate.updatePaymentStatus();
		assertEquals(paymentResult, purchaseController.getState());
        assertTrue(purchaseController.isPaymentResult());
    }



	void performAction(PurchaseController.ActionParameter param) {
		purchaseController.performAction(param);
		assertFalse("State is valid", purchaseController.isIllegalState());
		assertFalse("Model is valid", purchaseController.isIllegalModel());
	}


	ContactCredentials createContactCredentialsBy(Contact contact) {
		return createContactCredentialsBy(contact.getGivenName(), contact.getFamilyName(), contact.getEmailAddress());
	}

	ContactCredentials createContactCredentialsBy(String firstName, String lastName, String email) {
		ContactCredentials contactCredentials = new ContactCredentials();
		contactCredentials.setFirstName(firstName);
		contactCredentials.setLastName(lastName);
		contactCredentials.setEmail(email);
		return contactCredentials;
	}


	void assertEnabledEnrolment(Enrolment enrolment) {
		for (InvoiceLine invoiceLine : enrolment.getInvoiceLines()) {
			assertNotNull(invoiceLine);
		}
		assertFalse("Enrolment should be linked with at least 1 invoiceline", enrolment.getInvoiceLines().isEmpty());
        assertEquals(EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
    }

    void assertDisabledEnrolment(Enrolment enrolment) {
        assertTrue(enrolment.getObjectId().isTemporary());
        assertTrue("Enrolment should not be linked with invoicelines", enrolment.getInvoiceLines().isEmpty());
        assertEquals(EnrolmentStatus.NEW, enrolment.getStatus());
    }

    void assertEnabledEnrolments(Contact contact, int count, boolean commited) {
        List<Enrolment> enrolments = purchaseController.getModel().getAllEnrolments(contact);
        assertEquals(count, enrolments.size());
        for (Enrolment enrolment : enrolments) {
            assertEquals(commited, enrolment.getObjectId().isTemporary());
            assertEnabledEnrolment(enrolment);
        }
    }

    void assertDisabledEnrolments(Contact contact, int count) {
        List<Enrolment> enrolments = purchaseController.getModel().getDisabledEnrolments(contact);
        assertEquals(count, enrolments.size());
        for (Enrolment enrolment : enrolments) {
            assertDisabledEnrolment(enrolment);
        }
    }

	void assertPayer(Contact expected) {
		assertEquals(expected.getId(),
				expected.getId());
		assertEquals(expected.getId(),
				expected.getId());
		assertEquals(expected.getId(),
				purchaseController.getModel().getPayer().getId());
	}


    protected PurchaseController init() {
        return init(true);
    }

    protected PurchaseController init(boolean addPayer) {
        return init(Arrays.asList(1186958L,1186959L,1186960L), Arrays.asList(7L),Arrays.asList(2L), addPayer);
    }

    protected PurchaseController init(List<Long> courseClassIds, List<Long> productIds,  List<Long> discountIds, boolean addPayer) {
        ObjectContext context = cayenneService.newContext();

        List<CourseClass> courseClasses = new ArrayList<>();
        for (Long id : courseClassIds)
            courseClasses.add(Cayenne.objectForPK(context, CourseClass.class, id));

        List<Product> products = new ArrayList<>();
        for (Long id : productIds)
            products.add(Cayenne.objectForPK(context, Product.class, id));

        List<Discount> discounts = new ArrayList<>();
        for (Long id : discountIds)
            discounts.add(Cayenne.objectForPK(context, Discount.class, id));


        PurchaseModel model = createModel(context,
                courseClasses,
                products,
                discounts);

        createPurchaseController(model);
        assertEquals(courseClasses.size(), model.getClasses().size());
        assertEquals(products.size(), model.getProducts().size());
        assertEquals(discounts.size(), model.getDiscounts().size());

        if (addPayer) {
            addFirstContact(1189157);
            assertEquals(1, purchaseController.getModel().getContacts().size());
            assertNotNull(purchaseController.getModel().getPayer());
        }

        return purchaseController;
    }

}
