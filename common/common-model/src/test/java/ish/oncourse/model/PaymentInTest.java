package ish.oncourse.model;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.test.TestContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class PaymentInTest {

	private ObjectContext context;
	private College college;

	@Before
	public void setUp() throws Exception {
		TestContext testContext = new TestContext().open();

		this.context = testContext.getServerRuntime().newContext();

		this.college = context.newObject(College.class);
		college.setName("name");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		college.setRequiresAvetmiss(false);

		context.commitChanges();
	}

	@Test
	public void testZeroContraPayment() {


		PaymentIn pIn = context.newObject(PaymentIn.class);
		pIn.setAmount(Money.ONE);
		pIn.setType(PaymentType.CONTRA);
		pIn.setCollege(college);
		pIn.setSource(PaymentSource.SOURCE_ONCOURSE);
		try {
			context.commitChanges();
			assertTrue("The CONTRA payment-in amount must be $0", false);
		} catch (Exception e) {
			assertTrue(e instanceof ValidationException);
			assertTrue(((ValidationException) e).getValidationResult().getFailures().size() == 1);
			assertEquals("amount", ((ValidationFailure) ((ValidationException) e).getValidationResult().getFailures().get(0)).getProperty());
		}
	}
}