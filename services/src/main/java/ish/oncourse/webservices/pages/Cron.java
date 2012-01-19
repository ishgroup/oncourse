package ish.oncourse.webservices.pages;

import ish.oncourse.webservices.jobs.PaymentInExpireJob;
import ish.oncourse.webservices.jobs.SMSJob;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.TextStreamResponse;

public class Cron {

	private static final String SMS_JOB_IDENTIFIER = "smsJob";
	private static final String PAYMENT_IN_EXPIRE_JOB_IDENTIFIER = "paymentInExpireJob";

	@Inject
	private SMSJob smsJob;

	@Inject
	private PaymentInExpireJob paymentInExpireJob;

	public StreamResponse onActivate(final String jobIdentifier) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (SMS_JOB_IDENTIFIER.equalsIgnoreCase(jobIdentifier)) {
					smsJob.execute();

				} else if (PAYMENT_IN_EXPIRE_JOB_IDENTIFIER.equalsIgnoreCase(jobIdentifier)) {
					paymentInExpireJob.execute();
				}
			}
		}).start();

		return new TextStreamResponse("text/html", "OK");
	}

}
