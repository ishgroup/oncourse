package ish.oncourse.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import ish.oncourse.model.auto._PaymentIn;

public class PaymentIn extends _PaymentIn {

	public BigDecimal totalIncGst() {
		BigDecimal totalExGst = getTotalExGst();
		BigDecimal totalGst = getTotalGst();
		BigDecimal total = new BigDecimal(BigInteger.ZERO);
		if (totalExGst != null) {
			total = total.add(totalExGst);
		}
		if (totalGst != null) {
			total = total.add(totalGst);
		}
		return total;
	}
}
