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

import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._ACLAccessKey

import javax.annotation.Nonnull

/**
 * Access keys define access to a specific feature of onCourse for an ACLRole
 */
@API
class ACLAccessKey extends _ACLAccessKey {

	@Override
	void prePersist() {
		super.prePersist()

		updateAccountRoleKey()
	}

	@Override
	void preUpdate() {
		super.preUpdate()

		updateAccountRoleKey()
	}

	private void updateAccountRoleKey() {
		ACLRole parentRole = this.role
		ACLAccessKey accountPermission = parentRole.getAccessKeyForKeyCode(KeyCode.ACCOUNT)
		if (accountPermission == null) {
			accountPermission = context.newObject(ACLAccessKey).with {k ->
				k.role = this.role
				k.keycode = KeyCode.ACCOUNT
				k.mask = Mask.NONE
				k
			}
		}

		if (!isActionAllowed(accountPermission, Mask.VIEW) && isActionAllowedForKeycodes(parentRole, KeyCode.INVOICE, KeyCode.PAYMENT_IN, KeyCode.PAYMENT_OUT, KeyCode.DISCOUNT, KeyCode.MEMBERSHIP, KeyCode.PRODUCT, KeyCode.SALE, KeyCode.VOUCHER, KeyCode.BUDGET)) {
			accountPermission.mask = Mask.VIEW
		}
	}

	private boolean isActionAllowedForKeycodes(ACLRole role, KeyCode... keyCodes) {
		boolean result = false
		keyCodes.each {result = result || isActionAllowed(role.getAccessKeyForKeyCode(it), Mask.VIEW)}
		result
	}

	private boolean isActionAllowed(ACLAccessKey key, int mask) {
		boolean result = false
		if (key != null) {
			result = (key.mask & mask) == mask
		}
		return result
	}

	/**
	 * @return the mask for the access key, including any always allowed masks and removing any always denied
	 */
	@Nonnull
	@API
	@Override
	Integer getMask() {
		// the next line adds any always allowed masks and removes any always disallowed
		return (getKeycode().getAlwaysAllowedMask() | super.getMask()) & ~getKeycode().getNeverAllowedMask()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return
	 */
	@Override
	Boolean getIncludesChildren() {
		return super.getIncludesChildren()
	}

	/**
	 * @return the keycode this access right refers to
	 */
	@Nonnull
	@API
	@Override
	KeyCode getKeycode() {
		return super.getKeycode()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}



	/**
	 * @return the role for this key
	 */
	@Nonnull
	@API
	@Override
	ACLRole getRole() {
		return super.getRole()
	}

	Integer getRawMask() {
		return super.getMask()
	}
}
