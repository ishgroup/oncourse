package ish.oncourse.enrol.utils;

import static org.junit.Assert.*;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import javax.sql.DataSource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.enrol.services.EnrolTestModule;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.test.ServiceTest;

public class PurchaseControllerTest extends ServiceTest {
	
	private IInvoiceProcessingService invoiceProcessingService;
	private IVoucherService voucherService;
	private IDiscountService discountService;
	private ICayenneService cayenneService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.enrol.services", "enrol", EnrolTestModule.class);
		InputStream st = EnrolCoursesControllerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/utils/enrolCoursesControllerDataSet.xml");

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		
		this.invoiceProcessingService = getService(IInvoiceProcessingService.class);
		this.voucherService = getService(IVoucherService.class);
		this.discountService = getService(IDiscountService.class);
		this.cayenneService = getService(ICayenneService.class);
	}
	
	private PurchaseController createPurchaseController(ObjectContext context) {
		College college = Cayenne.objectForPK(context, College.class, 1);
		Contact contact = Cayenne.objectForPK(context, Contact.class, 1189157);
		
		CourseClass cc1 = Cayenne.objectForPK(context, CourseClass.class, 1186958);
		CourseClass cc2 = Cayenne.objectForPK(context, CourseClass.class, 1186959);
		CourseClass cc3 = Cayenne.objectForPK(context, CourseClass.class, 1186960);
		
		return new PurchaseController(invoiceProcessingService, discountService, voucherService, context, college, contact, 
				Arrays.asList(cc1, cc2, cc3), Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}
	
	@Test
	public void testInit() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		
		PurchaseModel model = controller.getModel();
		
		assertNotNull(model);
		assertNotNull(model.getInvoice());
		assertNotNull(model.getPayment());
		assertEquals(1, model.getContacts().size());
		assertTrue(model.getContacts().contains(model.getPayer()));
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());
	}
	
	@Test
	public void testChangePayer() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();
		
		Contact originalPayer = model.getPayer();
		
		Contact newPayer = Cayenne.objectForPK(context, Contact.class, 1189158);
		
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(Action.CHANGE_PAYER);
		param.setValue(newPayer);
		
		controller.performAction(Action.CHANGE_PAYER, param);
		
		assertEquals(originalPayer, model.getPayer());
		
		param = new ActionParameter(Action.ADD_STUDENT);
		param .setValue(newPayer);
		
		controller.performAction(Action.ADD_STUDENT, param);
		
		param = new PurchaseController.ActionParameter(Action.CHANGE_PAYER);
		param.setValue(newPayer);
		
		controller.performAction(Action.CHANGE_PAYER, param);
		
		assertEquals(newPayer, model.getPayer());
	}
	
	@Test
	public void testAddStudent() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();
		
		Contact newContact = Cayenne.objectForPK(context, Contact.class, 1189158);
		
		ActionParameter param = new ActionParameter(Action.ADD_STUDENT);
		param .setValue(newContact);
		
		controller.performAction(Action.ADD_STUDENT, param);
		
		assertEquals(2, model.getContacts().size());
		assertTrue(model.getContacts().contains(newContact));
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(3, model.getEnabledEnrolments(newContact).size());
	}

}
