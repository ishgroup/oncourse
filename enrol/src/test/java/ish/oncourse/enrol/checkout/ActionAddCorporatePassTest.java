package ish.oncourse.enrol.checkout;

import org.junit.Before;
import org.junit.Test;

public class ActionAddCorporatePassTest extends ACheckoutTest {

    @Before
    public void setup() throws Exception {
        setup("ish/oncourse/enrol/checkout/ActionAddCorporatePassTest.xml");
    }

    @Test
    public void testAddCorporatePassWithSpecifiedValidClasses() {
        createPurchaseController(1001);
        addFirstContact(1001);
        proceedToPayment();

        selectCorporatePassEditor();
        selectCardEditor();

        selectCorporatePassEditor();
		addCorporatePass("password1");

        selectCardEditor();
        selectCorporatePassEditor();
		addCorporatePass("password1");

        makeCorporatePass();
	}
    
    @Test
    public void testAddCorporatePassWithoutSpecifiedValidClasses() {
    	createPurchaseController(1001);
        addFirstContact(1001);
        proceedToPayment();
        selectCorporatePassEditor();
		addCorporatePass("password4");//set the corporate pass without classes relationship

        makeCorporatePass();
    }
}
