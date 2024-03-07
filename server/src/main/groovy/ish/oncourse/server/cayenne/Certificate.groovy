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
import ish.oncourse.server.cayenne.glue._Certificate
import ish.util.SecurityUtil
import ish.util.UrlUtil
import ish.validation.ValidationFailure
import org.apache.cayenne.query.MappedSelect
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate

import static ish.oncourse.cayenne.MappedSelectParams.*

/**
 * A certificate is an object describing a qualification or set of outcomes issued to a student.
 * Certificates might be created at the end of a single enrolment to capture just the
 * outcomes from that class. Or they might bring today outcomes from separate enrolments
 * and from RPL.
 *
 * Certificates cannot be deleted once they are printed or issued to the student. They can however
 * be revoked.
 *
 */
@API
@QueueableEntity
class Certificate extends _Certificate implements Queueable, AttachableTrait, ContactActivityTrait, CertificateTrait {
	private static final Logger logger = LogManager.getLogger()

	public static final SUCCESSFUL_OUTCOMES_PROPERTY = "successful_outcomes";
	public static final String OUTCOMES_COUNT_KEY = "outcomesCount";

	/**
	 * @return
	 */
	long getNextCertificateNumber() {
		MappedSelect query = MappedSelect.query(MAX_QUERY)
				.param(ENTITY_NAME_PARAMETER,  Certificate.class.getSimpleName())
				.param(FIELD_NAME,"MAX(CERTIFICATENUMBER)").forceNoCache()
		Map<String, ?> row = (Map<String, ?>) getObjectContext().performQuery(query).get(0)
		Number number = (Number) row.get(COUNT_COLUMN)

		long highestCertificateNumberInDB = number == null ? 0 : number.longValue()

		long highestCertificateNumberInContext = 0
		for (Object o : getObjectContext().uncommittedObjects()) {
			if (o instanceof Certificate && o != this) {
				Certificate cert = (Certificate) o
				if (cert.getCertificateNumber() != null && cert.getCertificateNumber() > highestCertificateNumberInContext) {
					highestCertificateNumberInContext = cert.getCertificateNumber()
				}
			}
		}

		long maxId = Math.max(highestCertificateNumberInDB, highestCertificateNumberInContext)
		return maxId >= 0 ? maxId + 1 : 1
	}

	@Override
	void postAdd() {
		super.postAdd()
		if (getCertificateNumber() == null) {
			setCertificateNumber(getNextCertificateNumber())
		}
		if (getUniqueCode() == null) {
			setUniqueCode(SecurityUtil.generateCertificateCode())
		}

	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		if (relation instanceof CertificateAttachmentRelation) {
			addToAttachmentRelations((CertificateAttachmentRelation) relation)
		} else {
			throw new IllegalArgumentException("expected CertificateAttachmentRelation.class, was " + relation.getClass())
		}
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		if (relation instanceof CertificateAttachmentRelation) {
			removeFromAttachmentRelations((CertificateAttachmentRelation) relation)
		} else {
			throw new IllegalArgumentException("expected CertificateAttachmentRelation.class, was " + relation.getClass())
		}
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return CertificateAttachmentRelation.class
	}

	/**
	 * @see ish.oncourse.server.cayenne.glue.CayenneDataObject#validateForDelete(ValidationResult)
	 */
	@Override
	void validateForDelete(@Nonnull ValidationResult validationResult) {
		super.validateForDelete(validationResult)
		if (getPrintedOn() != null) {
			validationResult.addFailure(ValidationFailure.validationFailure(this, PRINTED_ON.getName(), "Printed certificates cannot be deleted."))
		}
	}

	/**
	 * @return student first name
	 * @Deprecated Use certificate.student.contact.firstName instead
	 */
	@API
	@Deprecated
	String getStudentFirstName() {
		if (getStudent() != null && getStudent().getContact() != null) {
			return getStudent().getContact().getFirstName()
		}
		return null
	}

	/**
	 * @return student last name
	 * @Deprecated Use certificate.student.contact.lastName instead
	 */
	@API
	@Deprecated
	String getStudentLastName() {
		if (getStudent() != null && getStudent().getContact() != null) {
			return getStudent().getContact().getLastName()
		}
		return null
	}

	/**
	 * @return a unique number for this certificate
	 */
	@Nonnull
	@API
	@Override
	Long getCertificateNumber() {
		return super.getCertificateNumber()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	@Override
	String getInteractionName() {
		return qualification?.title
	}
/**
	 * @return true if this certificate is for a full qualification (or accredited course)
	 *
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsQualification() {
		return super.getIsQualification()
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
	 * @return the date this certificate was first printed
	 *
	 */
	@API
	@Override
	LocalDate getPrintedOn() {
		return super.getPrintedOn()
	}

	/**
	 * @return any private notes
	 *
	 */
	@API
	@Override
	String getPrivateNotes() {
		return super.getPrivateNotes()
	}

	/**
	 * @return number of outcomes
	 *
	 */
	String getOutcomesCount() {
		return getOutcomes().size();
	}

	/**
	 * @return public visible notes
	 *
	 */
	@API
	@Override
	String getPublicNotes() {
		return super.getPublicNotes()
	}

	/**
	 * If a certificate is revoked, this field will be not null. It is important to check this value
	 * before a certificate is exported or printed.
	 *
	 * @return the date of revocation
	 */
	@API
	@Override
	LocalDate getRevokedOn() {
		return super.getRevokedOn()
	}

	/**
	 * @return all the outcomes on this certificate
	 *
	 */
	@Nonnull
	@Override
    List<CertificateOutcome> getCertificateOutcomes() {
		return super.getCertificateOutcomes()
	}

	/**
	 * A certificate can be issued for outcomes toward a certain qualification even if the qualification
	 * itself is not issued. Check getIsQualification() to see whether this was a full qualification.
	 *
	 * @return the qualification
	 */
	@API
	@Override
	Qualification getQualification() {
		return super.getQualification()
	}

	/**
	 * @return the student who received the qualification
	 */
	@Nonnull
	@API
	@Override
	Student getStudent() {
		return super.getStudent()
	}


	/**
	 * @return Certificate unique code
	 */
	@Nullable
	@API
	@Override
	String getUniqueCode() {
		return super.getUniqueCode()
	}

	@Override
	String getSummaryDescription() {
		if(getStudent() == null || getStudent().getContact() == null) {
			return super.getSummaryDescription()
		}
		return "Certificate for " + getStudent().getContact().getName(false)
	}
}
