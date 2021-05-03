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

package ish.oncourse.server.cayenne

import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._MembershipProduct
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.util.List

/**
 * A persistent class mapped as "MembershipProduct" Cayenne entity.
 */
@API
@QueueableEntity
class MembershipProduct extends _MembershipProduct {

	private static final Logger logger = LogManager.getLogger()

	@Override
	void validateForSave(@Nonnull ValidationResult result) {
		super.validateForSave(result)

		if (getPriceExTax() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, PRICE_EX_TAX.getName(), "The price cannot be null."))
		}

		if (StringUtils.trimToNull(getName()) == null) {
			result.addFailure(ValidationFailure.validationFailure(this, NAME.getName(), "Name cannot be empty."))
		}

		if (StringUtils.trimToNull(getSku()) == null) {
			result.addFailure(ValidationFailure.validationFailure(this, SKU.getName(), "SKU cannot be null."))
		}

		if (getExpiryType() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, EXPIRY_TYPE.getName(), "Expiry type must be set."))
		}
	}

	/**
	 * @return relational object mapping this object to the Discount used to purchase it
	 */
	@Nonnull
	@API
	@Override
	List<DiscountMembership> getDiscountMemberships() {
		return super.getDiscountMemberships()
	}

	/**
	 * @return Discounts available to be used with purchase of this Membership
	 */
	@Nonnull
	@API
	@Override
	List<Discount> getDiscountsAvailable() {
		return super.getDiscountsAvailable()
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return MembershipCustomField
	}
}
