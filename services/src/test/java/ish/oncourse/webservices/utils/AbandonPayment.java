package ish.oncourse.webservices.utils;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.services.AppModule;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.test.PageTester;

import javax.naming.NamingException;
import java.sql.SQLException;

public class AbandonPayment extends AbstractUtil {
	public static void main(String[] args) throws SQLException, NamingException {

		AbandonPayment abandonPayment = new AbandonPayment();
		abandonPayment.setUser(args[0]);
		abandonPayment.setPassword(args[1]);
		abandonPayment.setDataSourceUrl(args[2]);
		abandonPayment.init();

		PageTester tester = new PageTester("ish.oncourse.webservices.services", StringUtils.EMPTY, "src/main/webapp", AppModule.class);

		ICayenneService cayenneService = tester.getService(ICayenneService.class);

		ObjectContext context = cayenneService.newContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 1542921);
		paymentIn.abandonPayment();

	}
}
