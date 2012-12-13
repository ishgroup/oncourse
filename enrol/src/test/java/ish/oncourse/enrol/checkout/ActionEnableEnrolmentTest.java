package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.services.EnrolTestModule;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import static org.junit.Assert.*;

public class ActionEnableEnrolmentTest extends ACheckoutTest {


    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.enrol.services", "enrol", EnrolTestModule.class);
        InputStream st = ActionEnableEnrolment.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/enrol/checkout/ActionEnableEnrolmentTest.xml");

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);

        DataSource refDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);


        this.cayenneService = getService(ICayenneService.class);
        this.purchaseControllerBuilder = getService(IPurchaseControllerBuilder.class);
    }

    @Test
    public void testDuplicateEnrolment() {

        ObjectContext context = cayenneService.newContext();
        CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, 1001);
        PurchaseModel model = createModel(context, Arrays.asList(courseClass), Collections.EMPTY_LIST, null);
        createPurchaseController(model);

        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001);
        addFirstContact(contact);

        //test duplicate enrolment
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertEquals(1, purchaseController.getModel().getDisabledEnrolments(contact).size());
        List<Enrolment> enrolments = purchaseController.getModel().getAllEnrolments(contact);
        for (Enrolment enrolment : enrolments) {
            assertTrue(enrolment.getObjectId().isTemporary());
        }

        contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1002);
        addContact(contact);
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertEquals(1, purchaseController.getModel().getDisabledEnrolments(contact).size());
        enrolments = purchaseController.getModel().getAllEnrolments(contact);
        for (Enrolment enrolment : enrolments) {
            assertDisabledEnrolment(enrolment);
        }


        ActionParameter actionParameter = new ActionParameter(PurchaseController.Action.proceedToPayment);
        actionParameter.setValue(purchaseController.getModel().getPayment());
        purchaseController.performAction(actionParameter);
        assertTrue("All enrolments are disabled", purchaseController.getErrors().containsKey(PurchaseController.Message.noEnabledItemForPurchase.name()));

        List<Contact> contacts = purchaseController.getModel().getContacts();
        for (Contact c : contacts) {
            assertDisabledEnrolments(c, 1);
        }
    }

    @Test
    public void testCourseClassEnded() {
        ObjectContext context = cayenneService.newContext();
        CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, 1002);
        PurchaseModel model = createModel(context, Arrays.asList(courseClass), Collections.EMPTY_LIST, null);
        createPurchaseController(model);

        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001);
        addFirstContact(contact);
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertDisabledEnrolments(contact, 1);
    }

    @Test
    public void testCourseClassHasNoPlaces() {
        ObjectContext context = cayenneService.newContext();
        //calss has only three places and one already used
        CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, 1003);
        PurchaseModel model = createModel(context, Arrays.asList(courseClass), Collections.EMPTY_LIST, null);
        createPurchaseController(model);

        //add first contact
        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001);
        addFirstContact(contact);
        assertNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getDisabledEnrolments(contact).size());
        assertEnabledEnrolments(contact, 1, false);
        assertEquals(1, purchaseController.getModel().getAllEnabledEnrolments().size());

        //add second contact
        contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1002);
        addContact(contact);
        assertNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getDisabledEnrolments(contact).size());
        assertEnabledEnrolments(contact, 1, false);
        assertEquals(2, purchaseController.getModel().getAllEnabledEnrolments().size());

        //add third contact
        contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1003);
        addContact(contact);
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertDisabledEnrolments(contact, 1);
        assertEquals(2, purchaseController.getModel().getAllEnabledEnrolments().size());
    }


    @Test
    public void testDeleteDisabledEnrolments() {
        testCourseClassHasNoPlaces();
        proceedToPayment();
        assertTrue(purchaseController.getErrors().isEmpty());
        assertTrue(purchaseController.isEditPayment());
        assertEquals(2, purchaseController.getModel().getAllEnabledEnrolments().size());

        Contact contact1 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001);
        assertEnabledEnrolments(contact1, 1, true);

        Contact contact2 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1002);
        assertEnabledEnrolments(contact2, 1, true);

        Contact contact3 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1003);
        assertEnabledEnrolments(contact3, 0, false);

        //emulate back button
        purchaseController.adjustState(PurchaseController.Action.enableEnrolment);
        assertEnabledEnrolments(contact1, 1, true);
        assertEnabledEnrolments(contact2, 1, true);
        assertDisabledEnrolments(contact3, 1);

    }
}
