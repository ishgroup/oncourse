/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.entity.services;

import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.cayenne.PaymentMethodInterface;
import ish.util.PaymentMethodUtil;

public class SetPaymentMethod {

	private PaymentMethodInterface method;
	private PaymentInterface payment;

	private SetPaymentMethod(PaymentMethodInterface method, PaymentInterface payment) {
		this.method = method;
		this.payment = payment;
	}

	public static SetPaymentMethod valueOf(PaymentMethodInterface method, PaymentInterface payment) {
		return new SetPaymentMethod(method, payment);
	}

	public void set() {
		payment.setPaymentMethod(method);

		if (method != null) {
			if (!PaymentMethodUtil.SYSTEM_TYPES.contains(method.getType())) {
				payment.setAccount(method.getAccount());
				payment.setUndepositedFundsAccount(method.getUndepositedFundsAccount());
			}
		}
	}
}
