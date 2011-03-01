package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.RecordStatus;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.ArrayList;
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
	protected Queueable updateRelatedEntity(List<HollowStub> relationStubs, ReplicationStub stub) {
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
	public List<HollowStub> updateRecord(V stub) {
		
		List<HollowStub> respStubs = new ArrayList<HollowStub>();
		
		HollowStub hollowStub = new HollowStub();
		hollowStub.setEntityIdentifier(stub.getEntityIdentifier());
		hollowStub.setAngelId(stub.getAngelId());
		hollowStub.setAction(stub.getAction());
		
		respStubs.add(hollowStub);
		
		ObjectContext ctx = college.getObjectContext();

		try {
			switch (stub.getAction()) {
			case CREATE:
			{
				T newEntity = ctx.newObject(getEntityClass(stub));
				
				List<HollowStub> relStubs = updateEntity(stub, newEntity);
				respStubs.addAll(relStubs);
				
				ctx.commitChanges();
				
				hollowStub.setWillowId(newEntity.getId());
				
				break;
			}
			case UPDATE:
			{
				T updatedEntity = findMatchingEntity(stub);
				
				List<HollowStub> relStubs = updateEntity(stub, updatedEntity);
				respStubs.addAll(relStubs);
				
				ctx.commitChanges();
				
				hollowStub.setWillowId(updatedEntity.getId());
				break;
			}
			case DELETE:
				T deletedEntity = findMatchingEntity(stub);
				
				ctx.deleteObject(deletedEntity);
				ctx.commitChanges();
				
				break;
			default:
				throw new IllegalArgumentException("ReplicationStub with null action is not allowed.");
			}

			hollowStub.setRecordStatus(RecordStatus.SUCCESS);

		} catch (Exception e) {
			logger.error("Failed to replicate record.", e);
			hollowStub.setRecordStatus(RecordStatus.FAILURE);
		}

		return respStubs;
	}

	protected abstract List<HollowStub> updateEntity(V stub, T entity);
}
