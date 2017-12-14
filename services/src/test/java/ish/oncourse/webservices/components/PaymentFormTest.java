package ish.oncourse.webservices.components;

import ish.oncourse.services.ServiceModule;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.services.AppModule;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.test.PageTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class PaymentFormTest extends ServiceTest{

    private static final String APP_PACKAGE = "ish.oncourse.webservices";
    private static final String APP_NAME = "";
    private static final String CONTEXT_NAME = "src/main/webapp";
    private static final String URL = "Payment/999999999";
    private static final int DIV_FOR_CARD_NUMBER_INPUT = 2;


    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream st = PaymentFormTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/webservices/components/paymentFormDataSet.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
        DataSource refDataSource = getDataSource();
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
    }


    @Test
	@Ignore
    public void testCreditCardNumberWithoutSpaces() {
        PageTester tester = new PageTester(APP_PACKAGE, APP_NAME, CONTEXT_NAME, AppModule.class);

        Document doc = tester.renderPage(URL);
        Element paymentDetailsForm = doc.getElementById("paymentDetailsForm");
        Element paymentSubmit = paymentDetailsForm.getElementById("paymentSubmit");

        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put("cardnumber", "5105 1051 0510 5100");

        tester.clickSubmitAndReturnResponse(paymentSubmit, fieldValues);
        doc = tester.renderPage(URL);
        paymentDetailsForm = doc.getElementById("paymentDetailsForm");
        assertNotNull(paymentDetailsForm);

        String cardNumberFormDivString = paymentDetailsForm.getChildren().get(DIV_FOR_CARD_NUMBER_INPUT).toString();
        assertTrue(cardNumberFormDivString.contains("<span></span>"));

        tester.shutdown();
    }

    @Test
	@Ignore
    public void testCreditCardNumberWithSpaces() {
        PageTester tester = new PageTester(APP_PACKAGE, APP_NAME, CONTEXT_NAME, AppModule.class);

        Document doc = tester.renderPage(URL);
        Element paymentDetailsForm = doc.getElementById("paymentDetailsForm");
        Element paymentSubmit = paymentDetailsForm.getElementById("paymentSubmit");

        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put("cardnumber", "5105105105105100");

        tester.clickSubmitAndReturnResponse(paymentSubmit, fieldValues);
        doc = tester.renderPage(URL);
        paymentDetailsForm = doc.getElementById("paymentDetailsForm");
        assertNotNull(paymentDetailsForm);

        String cardNumberFormDivString = paymentDetailsForm.getChildren().get(DIV_FOR_CARD_NUMBER_INPUT).toString();
        assertTrue(cardNumberFormDivString.contains("<span></span>"));

        tester.shutdown();
    }
}
