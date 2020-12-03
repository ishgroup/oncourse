/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.webservices.v23.stubs.replication.EnrolmentStub

/**
 */
class EnrolmentStubBuilder extends AbstractAngelStubBuilder<Enrolment, EnrolmentStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected EnrolmentStub createFullStub(Enrolment en) {
		def stub = new EnrolmentStub()
		stub.setCourseClassId(en.getCourseClass().getId())
		stub.setCreated(en.getCreatedOn())
		stub.setModified(en.getModifiedOn())
		if (!en.getInvoiceLines().isEmpty()) {
			stub.setInvoiceLineId(en.getInvoiceLines().get(0).getId())
		} else {
			logger.error("Enrollment without invoice line detected with id = {} and willowid = {}", en.getId(), en.getWillowId())
		}
		stub.setReasonForStudy(en.getStudyReason().getDatabaseValue())
		stub.setSource(en.getSource().getDatabaseValue())
		stub.setStudentId(en.getStudent().getId())
		if (en.getStatus() != null) {
			stub.setStatus(en.getStatus().name())
		}
		stub.setCreditOfferedValue(en.getCreditOfferedValue())
		stub.setCreditProvider(en.getCreditProvider())
		stub.setCreditUsedValue(en.getCreditUsedValue())
		stub.setCreditFoeId(en.getCreditFOEId())
		if (en.getFeeStatus() != null) {
			stub.setFeeStatus(en.getFeeStatus().getDatabaseValue())
		}
		if (en.getCreditTotal() != null) {
			stub.setCreditTotal(en.getCreditTotal().getDatabaseValue())
		}
		if (en.getCreditType() != null) {
			stub.setCreditType(en.getCreditType().getDatabaseValue())
		}
		if (en.getCreditLevel() != null) {
			stub.setCreditLevel(en.getCreditLevel().getDatabaseValue())
		}
		if (en.getCreditProviderType() != null) {
			stub.setCreditProviderType(en.getCreditProviderType().getDatabaseValue())
		}
		if (en.getFeeHelpStatus() != null) {
			stub.setFeeHelpStatus(en.getFeeHelpStatus().getDatabaseValue())
		}
		if (en.getConfirmationStatus() != null) {
			stub.setConfirmationStatus(en.getConfirmationStatus().getDatabaseValue())
		}
		if (en.getFundingSource() != null) {
			stub.setFundingSource(en.getFundingSource().getDatabaseValue())
		}
		if (en.getFeeHelpAmount() != null) {
			stub.setFeeHelpAmount(en.getFeeHelpAmount())
		}

		return stub
	}
}
