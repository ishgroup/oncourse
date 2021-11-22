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
<% replicationVersions.each { v -> %>
		case V${v}:
			ish.oncourse.webservices.v${v}.stubs.replication.ReplicatedRecord replV${v}Record = new ish.oncourse.webservices.v${v}.stubs.replication.ReplicatedRecord();
			StubUtils.setSuccessStatus(replV${v}Record);
			replV${v}Record.setStub(toV${v}Hollow(stub, setWillowId));
			return replV${v}Record;
<% } %>
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

<% replicationVersions.each { v -> %> 
	public static ish.oncourse.webservices.v${v}.stubs.replication.HollowStub toV${v}Hollow(final GenericReplicationStub stub, final boolean setWillowId) {
		ish.oncourse.webservices.v${v}.stubs.replication.HollowStub hollowStub = new ish.oncourse.webservices.v${v}.stubs.replication.HollowStub();
		fillHollowStub(stub, hollowStub, setWillowId);
		return hollowStub;
	}
<% } %>
	
	private static GenericPaymentInStub getPaymentInStub(final GenericTransactionGroup group) {
		for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryInfo()) {
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
		for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryInfo()) {
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
		for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryInfo()) {
			if (stub instanceof GenericEnrolmentStub) {
				return (GenericEnrolmentStub) stub;
			}
		}
		return null;
	}
}
