package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
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
    public void test() {

        ObjectContext context = cayenneService.newContext();
        CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, 1001);
        PurchaseModel model = createModel(context, Arrays.asList(courseClass), Collections.EMPTY_LIST, null);
        PurchaseController purchaseController = createPurchaseController(model);

        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001);
        addContactAction(purchaseController, contact);

        //test duplicate enrolment
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertEquals(1, purchaseController.getModel().getDisabledEnrolments(contact).size());
        List<Enrolment> enrolments = purchaseController.getModel().getAllEnrolments(contact);
        for (Enrolment enrolment : enrolments) {
            assertTrue(enrolment.getObjectId().isTemporary());
        }

        contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1002);
        addContact(purchaseController, contact);
        assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(contact, courseClass)));
        assertEquals(0, purchaseController.getModel().getEnabledEnrolments(contact).size());
        assertEquals(1, purchaseController.getModel().getDisabledEnrolments(contact).size());
        enrolments = purchaseController.getModel().getAllEnrolments(contact);
        for (Enrolment enrolment : enrolments) {
            assertTrue(enrolment.getObjectId().isTemporary());
            assertEquals(EnrolmentStatus.NEW, enrolment.getStatus());
        }


        ActionParameter actionParameter = new ActionParameter(PurchaseController.Action.proceedToPayment);
        actionParameter.setValue(purchaseController.getModel().getPayment());
        purchaseController.performAction(actionParameter);
        assertTrue("All enrolments are disabled", purchaseController.getErrors().containsKey(PurchaseController.Message.noEnabledItemForPurchase.name()));

        List<Contact> contacts = purchaseController.getModel().getContacts();
        for (Contact c : contacts) {
            enrolments = purchaseController.getModel().getAllEnrolments(c);
            for (Enrolment enrolment : enrolments) {
                assertTrue(enrolment.getObjectId().isTemporary());
                assertEquals(EnrolmentStatus.NEW, enrolment.getStatus());
            }
        }



    }
}
