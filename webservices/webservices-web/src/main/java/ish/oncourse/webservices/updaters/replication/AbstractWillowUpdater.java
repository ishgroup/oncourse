package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;

public abstract class AbstractWillowUpdater<V extends ReplicationStub, T extends Queueable> implements IWillowUpdater<V> {
	
	private static final Logger logger = Logger.getLogger(AbstractWillowUpdater.class);

	protected College college;
	
	private ObjectContext ctx;
	
	@SuppressWarnings("rawtypes")
	private IWillowUpdater next;

	public AbstractWillowUpdater(College college, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		this.college = college;
		this.ctx = college.getObjectContext();
		this.next = next;
	}

	private T findMatchingEntity(ReplicationStub stub) {
		return DataObjectUtils.objectForPK(ctx, getEntityClass(stub), stub.getWillowId());
	}
	
	@SuppressWarnings("unchecked")
	protected Queueable updateRelatedEntity(ReplicationStub stub, List<ReplicatedRecord> relationStubs) {
		if (stub instanceof HollowStub) {
			return findMatchingEntity(stub);
		}
		else {
			relationStubs.addAll(next.updateRecord(stub));
			return findMatchingEntity(stub);
		}
	}

	private Class<T> getEntityClass(ReplicationStub stub) {
		@SuppressWarnings("unchecked")
		Class<T> entityClass = (Class<T>) ctx.getEntityResolver().getObjEntity(stub.getEntityIdentifier()).getClass();
		return entityClass;
	}

	@Override
	public List<ReplicatedRecord> updateRecord(V stub) {
		
		List<ReplicatedRecord> respStubs = new LinkedList<ReplicatedRecord>();
		
		ReplicatedRecord record = new ReplicatedRecord();
		
		HollowStub hollowStub = new HollowStub();
		hollowStub.setEntityIdentifier(stub.getEntityIdentifier());
		hollowStub.setAngelId(stub.getAngelId());
		hollowStub.setModified(new Date());
		
		record.setStub(hollowStub);
		
		respStubs.add(record);
		
		try {
			if (stub instanceof DeletedStub) {
				T deletedEntity = findMatchingEntity(stub);
				ctx.deleteObject(deletedEntity);
				ctx.commitChanges();
			}
			else {
				T entity = findMatchingEntity(stub);
				
				if (entity == null) {
					entity = ctx.newObject(getEntityClass(stub));
					hollowStub.setCreated(new Date());
				}
				
				updateEntity(stub, entity, respStubs);
				
				ctx.commitChanges();
				
				hollowStub.setWillowId(entity.getId());
				record.setStatus(Status.SUCCESS);
			}
		}
		catch (Exception e) {
			logger.error("Failed to update record.", e);
			record.setStatus(Status.FAILURE);
		}
		
		return respStubs;
	}

	protected abstract void updateEntity(V stub, T entity, List<ReplicatedRecord> relationStubs);
}
