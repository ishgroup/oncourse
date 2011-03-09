package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;

public abstract class AbstractWillowUpdater<V extends ReplicationStub, T extends Queueable> implements IWillowUpdater<V> {

	private static final Logger logger = Logger.getLogger(AbstractWillowUpdater.class);

	private College college;

	@SuppressWarnings("rawtypes")
	private IWillowUpdater next;

	private IWillowQueueService queueService;

	public AbstractWillowUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		this.college = college;
		this.queueService = queueService;
		this.next = next;
	}

	@SuppressWarnings("unchecked")
	private T findMatchingEntity(ReplicationStub stub) {
		return (T) queueService.findEntityByWillowId(stub.getEntityIdentifier(), stub.getWillowId());
	}
	
	protected College getCollege(ObjectContext ctx) {
		return (College) ctx.localObject(college.getObjectId(), null);
	}

	@SuppressWarnings("unchecked")
	protected Queueable updateRelatedEntity(ObjectContext ctx, ReplicationStub stub, List<ReplicatedRecord> relationStubs) {
		Queueable entity = null;
		
		if (stub instanceof HollowStub) {
			entity = findMatchingEntity(stub);
		} else {
			relationStubs.addAll(next.updateRecord(stub));
			entity = findMatchingEntity(stub);
		}
		
		return (Queueable) ctx.localObject(entity.getObjectId(), null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReplicatedRecord> updateRecord(V stub) {

		List<ReplicatedRecord> respStubs = new LinkedList<ReplicatedRecord>();

		ReplicatedRecord record = new ReplicatedRecord();
		respStubs.add(record);

		HollowStub hollowStub = new HollowStub();
		hollowStub.setEntityIdentifier(stub.getEntityIdentifier());
		hollowStub.setAngelId(stub.getAngelId());

		Date today = new Date();

		hollowStub.setModified(today);
		hollowStub.setCreated(today);

		record.setStub(hollowStub);

		T objectToUpdate = null;

		if (stub.getWillowId() != null) {
			objectToUpdate = findMatchingEntity(stub);
			if (objectToUpdate == null) {
				record.setStatus(Status.FAILURE);
				String message = String.format("Cannot find object:%s by willowId:%s", stub.getEntityIdentifier(), stub.getWillowId());
				logger.error(message);
				record.setMessage(message);
			} else {
				if (objectToUpdate.getAngelId() != stub.getAngelId()) {
					record.setStatus(Status.FAILURE);
					String message = String.format("AngelId doesn't match. Got %s while expected %s.", stub.getAngelId(),
							objectToUpdate.getAngelId());
					logger.error(message);
					record.setMessage(message);
				}
			}
		} else {
			if (stub.getAngelId() != null) {
				objectToUpdate = (T) queueService.findEntityByAngelId(stub.getEntityIdentifier(), stub.getAngelId());
				if (objectToUpdate != null) {
					record.setStatus(Status.FAILURE);
					String message = String.format("Have willowId:null but found existing record for entity:%s and angelId:%s", stub.getEntityIdentifier(), stub.getAngelId());
					logger.error(message);
					record.setMessage(message);
				} else {
					objectToUpdate = queueService.createNew((Class<T>) queueService.getEntityClass(stub.getEntityIdentifier()));
				}

			} else {
				record.setStatus(Status.FAILURE);
				String message = String.format("Both angelId and willowId are empty for object %s.", stub.getEntityIdentifier());
				logger.error(message);
				record.setMessage(message);
			}
		}

		if (record.getStatus() != Status.FAILURE && objectToUpdate != null) {
			ObjectContext ctx = objectToUpdate.getObjectContext();
			try {
				if (stub instanceof DeletedStub) {
					ctx.deleteObject(objectToUpdate);
					ctx.commitChanges();
				} else {
					updateEntity(stub, objectToUpdate, respStubs);
					ctx.commitChanges();
					hollowStub.setWillowId(objectToUpdate.getId());
					record.setStatus(Status.SUCCESS);
				}
			} catch (Exception e) {
				logger.error("Failed to update record.", e);
				record.setMessage(e.getMessage());
				record.setStatus(Status.FAILURE);
			}
		}

		return respStubs;
	}

	protected abstract void updateEntity(V stub, T entity, List<ReplicatedRecord> relationStubs);
}
