package ish.oncourse.webservices.replication.updaters;

import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.model.Discount;
import ish.oncourse.webservices.v4.stubs.replication.DiscountStub;

public class DiscountUpdater extends AbstractWillowUpdater<DiscountStub, Discount> {

	@Override
	protected void  updateEntity(DiscountStub stub, Discount entity, RelationShipCallback callback) {
		entity.setCode(stub.getCode());
		entity.setCombinationType(stub.isCombinationType());
		entity.setCreated(stub.getCreated());
		entity.setDetail(stub.getDetail());
		entity.setDiscountAmount(Money.valueOf(stub.getDiscountAmount()));
		entity.setDiscountRate(stub.getDiscountRate());
		entity.setIsCodeRequired(stub.isCodeRequired());
		entity.setMaximumDiscount(Money.valueOf(stub.getMaximumDiscount()));
		entity.setMinimumDiscount(Money.valueOf(stub.getMinimumDiscount()));
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setRoundingMode(MoneyRounding.getEnumForDatabaseValue(stub.getRoundingMode()));
		entity.setStudentAge(stub.getStudentAge());
		entity.setStudentAgeOperator(stub.getStudentAgeOperator());
		entity.setStudentEnrolledWithinDays(stub.getStudentEnrolledWithinDays());
		entity.setStudentPostcodes(stub.getStudentPostcodes());
		entity.setValidFrom(stub.getValidFrom());
		entity.setValidTo(stub.getValidTo());
	}
}
