package ish.oncourse.enrol.checkout;

import ish.common.types.CreditCardType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentType;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.model.InvoiceNode;
import ish.oncourse.enrol.checkout.model.PurchaseModel;
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
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.*;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.selectCardEditor;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.selectCorporatePassEditor;
import static ish.oncourse.enrol.checkout.PurchaseController.State.paymentProgress;
import static ish.oncourse.enrol.checkout.PurchaseController.State.paymentResult;
import static org.junit.Assert.*;

public abstract class ACheckoutTest extends ServiceTest {

	protected ICayenneService cayenneService;
	IPurchaseControllerBuilder purchaseControllerBuilder;
    protected PurchaseController purchaseController;

	protected void setup(String dbResource) throws Exception {
		InitialContext context = new InitialContext();
		context.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);
		InitialContextFactoryMock.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);
		InitialContextFactoryMock.bind(ContextUtil.QUERY_CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);

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


            configDataSet(rDataSet);

            DataSource refDataSource = getDataSource("jdbc/oncourse");
			DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), rDataSet);
		}


		this.cayenneService = getService(ICayenneService.class);
		this.purchaseControllerBuilder = getService(IPurchaseControllerBuilder.class);

	}

    protected void configDataSet(ReplacementDataSet dataSet)
    {
        dataSet.addReplacementObject("[null]", null);
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
		purchaseController.performAction(new PurchaseController.ActionParameter(Action.init));
		assertPurchaseController();
	}

	protected List<CourseClass> createPurchaseController(long... courseClassId)
	{
		ObjectContext context = cayenneService.newContext();
		ArrayList<CourseClass> courseClasses = new ArrayList<>();
		for (long id : courseClassId) {
			CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, id);
			courseClasses.add(courseClass);
		}
		PurchaseModel model = createModel(context, courseClasses, new ArrayList<Product>(), new ArrayList<Discount>());
		createPurchaseController(model);
		return courseClasses;

	}

	protected CourseClass createPurchaseController(long courseClassId)
	{
		return createPurchaseController(new long[]{courseClassId}).get(0);
	}

	protected Contact addFirstContact(long contactId)
	{
		Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, contactId);
		addFirstContact(contact);
		return contact;
	}

	void addFirstContact(Contact contact) {
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(Action.addContact);
        purchaseController.performAction(actionParameter);

        actionParameter = new PurchaseController.ActionParameter(Action.addContact);
		ContactCredentials contactCredentials = createContactCredentialsBy(contact);
		actionParameter.setValue(contactCredentials);
		purchaseController.performAction(actionParameter);
		assertEquals(PurchaseController.State.editCheckout, purchaseController.getState());
		assertTrue(purchaseController.getModel().getContacts().contains(contact));
	}


	void addContact(Contact newContact) {
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(Action.addContact);
		performAction(param);

		param = new PurchaseController.ActionParameter(Action.addContact);
		param.setValue(createContactCredentialsBy(newContact));

		performAction(param);
	}


    void addContact(long contactId) {

        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(),Contact.class, contactId);
        addContact(contact);
    }

    Contact addPayer(long contactId) {
        Contact newPayer = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, contactId);
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(Action.addPersonPayer);
        performAction(actionParameter);

        ContactCredentials credential =  purchaseController.getAddContactDelegate().getContactCredentials();
        credential.setEmail(newPayer.getEmailAddress());
        credential.setFirstName(newPayer.getGivenName());
        credential.setLastName(newPayer.getFamilyName());

        purchaseController.getAddContactDelegate().addContact();
        return newPayer;
    }


    protected void proceedToPayment() {
        assertNull(purchaseController.getPaymentEditorDelegate());
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(Action.proceedToPayment);
		param.setValue(purchaseController.getModel().getPayment());
		performAction(param);
	}
	
	

	protected void makeInvalidPayment() throws InterruptedException {
		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		delegate.getPaymentRequest().setCvv("1111");
		delegate.getPaymentRequest().setMonth("12");
		delegate.getPaymentRequest().setYear("2020");
		delegate.getPaymentRequest().setName("NAME NAME");
		delegate.getPaymentRequest().setNumber("9999990000000378");
		delegate.getPaymentIn().setCreditCardType(CreditCardType.VISA);
		delegate.makePayment();
		assertEquals(paymentProgress, purchaseController.getState());
        assertTrue(purchaseController.isPaymentProgress());
        assertFalse(purchaseController.isFinished());
        updatePaymentStatus();
		checkSortOrder();
    }

	protected void makeValidPayment() throws InterruptedException {
		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		delegate.getPaymentRequest().setCvv(TestPaymentGatewayService.VISA.getCvv());
		delegate.getPaymentRequest().setMonth(TestPaymentGatewayService.VISA.getExpiry().split("/")[0]);
		delegate.getPaymentRequest().setYear(TestPaymentGatewayService.VISA.getExpiry().split("/")[1]);
		delegate.getPaymentRequest().setName(TestPaymentGatewayService.VISA.getName());
		delegate.getPaymentRequest().setNumber(TestPaymentGatewayService.VISA.getNumber());
		delegate.getPaymentIn().setCreditCardType(TestPaymentGatewayService.VISA.getType());
		delegate.makePayment();
		assertEquals(paymentProgress, purchaseController.getState());
        assertTrue(purchaseController.isPaymentProgress());
        updatePaymentStatus();
		checkSortOrder();
	}

	void updatePaymentStatus() throws InterruptedException {
		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		delegate.updatePaymentStatus();
		Thread.sleep(20000);
		delegate.updatePaymentStatus();
		assertEquals(paymentResult, purchaseController.getState());
        assertTrue(purchaseController.isPaymentResult());
    }

	protected void performAction(APurchaseAction purchaseAction, Action action) {
		purchaseController.performAction(purchaseAction, action);
		assertFalse("State is illegal", purchaseController.isIllegalState());
		assertFalse("Model is invalid", purchaseController.isIllegalModel());
	}


	protected void performAction(PurchaseController.ActionParameter param) {
		purchaseController.performAction(param);
		assertFalse("State is illegal", purchaseController.isIllegalState());
		assertFalse("Model is invalid", purchaseController.isIllegalModel());
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
		AssertEnabledEnrolment.valueOf(enrolment).assertValue();
    }

    protected void assertDisabledEnrolment(Enrolment enrolment) {
        assertTrue(enrolment.getObjectId().isTemporary());
        assertTrue("Enrolment should not be linked with invoicelines", enrolment.getInvoiceLines().isEmpty());
        assertEquals(EnrolmentStatus.NEW, enrolment.getStatus());
    }

    protected void assertEnabledEnrolments(Contact contact, int count) {
	    AssertEnabledEnrolments.valueOf(contact, count, purchaseController);
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
        return init(Arrays.asList(1186958L,1186959L,1186960L), Arrays.asList(7L), Arrays.asList(2L), addPayer);
    }

    protected PurchaseController init(List<Long> courseClassIds, List<Long> productIds,  List<Long> discountIds, boolean addPayer) {
        ObjectContext context = cayenneService.newContext();

        List<CourseClass> courseClasses = new ArrayList<>();
        for (Long id : courseClassIds)
            courseClasses.add(Cayenne.objectForPK(context, CourseClass.class, id));

        List<Product> products = new ArrayList<>();
        for (Long id : productIds)
            products.add(SelectById.query(Product.class, id).selectOne(context));

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


    public PurchaseController addCode(String code)
    {
        PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(Action.addCode);
        param.setValue(code);
        performAction(param);
        return purchaseController;
    }

	protected void assertQueuedRecord(List<QueuedRecord> records, Queueable entity) {
		for (QueuedRecord queuedRecord : records) {
			if (queuedRecord.getLinkedRecord().getObjectId().equals(entity.getObjectId())) {
				assertTrue(Boolean.TRUE);
				return;
			}
		}
		assertTrue(String.format("QueuedRecord for %s does not exist", entity), Boolean.FALSE);
	}

	protected void assertQueuedRecords(Queueable... queueables) {
		List<QueuedRecord> records = ObjectSelect.query(QueuedRecord.class).orderBy(QueuedRecord.ENTITY_IDENTIFIER.asc()).select(getModel().getObjectContext());
		assertEquals(queueables.length, records.size());
		for (Queueable queueable : queueables) {
			assertQueuedRecord(records, queueable);
		}
	}

	protected void selectCorporatePassEditor() {
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(selectCorporatePassEditor);
		performAction(parameter);

		assertEquals(PaymentType.INTERNAL, purchaseController.getModel().getPayment().getType());
		assertTrue(purchaseController.getModel().getPayment().isZeroPayment());
		assertTrue(purchaseController.isEditCorporatePass());
	}

	protected void addCorporatePass(String corporatePass) {
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(Action.addCorporatePass);
		parameter.setValue(corporatePass);
		purchaseController.performAction(parameter);
		assertNotNull(purchaseController.getModel().getInvoice().getCorporatePassUsed());

		List<InvoiceNode> invoiceNodes = purchaseController.getModel().getPaymentPlanInvoices();
		for (InvoiceNode invoiceNode : invoiceNodes) {
			assertNotNull(invoiceNode.getInvoice().getCorporatePassUsed());
		}

		assertPayer(purchaseController.getModel().getCorporatePass().getContact());
	}

	protected void selectCardEditor() {
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(selectCardEditor);
		performAction(parameter);
		assertEquals(PaymentType.CREDIT_CARD, purchaseController.getModel().getPayment().getType());
		assertFalse(purchaseController.getModel().getPayment().isZeroPayment());
		assertTrue(purchaseController.isEditPayment());
		assertNull(purchaseController.getModel().getInvoice().getCorporatePassUsed());

		Contact contact = purchaseController.getModel().getContacts().get(0);
		assertPayer(contact);
	}

	protected void makeCorporatePass() {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(Action.makePayment);
		purchaseController.performAction(actionParameter);

		assertTrue(purchaseController.getPaymentEditorDelegate().isCorporatePass());
		assertEquals(0, ObjectSelect.query(PaymentIn.class).select(purchaseController.getModel().getObjectContext()).size());

		List<Enrolment> enrolments = purchaseController.getModel().getAllEnabledEnrolments();
		for (Enrolment enrolment : enrolments) {
			assertEquals(EnrolmentStatus.SUCCESS, enrolment.getStatus());
			assertEquals(PersistenceState.COMMITTED , enrolment.getPersistenceState());
		}
	}


	protected PurchaseModel getModel() {
		return purchaseController.getModel();
	}

	protected Set<Queueable> getInvoiceTransaction(Invoice rInvoice) {
		Set<Queueable> queueables = new HashSet<>();
		queueables.add(rInvoice);
		queueables.addAll(rInvoice.getInvoiceDueDates());
		queueables.addAll(rInvoice.getRefundedInvoices());
		queueables.addAll(rInvoice.getPaymentInLines());
		queueables.add(rInvoice.getContact());
		for (PaymentInLine line: rInvoice.getPaymentInLines()) {
			queueables.add(line.getPaymentIn());
			queueables.add(line.getPaymentIn().getContact());
		}

		queueables.addAll(rInvoice.getInvoiceLines());
		for (InvoiceLine line: rInvoice.getInvoiceLines()) {
			if (line.getEnrolment() != null) {
				queueables.add(line.getEnrolment());
				queueables.add(line.getEnrolment().getStudent());
				queueables.add(line.getEnrolment().getStudent().getContact());
			}
		}

		for (Invoice invoice: rInvoice.getRefundedInvoices()) {
			queueables.addAll(invoice.getInvoiceLines());
			queueables.addAll(invoice.getPaymentInLines());
			queueables.addAll(invoice.getInvoiceLines());
			queueables.addAll(invoice.getPaymentInLines());
			queueables.add(invoice.getPaymentInLines().get(0).getPaymentIn());
		}
		return queueables;
	}

	protected void checkSortOrder() {
		checkSortOrder(getModel().getInvoice());
		for (InvoiceNode invoice : getModel().getPaymentPlanInvoices()) {
			checkSortOrder(invoice.getInvoice());
		}
	}
	
	
	protected void checkSortOrder(Invoice invoice) {
		if (invoice != null) {
			for (int i = 0; i < invoice.getInvoiceLines().size(); i++) {
				InvoiceLine invoiceLine = invoice.getInvoiceLines().get(i);
				assertEquals(i, invoiceLine.getSortOrder().intValue());
			}
		}
	}

	protected Enrolment disableEnrolment(Enrolment enrolmentToDisable) {
		ActionDisableEnrolment actionDisableEnrolment = Action.disableEnrolment.createAction(purchaseController);
		actionDisableEnrolment.setEnrolment(enrolmentToDisable);
		performAction(actionDisableEnrolment, Action.disableEnrolment);
		return actionDisableEnrolment.getEnrolment();
	}

	protected Enrolment enableEnrolment(Enrolment enrolment) {
		ActionEnableEnrolment action = Action.enableEnrolment.createAction(purchaseController);
		action.setEnrolment(enrolment);
		performAction(action, Action.enableEnrolment);
		return action.getEnrolment();
	}


}
