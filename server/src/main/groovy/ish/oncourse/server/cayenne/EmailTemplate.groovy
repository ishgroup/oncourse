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
import ish.oncourse.server.cayenne.glue._EmailTemplate

import javax.annotation.Nonnull
import java.util.Date

/**
 * Email template defines generic structure of an email suited for some particular purpose (enrolment confirmation,
 * invoice, cancellation notice) which is meant to be filled in with specific data and sent out repeatedly.
 * Email templates in onCourse use Groovy templates engine which uses Groovy language syntax.
 */
@API
@QueueableEntity
class EmailTemplate extends _EmailTemplate implements Queueable, AutomationTrait {
	@Override
	protected void postAdd() {
		if(isSubtemplate == null)
			setIsSubtemplate(false)

		super.postAdd()
	}


	/**
	 * @return email HTML body template
	 */
	@API
	@Override
	String getBodyHtml() {
		return super.getBodyHtml()
	}

	/**
	 * @return email plain text body template
	 */
	@API
	@Override
	String getBodyPlain() {
		return super.getBodyPlain()
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
	 * @return specific record type email is linked to (e.g. enrolment for enrolment confirmation)
	 */
	@API
	@Override
	String getEntity() {
		return super.getEntity()
	}

	@Override
	String getBody() {
		return super.getBodyHtml()
	}
/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	@Override
	Class<? extends AutomationBinding> getAutomationBindingClass() {
		return EmailTemplateAutomationBinding
	}

	@Override
	void setBody(String body) {
		super.setBodyHtml(body)
	}
/**
	 * @return name of this email template
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return email subject
	 */
	@API
	@Override
	String getSubject() {
		return super.getSubject()
	}
}



