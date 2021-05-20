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

import com.sun.istack.NotNull
import ish.common.types.KeyCode
import ish.messaging.ISystemUser
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._SystemUser
import org.apache.cayenne.validation.BeanValidationFailure
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate

/**
 * These objects represent people who are allowed to log into onCourse.
 *
 */
@API
@QueueableEntity
class SystemUser extends _SystemUser implements ISystemUser, Queueable {



	private transient Map<KeyCode, ACLAccessKey> accessKeys

	@Override
	void postAdd() {
		super.postAdd()
		if (getIsAdmin() == null) {
			setIsAdmin(true)
		}
		if (getIsActive() == null) {
			setIsActive(true)
		}
		if (getCanEditCMS() == null) {
			setCanEditCMS(false)
		}
		if (getCanEditTara() == null) {
			setCanEditTara(false)
		}

		if (getPasswordUpdateRequired() == null) {
			setPasswordUpdateRequired(false)
		}

		if (getPasswordLastChanged() == null) {
			setPasswordLastChanged(LocalDate.now())
		}
	}

	@Override
	void validateForSave(@Nonnull ValidationResult vr) {
		super.validateForSave(vr)

		if (getIsAdmin() == null) {
			vr.addFailure(new BeanValidationFailure(this, IS_ADMIN_PROPERTY, "The admin property has to be set."))
		}

		if (getIsActive() == null) {
			vr.addFailure(new BeanValidationFailure(this, IS_ACTIVE_PROPERTY, "The active property has to be set."))
		}
	}

	/**
	 * @return true if the user can log into the CMS
	 */
	@Nonnull
	@API
	@Override
	Boolean getCanEditCMS() {
		return super.getCanEditCMS()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	Boolean getCanEditTara() {
		return super.getCanEditTara()
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
	 * @return the user's email address
	 */
	@API
	@Override
	String getEmail() {
		return super.getEmail()
	}

	/**
	 * @return the user's first name
	 */
	@Nonnull
	@API
	@Override
	String getFirstName() {
		return super.getFirstName()
	}


	/**
	 * @return true if the user is allowed to log into onCourse or the CMS
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsActive() {
		return super.getIsActive()
	}

	/**
	 * @return true if the user is an administrator who bypasses all access control checks
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsAdmin() {
		return super.getIsAdmin()
	}

	/**
	 * @return the IP address from which the user logged in last time
	 */
	@API
	@Override
	String getLastLoginIP() {
		return super.getLastLoginIP()
	}

	/**
	 * @return the last time this user logged in
	 */
	@API
	@Override
	Date getLastLoginOn() {
		return super.getLastLoginOn()
	}

	/**
	 * @return the user's last name
	 */
	@Nonnull
	@API
	@Override
	String getLastName() {
		return super.getLastName()
	}

	/**
	 * @return the login name
	 */
	@Nonnull
	@API
	@Override
	String getLogin() {
		return super.getLogin()
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
	 * @return
	 */
	@Nonnull
	@Override
	List<ACLRole> getAclRoles() {
		return super.getAclRoles()
	}

	/**
	 * @return default administration site for this user
	 */
	@Nonnull
	@API
	@Override
	Site getDefaultAdministrationCentre() {
		return super.getDefaultAdministrationCentre()
	}

	/**
	 * @return preferences belonging to this user
	 */
	@Nonnull
	@API
	@Override
	List<Preference> getPreferences() {
		return super.getPreferences()
	}

	/**
	 * @return saved finds created by this user
	 */
	@Nonnull
	@Override
	List<SavedFind> getSavedFinds() {
		return super.getSavedFinds()
	}

	/**
	 * @return date of last password update
	 */
	@Nullable
	@API
	@Override
	LocalDate getPasswordLastChanged() {
		return super.getPasswordLastChanged()
	}

	/**
	 * @return true if user needs to update password on next log into onCourse
	 */
	@Nonnull
	@API
	@Override
	Boolean getPasswordUpdateRequired() {
		return super.getPasswordUpdateRequired()
	}

	@Override
	String getSummaryDescription() {
		return getEmail()
	}
	String getFullName() {
		getFullName(Boolean.TRUE)
	}

	/**
	 * @param firstNameFirst flag which specify that first name is first or not
	 * @return full name of system user
	 */
	@NotNull
	@API
	String getFullName(boolean firstNameFirst) {
		StringBuilder builder = new StringBuilder()

		if (firstNameFirst) {
			if (StringUtils.isNotBlank(getFirstName())) { builder.append(getFirstName()) }
			if (StringUtils.isNotBlank(getLastName())) { builder.append(StringUtils.SPACE).append(getLastName()) }
		} else {
			if (StringUtils.isNotBlank(getLastName())) { builder.append(getLastName()) }
			if (StringUtils.isNotBlank(getFirstName())) { builder.append(StringUtils.SPACE).append(getFirstName()) }
		}

		builder.toString()
	}
}
