/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.services;

import ish.common.types.PaymentType;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjEntity;

import java.util.*;

/**
 * Supplimentary replication methods.
 */
public class ReplicationUtils {

	public static final int GENERIC_EXCEPTION = 1;

	public static String getEntityName(Class<? extends Queueable> clazz) {
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
	}

	public static Class<? extends Queueable> getEntityClass(ObjectContext objectContext, String entityIdentifier) {
        ObjEntity entity = objectContext.getEntityResolver().getObjEntity(entityIdentifier);
        if (entity == null)
            throw new IllegalArgumentException(String.format("CayenneRuntime does not contain entity with name: %s",entityIdentifier));
		Class<? extends Queueable> entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver()
				.getObjEntity(entityIdentifier).getJavaClass();
		return entityClass;
	}

	@SuppressWarnings("unchecked")
	public static Collection<Queueable> toCollection(Object object) {

		if (object == null) {
			return Collections.emptyList();
		} else if (object instanceof Collection) {
			return new ArrayList<>((Collection<Queueable>) object);
		} else {
			Queueable q = (Queueable) object;
			return Collections.singleton(q);
		}
	}

	public static GenericReplicatedRecord toReplicatedRecord(final GenericReplicationStub stub, final boolean setWillowId) {
		final SupportedVersions version = PortHelper.getVersionByReplicationStub(stub);
		switch (version) {

		case V13:
			ish.oncourse.webservices.v13.stubs.replication.ReplicatedRecord replV13Record = new ish.oncourse.webservices.v13.stubs.replication.ReplicatedRecord();
			StubUtils.setSuccessStatus(replV13Record);
			replV13Record.setStub(toV13Hollow(stub, setWillowId));
			return replV13Record;

		case V17:
			ish.oncourse.webservices.v17.stubs.replication.ReplicatedRecord replV17Record = new ish.oncourse.webservices.v17.stubs.replication.ReplicatedRecord();
			StubUtils.setSuccessStatus(replV17Record);
			replV17Record.setStub(toV17Hollow(stub, setWillowId));
			return replV17Record;

		default:
			return null;
		}
	}

	private static void fillHollowStub(final GenericReplicationStub stub, final GenericReplicationStub hollowStub, final boolean setWillowId) {
		hollowStub.setEntityIdentifier(stub.getEntityIdentifier());
		hollowStub.setAngelId(stub.getAngelId());
		Date today = new Date();
		hollowStub.setModified(today);
		hollowStub.setCreated(today);
		if (setWillowId) {
			hollowStub.setWillowId(stub.getWillowId());
		}
	}

 
	public static ish.oncourse.webservices.v13.stubs.replication.HollowStub toV13Hollow(final GenericReplicationStub stub, final boolean setWillowId) {
		ish.oncourse.webservices.v13.stubs.replication.HollowStub hollowStub = new ish.oncourse.webservices.v13.stubs.replication.HollowStub();
		fillHollowStub(stub, hollowStub, setWillowId);
		return hollowStub;
	}
 
	public static ish.oncourse.webservices.v17.stubs.replication.HollowStub toV17Hollow(final GenericReplicationStub stub, final boolean setWillowId) {
		ish.oncourse.webservices.v17.stubs.replication.HollowStub hollowStub = new ish.oncourse.webservices.v17.stubs.replication.HollowStub();
		fillHollowStub(stub, hollowStub, setWillowId);
		return hollowStub;
	}

	
	private static GenericPaymentInStub getPaymentInStub(final GenericTransactionGroup group) {
		for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				return (GenericPaymentInStub) stub;
			}
		}
		return null;
	}
	
	public static boolean isCreditCardPayment(final GenericTransactionGroup group) {
		GenericPaymentInStub paymentInStub = getPaymentInStub(group);
		if (paymentInStub != null) {
			return PaymentType.CREDIT_CARD.getDatabaseValue().equals(paymentInStub.getType());
		}
		return false;
	}

	public static GenericPaymentOutStub getPaymentOutStub(final GenericTransactionGroup group) {
		for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentOutStub) {
				return (GenericPaymentOutStub) stub;
			}
		}
		return null;
	}

	public static GenericReplicatedRecord replicatedEnrolmentRecord(final List<GenericReplicatedRecord> records) {
		for (GenericReplicatedRecord r : records) {
			if (ReplicationUtils.getEntityName(Enrolment.class).equals(r.getStub().getEntityIdentifier())) {
				return r;
			}
		}
		throw new RuntimeException(String.format("Enrolment record with should always present."));
	}

	public static GenericReplicatedRecord replicatedPaymentInRecord(final List<GenericReplicatedRecord> records) {
		for (GenericReplicatedRecord r : records) {
			if (ReplicationUtils.getEntityName(PaymentIn.class).equals(r.getStub().getEntityIdentifier())) {
				return r;
			}
		}
		throw new RuntimeException(String.format("Enrolment record with should always present."));
	}

	public static GenericReplicatedRecord replicatedPaymentOutRecord(final List<GenericReplicatedRecord> records) {
		for (GenericReplicatedRecord r : records) {
			if (ReplicationUtils.getEntityName(PaymentOut.class).equals(r.getStub().getEntityIdentifier())) {
				return r;
			}
		}
		throw new RuntimeException(String.format("Enrolment record with should always present."));
	}

	public static GenericEnrolmentStub getEnrolmentStub(final GenericTransactionGroup group) {
		for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericEnrolmentStub) {
				return (GenericEnrolmentStub) stub;
			}
		}
		return null;
	}
}
