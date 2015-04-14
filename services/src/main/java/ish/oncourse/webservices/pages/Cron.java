package ish.oncourse.webservices.pages;

import ish.oncourse.webservices.jobs.PaymentInExpireJob;
import ish.oncourse.webservices.jobs.SMSJob;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.TextStreamResponse;

public class Cron {

	@Inject
	private SMSJob smsJob;

	@Inject
	private PaymentInExpireJob paymentInExpireJob;

	public StreamResponse onActivate() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// executing jobs
				paymentInExpireJob.execute();
				smsJob.execute();
			}
		}).start();

		return new TextStreamResponse("text/html", "OK");
	}

}
