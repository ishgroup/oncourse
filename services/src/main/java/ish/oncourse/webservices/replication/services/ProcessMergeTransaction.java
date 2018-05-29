/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ContactDuplicate;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.toReplicatedRecord;
import static ish.oncourse.webservices.replication.services.TransactionGroupProcessorImpl.MERGE_KEY;

/**
 * User: akoiro
 * Date: 9/6/17
 */
public class ProcessMergeTransaction {
	private static final Logger logger = LogManager.getLogger();

	private ObjectContext context;
	private TransactionGroupProcessorImpl processor;

	private GenericTransactionGroup group;
	private College currentCollege = null;

	private ProcessMergeTransaction() {
		super();
	}

	public void process() {
		GenericReplicatedRecord replicatedContactDuplicate = null;
		try {
			GenericReplicationStub contactDuplicateStub = getContactDuplicateStub(group);
			replicatedContactDuplicate = processSingleStub(contactDuplicateStub, group);

			context.commitChanges();
			processor.fillWillowIds();

			cleanContactDuplicateStubs(group);

			ContactDuplicate contactDuplicate = MergeProcessor
					.valueOf(context, group.getGenericAttendanceOrBinaryDataOrBinaryInfo())
					.processMerge(replicatedContactDuplicate);

			removeStub(group, Contact.class.getSimpleName(), contactDuplicate.getContactToDeleteId(), contactDuplicate.getContactToDeleteAngelId());

			try {
				processor.processRegularTransaction(group);
			} catch (Exception e) {
				logger.error("Merge operation was completed on willow side. Unexpected error occurred during process merge stubs by regular mechanism.", e);
				throw e;
			}
		} finally {
			//leave contactDuplicate for angel response only
			processor.cleanResult();
			if (replicatedContactDuplicate != null) {
				processor.addToResult(replicatedContactDuplicate);
			}
		}
	}

	private void cleanContactDuplicateStubs(GenericTransactionGroup group) {
		Iterator<GenericReplicationStub> groupIterator = group.getGenericAttendanceOrBinaryDataOrBinaryInfo().iterator();
		while (groupIterator.hasNext()) {
			String entityIdentifier = groupIterator.next().getEntityIdentifier();
			if (ContactDuplicate.class.getSimpleName().equals(entityIdentifier)) {
				groupIterator.remove();
			}
		}
	}

	private boolean removeStub(GenericTransactionGroup group, String entityIdentifier, Long willowId, Long angelId) {
		GenericReplicationStub forRemove = null;
		for (GenericReplicationStub s : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if ((s.getEntityIdentifier().equals(entityIdentifier) && (s.getAngelId() == angelId || s.getWillowId() == willowId))) {
				forRemove = s;
				break;
			}
		}
		return group.getGenericAttendanceOrBinaryDataOrBinaryInfo().remove(forRemove);
	}


	/**
	 * Get the ContactDuplicate stub from current transaction group with assigned QueuedRecord with action CREATE. If
	 * transaction group does not contain or contain more than one ContactDuplicate with CREATE action throws exception.
	 * @param group transaction group
	 * @return contact duplicate stub or <code>null</code>
	 */
	private GenericReplicationStub getContactDuplicateStub(GenericTransactionGroup group) {
		List<Long> contactDuplicateAngelIds = new ArrayList<>();
		for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()){
			if (ContactDuplicate.class.getSimpleName().equals(stub.getEntityIdentifier())){
				contactDuplicateAngelIds.add(stub.getAngelId());
			}
		}
		GenericReplicationStub res = null;

		if (contactDuplicateAngelIds.size() > 0){
			if (contactDuplicateAngelIds.size() > 1) {
				List<ContactDuplicate> contactDuplicateList = ObjectSelect.query(ContactDuplicate.class)
						.where(ContactDuplicate.ANGEL_ID.in(contactDuplicateAngelIds).andExp(ContactDuplicate.COLLEGE.eq(currentCollege)))
							.select(context);

				// all contact duplicate stubs that not found in database accept as new (CREATED)
				for (ContactDuplicate cd : contactDuplicateList) {
					contactDuplicateAngelIds.remove(cd.getAngelId());
				}
				if (contactDuplicateAngelIds.size() == 1) {
					for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
						if (stub.getAngelId() == contactDuplicateAngelIds.get(0)) {
							res = stub;
							break;
						}
					}
				} else {
					throw new IllegalStateException(
							String.format("Merge transaction {transaction key: %s} contain more than one or zero ContactDuplicate.",
									getMergeTransactionGroupUniqueKey(group)));
				}
			} else {
				for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()){
					if (stub.getAngelId() == contactDuplicateAngelIds.get(0) && ContactDuplicate.class.getSimpleName().equals(stub.getEntityIdentifier())) {
						res = stub;
						break;
					}
				}
			}
		} else {
			throw new IllegalStateException(String.format("Transaction {key : %s} has MERGE key but without any ContactDuplicate stub.",
					getMergeTransactionGroupUniqueKey(group)));
		}

		return res;
	}

	private String getMergeTransactionGroupUniqueKey(GenericTransactionGroup group){
		String transactionKey = "undefined";
		for (String key : group.getTransactionKeys()){
			if (!MERGE_KEY.equals(key)){
				transactionKey = key;
			}
		}
		return transactionKey;
	}


	private GenericReplicatedRecord processSingleStub(GenericReplicationStub stub, GenericTransactionGroup group) {
		GenericReplicatedRecord replicationRec = toReplicatedRecord(stub, false);
		this.processor.addToResult(replicationRec);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().remove(stub);
		this.processor.processStub(stub);
		return replicationRec;
	}

	public static ProcessMergeTransaction valueOf(ObjectContext context, GenericTransactionGroup group,  TransactionGroupProcessorImpl processor, College currentCollege) {
		ProcessMergeTransaction result = new ProcessMergeTransaction();
		result.currentCollege = currentCollege;
		result.group = group;
		result.context = context;
		result.processor = processor;
		return result;
	}


}
