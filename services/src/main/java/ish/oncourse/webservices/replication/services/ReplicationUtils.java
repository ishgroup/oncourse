/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.services;

import ish.common.types.PaymentType;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInStub;
import ish.oncourse.webservices.v4.stubs.replication.PaymentOutStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;

/**
 * Supplimentary replication methods.
 * 
 * @author anton
 */
public class ReplicationUtils {

	public static String getEntityName(Class<? extends Queueable> clazz) {
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
	}

	public static Class<? extends Queueable> getEntityClass(ObjectContext objectContext, String entityIdentifier) {
		@SuppressWarnings("unchecked")
		Class<? extends Queueable> entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver()
				.getObjEntity(entityIdentifier).getJavaClass();
		return entityClass;
	}

	@SuppressWarnings("unchecked")
	public static Collection<Queueable> toCollection(Object object) {

		if (object == null) {
			return Collections.emptyList();
		} else if (object instanceof Collection) {
			return new ArrayList<Queueable>((Collection<Queueable>) object);
		} else {
			Queueable q = (Queueable) object;
			return Collections.singleton(q);
		}
	}

	public static GenericReplicatedRecord toReplicatedRecord(GenericReplicationStub stub) {
		ReplicatedRecord replRecord = new ReplicatedRecord();
		replRecord.setSuccessStatus();
		replRecord.setStub(toHollow(stub));
		return replRecord;
	}

	public static HollowStub toHollow(GenericReplicationStub stub) {
		HollowStub hollowStub = new HollowStub();
		hollowStub.setEntityIdentifier(stub.getEntityIdentifier());
		hollowStub.setAngelId(stub.getAngelId());
		Date today = new Date();
		hollowStub.setModified(today);
		hollowStub.setCreated(today);
		return hollowStub;
	}

	private static PaymentInStub getPaymentInStub(GenericTransactionGroup group) {

		for (GenericReplicationStub stub : group.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof PaymentInStub) {
				return (PaymentInStub) stub;
			}
		}

		return null;
	}
	
	public static boolean isCreditCardPayment(GenericTransactionGroup group) {
		PaymentInStub paymentInStub = getPaymentInStub(group);
		if (paymentInStub != null) {
			return PaymentType.CREDIT_CARD.getDatabaseValue().equals(paymentInStub.getType());
		}
		return false;
	}

	public static PaymentOutStub getPaymentOutStub(TransactionGroup group) {

		for (ReplicationStub stub : group.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof PaymentOutStub) {
				return (PaymentOutStub) stub;
			}
		}

		return null;
	}

	public static ReplicatedRecord replicatedEnrolmentRecord(List<ReplicatedRecord> records) {
		for (ReplicatedRecord r : records) {
			if (ReplicationUtils.getEntityName(Enrolment.class).equals(r.getStub().getEntityIdentifier())) {
				return r;
			}
		}

		throw new RuntimeException(String.format("Enrolment record with should always present."));
	}

	public static ReplicatedRecord replicatedPaymentInRecord(List<ReplicatedRecord> records) {
		for (ReplicatedRecord r : records) {
			if (ReplicationUtils.getEntityName(PaymentIn.class).equals(r.getStub().getEntityIdentifier())) {
				return r;
			}
		}

		throw new RuntimeException(String.format("Enrolment record with should always present."));
	}

	public static GenericReplicatedRecord replicatedPaymentOutRecord(List<GenericReplicatedRecord> records) {
		for (GenericReplicatedRecord r : records) {
			if (ReplicationUtils.getEntityName(PaymentOut.class).equals(r.getStub().getEntityIdentifier())) {
				return r;
			}
		}

		throw new RuntimeException(String.format("Enrolment record with should always present."));
	}

	public static EnrolmentStub getEnrolmentStub(TransactionGroup group) {

		for (ReplicationStub stub : group.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof EnrolmentStub) {
				return (EnrolmentStub) stub;
			}
		}

		return null;
	}
}
