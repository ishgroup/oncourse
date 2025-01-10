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
package ish.oncourse.cayenne.glue

import ish.math.Money
import ish.oncourse.cayenne.AccountInterface
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.SiteInterface
import ish.oncourse.cayenne.SystemUserInterface
import org.apache.cayenne.PersistentObject

/**
 * An abstract entity gathering all the code shared between PaymentIn and PaymentOut entities
 *
 */
abstract class Payment extends PersistentObject implements PaymentInterface {

	/**
	 *
	 */


	// methods in fact identical between payments
	abstract void setAdministrationCentre(SiteInterface administrationCentre);

	abstract void setCreatedBy(SystemUserInterface createdByUser);

	abstract void setAmount(Money amount);

	// methods different between payments
	abstract void setAccount(AccountInterface account);

}
