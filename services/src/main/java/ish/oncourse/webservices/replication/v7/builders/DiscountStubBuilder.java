package ish.oncourse.webservices.replication.v7.builders;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.model.Discount;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.DiscountStub;

public class DiscountStubBuilder extends AbstractWillowStubBuilder<Discount, DiscountStub> {

	@Override
	protected DiscountStub createFullStub(final Discount entity) {
		final DiscountStub stub = new DiscountStub();
		stub.setCode(entity.getCode());
		stub.setCombinationType(entity.getCombinationType());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setDetail(entity.getDetail());
		stub.setName(entity.getName());
		final Money discountAmount = entity.getDiscountAmount();
		if (discountAmount != null) {
			stub.setDiscountAmount(discountAmount.toBigDecimal());
		}
		stub.setDiscountRate(entity.getDiscountRate());
		final DiscountType discountType = entity.getDiscountType();
		if (discountType != null) {
			stub.setDiscountType(discountType.getDatabaseValue());
		}
		final Money maximumDiscount = entity.getMaximumDiscount();
		if (maximumDiscount != null) {
			stub.setMaximumDiscount(maximumDiscount.toBigDecimal());
		}
		final Money minimumDiscount = entity.getMinimumDiscount();
		if (minimumDiscount != null) {
			stub.setMinimumDiscount(minimumDiscount.toBigDecimal());
		}
		final MoneyRounding roundingMode = entity.getRoundingMode();
		if (roundingMode != null) {
			stub.setRoundingMode(roundingMode.getDatabaseValue());
		}
		stub.setStudentAge(entity.getStudentAge());
		stub.setStudentAgeOperator(entity.getStudentAgeOperator());
		stub.setStudentEnrolledWithinDays(entity.getStudentEnrolledWithinDays());
		stub.setStudentPostcodes(entity.getStudentPostcodes());
		stub.setValidFrom(entity.getValidFrom());
		stub.setValidTo(entity.getValidTo());
		stub.setHideOnWeb(entity.getHideOnWeb());
		stub.setAvailableOnWeb(entity.getIsAvailableOnWeb());
		return stub;
	}

}
