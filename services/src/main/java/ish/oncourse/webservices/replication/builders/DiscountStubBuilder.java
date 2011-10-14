package ish.oncourse.webservices.replication.builders;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.model.Discount;
import ish.oncourse.webservices.v4.stubs.replication.DiscountStub;

public class DiscountStubBuilder extends AbstractWillowStubBuilder<Discount, DiscountStub> {

	@Override
	protected DiscountStub createFullStub(Discount entity) {
		DiscountStub stub = new DiscountStub();

		stub.setCode(entity.getCode());
		stub.setCodeRequired(entity.getIsCodeRequired());
		stub.setCombinationType(entity.getCombinationType());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setDetail(entity.getDetail());
		stub.setName(entity.getName());

		Money discountAmount = entity.getDiscountAmount();
		if (discountAmount != null) {
			stub.setDiscountAmount(discountAmount.toBigDecimal());
		}

		stub.setDiscountRate(entity.getDiscountRate());

		DiscountType discountType = entity.getDiscountType();
		if (discountType != null) {
			stub.setDiscountType(discountType.getDatabaseValue());
		}

		Money maximumDiscount = entity.getMaximumDiscount();
		if (maximumDiscount != null) {
			stub.setMaximumDiscount(maximumDiscount.toBigDecimal());
		}

		Money minimumDiscount = entity.getMinimumDiscount();
		if (minimumDiscount != null) {
			stub.setMinimumDiscount(minimumDiscount.toBigDecimal());
		}

		MoneyRounding roundingMode = entity.getRoundingMode();
		if (roundingMode != null) {
			stub.setRoundingMode(roundingMode.getDatabaseValue());
		}

		stub.setStudentAge(entity.getStudentAge());
		stub.setStudentAgeOperator(entity.getStudentAgeOperator());
		stub.setStudentEnrolledWithinDays(entity.getStudentEnrolledWithinDays());
		stub.setStudentPostcodes(entity.getStudentPostcodes());
		stub.setValidFrom(entity.getValidFrom());
		stub.setValidTo(entity.getValidTo());

		return stub;
	}

}
