package ish.oncourse.model;

import ish.common.payable.InvoicePayableLineWrapper;
import ish.common.payable.TaxInterface;
/**
 * Fake representation of tax entity,
 * it is needed by {@link InvoicePayableLine} implementations, because willow doesn't have own tax entity yet, but
 * {@link InvoicePayableLineWrapper} needs for not-null tax for calculations
 * TODO delete this class when "Tax" will be implemented
 * @author ksenia
 *
 */
public class FakeTax implements TaxInterface{

	public String getDescription() {
		return "fake representation of tax entity";
	}

}
