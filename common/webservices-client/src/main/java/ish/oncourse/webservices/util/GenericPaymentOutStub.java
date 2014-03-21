package ish.oncourse.webservices.util;

import java.util.Date;

public interface GenericPaymentOutStub {
	public Integer getStatus();
	public Date getDateBanked();
	public Date getDatePaid();
}
