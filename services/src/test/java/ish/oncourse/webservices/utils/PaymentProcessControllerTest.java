package ish.oncourse.webservices.utils;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Calendar;

import static ish.oncourse.webservices.utils.PaymentProcessController.PaymentAction.*;
import static ish.oncourse.webservices.utils.PaymentProcessController.PaymentProcessState.*;
import static org.junit.Assert.*;

public class PaymentProcessControllerTest extends ServiceTest {

    public static final String VALID_CARD_NUMBER = "5431111111111111";

    public static final String DECLINED_CARD_NUMBER = "9999990000000378";

    public static final String CARD_HOLDER_NAME = "john smith";

    private static final String VALID_EXPIRY_DATE_STR = Calendar.getInstance().get(Calendar.MONTH) + 1 + "/"
            + Calendar.getInstance().get(Calendar.YEAR);

    private ICayenneService cayenneService;
    private IPaymentGatewayService paymentGatewayService;
    private IPaymentService paymentService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.webservices.services", "services", ReplicationTestModule.class);

        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/utils/PaymentProcessControllerTest.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
        cayenneService = getService(ICayenneService.class);
        paymentGatewayService = getService("PaymentGatewayService", IPaymentGatewayService.class);
        paymentService = getService(IPaymentService.class);

    }

    @Test
    public void testMAKE_PAYMENT() {
        PaymentProcessController paymentProcessController = createPaymentProcessController();

        assertInvalidActionsForNO_PROCESSED(paymentProcessController);

        fillInvalidCard(paymentProcessController);

        paymentProcessController.processAction(MAKE_PAYMENT);
        assertEquals("paymentProcessController.getCurrentState()", PROCESSING_PAYMENT, paymentProcessController.getCurrentState());
        assertFalse("paymentProcessController.isIllegalState()", paymentProcessController.isIllegalState());

        assertInvalidActionsForPROCESSING_PAYMENT(paymentProcessController);

        paymentProcessController.processAction(UPDATE_PAYMENT_GATEWAY_STATUS);
        assertEquals("paymentProcessController.getCurrentState()", FAILED, paymentProcessController.getCurrentState());
        assertFalse("paymentProcessController.isIllegalState()", paymentProcessController.isIllegalState());
        assertEquals("paymentProcessController.getPaymentIn().getStatus()", PaymentStatus.FAILED_CARD_DECLINED, paymentProcessController.getPaymentIn().getStatus());

        assertInvalidActionsForFAILED(paymentProcessController);

        paymentProcessController.processAction(TRY_ANOTHER_CARD);
        assertEquals("paymentProcessController.getCurrentState()", NOT_PROCESSED, paymentProcessController.getCurrentState());
        assertFalse("paymentProcessController.isIllegalState()", paymentProcessController.isIllegalState());
        assertEquals("paymentProcessController.getPaymentIn().getStatus()", PaymentStatus.IN_TRANSACTION, paymentProcessController.getPaymentIn().getStatus());
        assertInvalidActionsForNO_PROCESSED(paymentProcessController);

        fillValidCard(paymentProcessController);
        paymentProcessController.processAction(MAKE_PAYMENT);
        paymentProcessController.processAction(UPDATE_PAYMENT_GATEWAY_STATUS);
        assertEquals("paymentProcessController.getCurrentState()", SUCCESS, paymentProcessController.getCurrentState());
        assertFalse("paymentProcessController.isIllegalState()", paymentProcessController.isIllegalState());
        assertEquals("paymentProcessController.getPaymentIn().getStatus()", PaymentStatus.SUCCESS, paymentProcessController.getPaymentIn().getStatus());
        assertInvalidAction(ABANDON_PAYMENT, SUCCESS, paymentProcessController);
        assertInvalidAction(ABANDON_PAYMENT_KEEP_INVOICE, SUCCESS, paymentProcessController);
        assertInvalidAction(TRY_ANOTHER_CARD, SUCCESS, paymentProcessController);
        assertInvalidAction(CANCEL_PAYMENT, SUCCESS, paymentProcessController);
        assertInvalidAction(MAKE_PAYMENT, SUCCESS, paymentProcessController);
        assertInvalidAction(UPDATE_PAYMENT_GATEWAY_STATUS, SUCCESS, paymentProcessController);
    }

    @Test
    public void testCANCEL_PAYMENT() {
        PaymentProcessController paymentProcessController = createPaymentProcessController();
        paymentProcessController.processAction(CANCEL_PAYMENT);
        assertEquals("paymentProcessController.getCurrentState()", CANCEL, paymentProcessController.getCurrentState());
        assertFalse("paymentProcessController.isIllegalState()", paymentProcessController.isIllegalState());
        assertEquals("paymentProcessController.getPaymentIn().getStatus()", PaymentStatus.FAILED, paymentProcessController.getPaymentIn().getStatus());
        assertInvalidActionsForCANCEL(paymentProcessController);
    }

    @Test
    public void testTRY_ANOTHER_CARD() {
        PaymentProcessController paymentProcessController = createPaymentProcessController();
        fillInvalidCard(paymentProcessController);
        paymentProcessController.processAction(MAKE_PAYMENT);
        paymentProcessController.processAction(UPDATE_PAYMENT_GATEWAY_STATUS);
        paymentProcessController.processAction(TRY_ANOTHER_CARD);
        assertEquals("paymentProcessController.getCurrentState()", NOT_PROCESSED, paymentProcessController.getCurrentState());
        assertFalse("paymentProcessController.isIllegalState()", paymentProcessController.isIllegalState());
        assertEquals("paymentProcessController.getPaymentIn().getStatus()", PaymentStatus.IN_TRANSACTION, paymentProcessController.getPaymentIn().getStatus());
        assertInvalidActionsForNO_PROCESSED(paymentProcessController);
    }

    @Test
    public void testABANDON_PAYMENT() {
        PaymentProcessController paymentProcessController = createPaymentProcessController();
        fillInvalidCard(paymentProcessController);
        paymentProcessController.processAction(MAKE_PAYMENT);
        paymentProcessController.processAction(UPDATE_PAYMENT_GATEWAY_STATUS);
        paymentProcessController.processAction(ABANDON_PAYMENT);
        assertEquals("paymentProcessController.getCurrentState()", CANCEL, paymentProcessController.getCurrentState());
        assertFalse("paymentProcessController.isIllegalState()", paymentProcessController.isIllegalState());
        assertEquals("paymentProcessController.getPaymentIn().getStatus()", PaymentStatus.FAILED_CARD_DECLINED, paymentProcessController.getPaymentIn().getStatus());
        assertInvalidActionsForCANCEL(paymentProcessController);
    }

    @Test
    public void testABANDON_PAYMENT_KEEP_INVOICE() {
        PaymentProcessController paymentProcessController = createPaymentProcessController();
        fillInvalidCard(paymentProcessController);
        paymentProcessController.processAction(MAKE_PAYMENT);
        paymentProcessController.processAction(UPDATE_PAYMENT_GATEWAY_STATUS);
        paymentProcessController.processAction(ABANDON_PAYMENT_KEEP_INVOICE);
        assertEquals("paymentProcessController.getCurrentState()", CANCEL, paymentProcessController.getCurrentState());
        assertFalse("paymentProcessController.isIllegalState()", paymentProcessController.isIllegalState());
        assertEquals("paymentProcessController.getPaymentIn().getStatus()", PaymentStatus.FAILED_CARD_DECLINED, paymentProcessController.getPaymentIn().getStatus());
        assertInvalidActionsForCANCEL(paymentProcessController);
    }

    private void assertInvalidActionsForCANCEL(PaymentProcessController paymentProcessController) {
        assertInvalidAction(ABANDON_PAYMENT, CANCEL, paymentProcessController);
        assertInvalidAction(ABANDON_PAYMENT_KEEP_INVOICE, CANCEL, paymentProcessController);
        assertInvalidAction(TRY_ANOTHER_CARD, CANCEL, paymentProcessController);
        assertInvalidAction(CANCEL_PAYMENT, CANCEL, paymentProcessController);
        assertInvalidAction(MAKE_PAYMENT, CANCEL, paymentProcessController);
        assertInvalidAction(UPDATE_PAYMENT_GATEWAY_STATUS, CANCEL, paymentProcessController);
    }

    private void assertInvalidActionsForFAILED(PaymentProcessController paymentProcessController) {
        assertInvalidAction(MAKE_PAYMENT, FAILED, paymentProcessController);
        assertInvalidAction(CANCEL_PAYMENT, FAILED, paymentProcessController);
        assertInvalidAction(UPDATE_PAYMENT_GATEWAY_STATUS, FAILED, paymentProcessController);
    }

    private void assertInvalidActionsForPROCESSING_PAYMENT(PaymentProcessController paymentProcessController) {
        assertInvalidAction(ABANDON_PAYMENT, PROCESSING_PAYMENT, paymentProcessController);
        assertInvalidAction(ABANDON_PAYMENT_KEEP_INVOICE, PROCESSING_PAYMENT, paymentProcessController);
        assertInvalidAction(TRY_ANOTHER_CARD, PROCESSING_PAYMENT, paymentProcessController);
        assertInvalidAction(CANCEL_PAYMENT, PROCESSING_PAYMENT, paymentProcessController);
    }

    private void fillValidCard(PaymentProcessController paymentProcessController) {
        paymentProcessController.getPaymentIn().setCreditCardName(CARD_HOLDER_NAME);
        paymentProcessController.getPaymentIn().setCreditCardCVV("1111");
        paymentProcessController.getPaymentIn().setCreditCardType(CreditCardType.VISA);
        paymentProcessController.getPaymentIn().setCreditCardNumber(VALID_CARD_NUMBER);
        paymentProcessController.getPaymentIn().setCreditCardExpiry(VALID_EXPIRY_DATE_STR);
    }


    private void fillInvalidCard(PaymentProcessController paymentProcessController) {
        paymentProcessController.getPaymentIn().setCreditCardName(CARD_HOLDER_NAME);
        paymentProcessController.getPaymentIn().setCreditCardCVV("1111");
        paymentProcessController.getPaymentIn().setCreditCardType(CreditCardType.VISA);
        paymentProcessController.getPaymentIn().setCreditCardNumber(DECLINED_CARD_NUMBER);
        paymentProcessController.getPaymentIn().setCreditCardExpiry(VALID_EXPIRY_DATE_STR);
    }

    private void   assertInvalidAction(PaymentProcessController.PaymentAction invalidAction,
                                       PaymentProcessController.PaymentProcessState currentState,
                                       PaymentProcessController paymentProcessController)
    {
        paymentProcessController.processAction(invalidAction);
        assertEquals("paymentProcessController.getCurrentState()", currentState, paymentProcessController.getCurrentState());
        assertTrue("paymentProcessController.isIllegalState()", paymentProcessController.isIllegalState());
    }

    private void assertInvalidActionsForNO_PROCESSED(PaymentProcessController paymentProcessController) {
        assertInvalidAction(TRY_ANOTHER_CARD, NOT_PROCESSED, paymentProcessController);
        assertInvalidAction(ABANDON_PAYMENT, NOT_PROCESSED, paymentProcessController);
        assertInvalidAction(ABANDON_PAYMENT_KEEP_INVOICE, NOT_PROCESSED, paymentProcessController);
        assertInvalidAction(UPDATE_PAYMENT_GATEWAY_STATUS, NOT_PROCESSED, paymentProcessController);
    }

    private PaymentProcessController createPaymentProcessController() {
        String sessionId = "SESSIONID";
        PaymentProcessController paymentProcessController = new PaymentProcessController();
        ObjectContext context = cayenneService.newContext();
        paymentProcessController.setObjectContext(context);
        paymentProcessController.setPaymentGatewayService(paymentGatewayService);
        paymentProcessController.setPaymentIn(paymentService.currentPaymentInBySessionId(sessionId));
        Assert.assertNotNull("paymentProcessController.getPaymentIn()", paymentProcessController.getPaymentIn());
        assertEquals("paymentProcessController.getCurrentState()", NOT_PROCESSED, paymentProcessController.getCurrentState());
        return paymentProcessController;
    }


}
