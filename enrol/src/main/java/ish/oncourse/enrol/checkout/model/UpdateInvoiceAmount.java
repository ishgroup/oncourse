package ish.oncourse.enrol.checkout.model;

import ish.math.Money;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.Invoice;
import ish.util.InvoiceUtil;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UpdateInvoiceAmount {
	private Invoice invoice;
	private CorporatePass corporatePass;

	public void update() {
		Money totalGst = InvoiceUtil.sumInvoiceLines(invoice.getInvoiceLines(), true);
		Money totalExGst = InvoiceUtil.sumInvoiceLines(invoice.getInvoiceLines(), false);
		invoice.setTotalExGst(totalExGst);
		invoice.setTotalGst(totalGst);
		invoice.setCorporatePassUsed(corporatePass);
	}

	public static UpdateInvoiceAmount valueOf(Invoice invoice, CorporatePass corporatePass) {
		UpdateInvoiceAmount result = new UpdateInvoiceAmount();
		result.invoice = invoice;
		result.corporatePass = corporatePass;
		return result;
	}
}
