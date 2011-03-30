package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

public abstract class AbstractWillowUpdater<V extends ReplicationStub, T extends Queueable> implements IWillowUpdater<V> {

	private static final Logger logger = Logger.getLogger(AbstractWillowUpdater.class);

	@SuppressWarnings("rawtypes")
	private IWillowUpdater next;

	private ObjectContext objectContext;
	
	protected College college;

	private TransactionGroup group;

	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}

	public void setCollege(College college) {
		this.college = (College) this.objectContext.localObject(college.getObjectId(), null);
	}

	public void setGroup(TransactionGroup group) {
		this.group = group;
	}
	
	public void setNext(@SuppressWarnings("rawtypes") IWillowUpdater next) {
		this.next = next;
	}

	private Queueable findEntityByWillowId(Long entityId, String entityIdentifier) {
		SelectQuery q = new SelectQuery(getEntityClass(entityIdentifier));
		q.andQualifier(ExpressionFactory.matchDbExp("id", entityId));
		q.andQualifier(ExpressionFactory.matchExp("college", this.college));
		return (Queueable) Cayenne.objectForQuery(objectContext, q);
	}

	private Queueable findEntityByAngelId(ReplicationStub stub) {
		SelectQuery q = new SelectQuery(getEntityClass(stub.getEntityIdentifier()));
		q.andQualifier(ExpressionFactory.matchDbExp("angelId", stub.getAngelId()));
		q.andQualifier(ExpressionFactory.matchExp("college", this.college));
		return (Queueable) Cayenne.objectForQuery(objectContext, q);
	}

	@SuppressWarnings("unchecked")
	protected Queueable updateRelationShip(Long entityId, String entityIdentifier, List<ReplicatedRecord> result) {
		ReplicationStub stub = findInTransactionGroup(entityId, entityIdentifier);
		if (stub != null) {
			return next.updateRecord(stub, result);
		}
		return findEntityByWillowId(entityId, entityIdentifier);
	}

	private ReplicationStub findInTransactionGroup(Long entityId, String entityIdentifier) {
		List<ReplicationStub> stubs = new ArrayList<ReplicationStub>(group.getAttendanceOrBinaryDataOrBinaryInfo());
		for (ReplicationStub s : stubs) {
			if (entityId.equals(s.getAngelId()) && entityIdentifier.equals(s.getEntityIdentifier())) {
				return s;
			}
		}
		return null;
	}

	private Class<T> getEntityClass(String entityIdentifier) {
		@SuppressWarnings("unchecked")
		Class<T> entityClass = (Class<T>) objectContext.getEntityResolver().getObjEntity(entityIdentifier).getJavaClass();
		return entityClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queueable updateRecord(V stub, List<ReplicatedRecord> result) {

		ReplicatedRecord replRecord = new ReplicatedRecord();
		replRecord.setStatus(Status.SUCCESS);
		replRecord.setStub(toHollow(stub));
		result.add(replRecord);

		T objectToUpdate = null;

		if (stub.getWillowId() != null) {
			objectToUpdate = (T) findEntityByWillowId(stub.getWillowId(), stub.getEntityIdentifier());
			if (objectToUpdate == null) {
				String message = String.format("Cannot find object:%s by willowId:%s", stub.getEntityIdentifier(),
						stub.getWillowId());

				logger.error(message);

				replRecord.setStatus(Status.WILLOWID_NOT_FOUND);
				replRecord.setMessage(message);
			} else {
				if (objectToUpdate.getAngelId() != stub.getAngelId()) {

					String message = String.format("AngelId doesn't match. Got %s while expected %s.", stub.getAngelId(),
							objectToUpdate.getAngelId());

					logger.error(message);

					replRecord.setStatus(Status.ANGELID_NOT_MATCH);
					replRecord.setMessage(message);
				}
			}
		} else {
			if (stub.getAngelId() != null) {
				objectToUpdate = (T) findEntityByAngelId(stub);
				if (objectToUpdate != null) {

					String message = String.format("Have willowId:null but found existing record for entity:%s and angelId:%s",
							stub.getEntityIdentifier(), stub.getAngelId());

					logger.error(message);

					replRecord.setStatus(Status.DANGLING_OBJECT);
					replRecord.setMessage(message);

				} else {
					objectToUpdate = objectContext.newObject(getEntityClass(stub.getEntityIdentifier()));
				}

			} else {
				String message = String.format("Both angelId and willowId are empty for object %s.", stub.getEntityIdentifier());

				logger.error(message);

				replRecord.setStatus(Status.EMPTY_IDS);
				replRecord.setMessage(message);
			}
		}

		if (objectToUpdate != null) {
			try {
				if (stub instanceof DeletedStub) {
					objectContext.deleteObject(objectToUpdate);
				} else {
					updateEntity(stub, objectToUpdate, result);
				}
				
				objectContext.commitChangesToParent();
			} catch (Exception e) {
				logger.error("Failed to commit object.", e);
				objectContext.rollbackChangesLocally();
				replRecord.setStatus(Status.FAILED);
				replRecord.setMessage(e.getMessage());
			}
		}

		return objectToUpdate;
	}

	private static HollowStub toHollow(ReplicationStub stub) {

		HollowStub hollowStub = new HollowStub();
		hollowStub.setEntityIdentifier(stub.getEntityIdentifier());
		hollowStub.setAngelId(stub.getAngelId());

		Date today = new Date();

		hollowStub.setModified(today);
		hollowStub.setCreated(today);

		return hollowStub;
	}

	protected abstract void updateEntity(V stub, T entity, List<ReplicatedRecord> result);
}
