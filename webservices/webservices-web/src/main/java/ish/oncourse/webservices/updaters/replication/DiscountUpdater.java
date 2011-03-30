package ish.oncourse.webservices.updaters.replication;

import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.model.College;
import ish.oncourse.model.Discount;
import ish.oncourse.webservices.v4.stubs.replication.DiscountStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

import org.apache.cayenne.ObjectContext;

public class DiscountUpdater extends AbstractWillowUpdater<DiscountStub, Discount> {

	@Override
	protected void  updateEntity(DiscountStub stub, Discount entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCode(stub.getCode());
		entity.setCollege(college);
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
