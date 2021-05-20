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
package ish.oncourse.server.lifecycle;

import com.google.inject.Inject;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.PaymentInLine;
import ish.util.AccountUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostAdd;

/**
 */
public class PaymentInLineLifecycleListener {

	@Inject
	public PaymentInLineLifecycleListener() {
	}

	@PostAdd(value = PaymentInLine.class)
	public void postAdd(PaymentInLine entity) {
		if (entity.getAccountOut() == null) {
			var aContext = entity.getContext();
			entity.setAccountOut(AccountUtil.getDefaultDebtorsAccount(aContext, Account.class));
		}
	}
}
