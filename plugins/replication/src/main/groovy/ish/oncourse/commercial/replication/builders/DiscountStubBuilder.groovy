/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Discount
import ish.oncourse.webservices.v23.stubs.replication.DiscountStub

/**
 */
class DiscountStubBuilder extends AbstractAngelStubBuilder<Discount, DiscountStub> {
	static final String DIGIT_PATTERN = "\\d+"
	static final String AGE_UNDER = "<"
	static final String AGE_OVER = ">"

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected DiscountStub createFullStub(final Discount d) {
		final def stub = new DiscountStub()
		stub.setCode(d.getCode())
		stub.setCreated(d.getCreatedOn())
		if (d.getDiscountMax() != null) {
			stub.setMaximumDiscount(d.getDiscountMax().toBigDecimal())
		}
		if (d.getDiscountMin() != null) {
			stub.setMinimumDiscount(d.getDiscountMin().toBigDecimal())
		}
		if (d.getDiscountDollar() != null) {
			stub.setDiscountAmount(d.getDiscountDollar().toBigDecimal())
		}
		stub.setDiscountRate(d.getDiscountPercent())
		stub.setModified(d.getModifiedOn())
		stub.setName(d.getName())
		def studentAge = d.getStudentAge()
		if (studentAge != null && studentAge.length() > 0) {
			if (studentAge.startsWith(AGE_UNDER)) {
				stub.setStudentAgeOperator(AGE_UNDER)
			} else if (studentAge.startsWith(AGE_OVER)) {
				stub.setStudentAgeOperator(AGE_OVER)
			}
			if (studentAge.length() > 1) {
				studentAge = studentAge.substring(2)
				if (studentAge.matches(DIGIT_PATTERN)) {
					stub.setStudentAge(new Integer(studentAge))
				}
			}
		}
		stub.setStudentEnrolledWithinDays(d.getStudentEnrolledWithinDays())
		stub.setStudentPostcodes(d.getStudentPostcode())
		stub.setValidTo(d.getValidTo())
		stub.setValidFrom(d.getValidFrom())
		stub.setRoundingMode(d.getRounding().getDatabaseValue())
		stub.setDetail(d.getPublicDescription())
		stub.setDiscountType(d.getDiscountType().getDatabaseValue())
		stub.setHideOnWeb(d.getHideOnWeb())
		stub.setAvailableOnWeb(d.getIsAvailableOnWeb())
		stub.setMinEnrolments(d.getMinEnrolments())
		if (d.getMinValue() != null) {
			stub.setMinValue(d.getMinValue().toBigDecimal())
		}
		stub.setValidFromOffset(d.getValidFromOffset())
		stub.setValidToOffset(d.getValidToOffset())
		stub.setLimitPreviousEnrolment(d.getLimitPreviousEnrolment())
		return stub
	}

}
