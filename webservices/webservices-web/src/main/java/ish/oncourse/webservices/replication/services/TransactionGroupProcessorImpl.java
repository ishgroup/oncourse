package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.Preference;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tag;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.WaitingList;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.replication.updaters.AttendanceUpdater;
import ish.oncourse.webservices.replication.updaters.BinaryInfoRelationUpdater;
import ish.oncourse.webservices.replication.updaters.CertificateOutcomeUpdater;
import ish.oncourse.webservices.replication.updaters.CertificateUpdater;
import ish.oncourse.webservices.replication.updaters.ContactUpdater;
import ish.oncourse.webservices.replication.updaters.CourseClassUpdater;
import ish.oncourse.webservices.replication.updaters.CourseModuleUpdater;
import ish.oncourse.webservices.replication.updaters.CourseUpdater;
import ish.oncourse.webservices.replication.updaters.DiscountConcessionTypeUpdater;
import ish.oncourse.webservices.replication.updaters.DiscountUpdater;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.PreferenceUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.SessionTutorUpdater;
import ish.oncourse.webservices.replication.updaters.StudentUpdater;
import ish.oncourse.webservices.replication.updaters.TagUpdater;
import ish.oncourse.webservices.replication.updaters.TaggableTagUpdater;
import ish.oncourse.webservices.replication.updaters.TutorRoleUpdater;
import ish.oncourse.webservices.replication.updaters.TutorUpdater;
import ish.oncourse.webservices.replication.updaters.WaitingListUpdater;
import ish.oncourse.webservices.services.ICollegeRequestService;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;


public class TransactionGroupProcessorImpl implements ITransactionGroupProcessor {
	
	private static final Logger logger = Logger.getLogger(TransactionGroupProcessorImpl.class);

	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeRequestService collegeRequestService;

	private Map<String, IWillowUpdater> updaterMap = new HashMap<String, IWillowUpdater>();
	
	public TransactionGroupProcessorImpl() {
		updaterMap.put(getEntityName(Attendance.class), new AttendanceUpdater());
		updaterMap.put(getEntityName(BinaryInfoRelation.class), new BinaryInfoRelationUpdater());
		updaterMap.put(getEntityName(Contact.class), new ContactUpdater());
		updaterMap.put(getEntityName(Course.class), new CourseUpdater());
		updaterMap.put(getEntityName(CourseClass.class), new CourseClassUpdater());
		updaterMap.put(getEntityName(CourseModule.class), new CourseModuleUpdater());
		updaterMap.put(getEntityName(Certificate.class), new CertificateUpdater());
		updaterMap.put(getEntityName(CertificateOutcome.class), new CertificateOutcomeUpdater());
		updaterMap.put(getEntityName(Discount.class), new DiscountUpdater());
		updaterMap.put(getEntityName(DiscountConcessionType.class), new DiscountConcessionTypeUpdater());
		updaterMap.put(getEntityName(Preference.class), new PreferenceUpdater());
		updaterMap.put("SessionCourseClassTutor", new SessionTutorUpdater());
		updaterMap.put(getEntityName(Student.class), new StudentUpdater());
		updaterMap.put(getEntityName(Tag.class), new TagUpdater());
		updaterMap.put(getEntityName(TaggableTag.class), new TaggableTagUpdater());
		updaterMap.put(getEntityName(Tutor.class), new TutorUpdater());
		updaterMap.put("CourseClassTutor", new TutorRoleUpdater());
		updaterMap.put(getEntityName(WaitingList.class), new WaitingListUpdater());
	}
	
	@Override
	public List<ReplicatedRecord> processGroup(TransactionGroup group) {
		ObjectContext ctx = cayenneService.newNonReplicatingContext();

		List<ReplicatedRecord> result = new ArrayList<ReplicatedRecord>();

		while (!group.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
			ReplicationStub stub = group.getAttendanceOrBinaryDataOrBinaryInfo().remove(0);
			updateRecord(ctx, stub, group, result);
		}

		return result;
	}
	
	private Queueable updateRecord(ObjectContext objectContext, ReplicationStub stub, TransactionGroup group, List<ReplicatedRecord> result) {
		ReplicatedRecord replRecord = new ReplicatedRecord();
		replRecord.setStatus(Status.SUCCESS);
		replRecord.setStub(toHollow(stub));
		result.add(replRecord);

		Queueable objectToUpdate = null;

		if (stub.getWillowId() != null) {
			objectToUpdate = findEntityByWillowId(objectContext, stub.getWillowId(), stub.getEntityIdentifier());
			if (objectToUpdate == null) {
				String message = String.format("Cannot find object:%s by willowId:%s", stub.getEntityIdentifier(),
						stub.getWillowId());

				logger.error(message);

				replRecord.setStatus(Status.WILLOWID_NOT_FOUND);
				replRecord.setMessage(message);
			} else {
				if (stub.getAngelId() == null || !stub.getAngelId().equals(objectToUpdate.getAngelId())) {

					String message = String.format("AngelId doesn't match. Got %s while expected %s.", stub.getAngelId(),
							objectToUpdate.getAngelId());

					logger.error(message);
					
					objectToUpdate = null;
					replRecord.setStatus(Status.ANGELID_NOT_MATCH);
					replRecord.setMessage(message);
				}
			}
		} else {
			if (stub.getAngelId() != null) {
				objectToUpdate = findEntityByAngelId(objectContext, stub.getAngelId(), stub.getEntityIdentifier());
				
				if (objectToUpdate != null) {

					String message = String.format("Have willowId:null but found existing record for entity:%s and angelId:%s",
							stub.getEntityIdentifier(), stub.getAngelId());

					logger.error(message);
					
					objectToUpdate = null;
					replRecord.setStatus(Status.DANGLING_OBJECT);
					replRecord.setMessage(message);
				} else {
					objectToUpdate = objectContext.newObject(getEntityClass(objectContext, stub.getEntityIdentifier()));
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
					String key = stub.getEntityIdentifier();
					IWillowUpdater updater = updaterMap.get(key);

					if (updater == null) {
						throw new UpdaterNotFoundException(String.format("Updater not found for entity with key:%s", key), key);
					}
					
					College college = (College) objectContext.localObject(collegeRequestService.getRequestingCollege().getObjectId(), null);
					objectToUpdate.setCollege(college);
					
					updater.updateEntityFromStub(stub, objectToUpdate, new RelationShipCallbackImpl(objectContext, group, result));
				}
				
				objectContext.commitChangesToParent();
				replRecord.getStub().setWillowId(objectToUpdate.getId());
			} catch (Exception e) {
				logger.error("Failed to commit object.", e);
				objectContext.rollbackChanges();
				replRecord.setStatus(Status.FAILED);
				replRecord.setMessage(e.getMessage());
			}
		}

		return objectToUpdate;
	}
	
	private Class<? extends Queueable> getEntityClass(ObjectContext objectContext, String entityIdentifier) {
		@SuppressWarnings("unchecked")
		Class<? extends Queueable> entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver().getObjEntity(entityIdentifier).getJavaClass();
		return entityClass;
	}
	
	private Queueable findEntityByWillowId(ObjectContext objectContext, Long entityId, String entityIdentifier) {
		SelectQuery q = new SelectQuery(getEntityClass(objectContext, entityIdentifier));
		q.andQualifier(ExpressionFactory.matchDbExp("id", entityId));
		q.andQualifier(ExpressionFactory.matchExp("college", collegeRequestService.getRequestingCollege()));
		return (Queueable) Cayenne.objectForQuery(objectContext, q);
	}

	private Queueable findEntityByAngelId(ObjectContext objectContext, Long entityId, String entityIdentifier) {
		SelectQuery q = new SelectQuery(getEntityClass(objectContext, entityIdentifier));
		q.andQualifier(ExpressionFactory.matchDbExp("angelId", entityId));
		q.andQualifier(ExpressionFactory.matchExp("college", collegeRequestService.getRequestingCollege()));
		return (Queueable) Cayenne.objectForQuery(objectContext, q);
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
	
	private class RelationShipCallbackImpl implements RelationShipCallback {

		private ObjectContext ctx;

		private List<ReplicatedRecord> result;

		private TransactionGroup group;

		public RelationShipCallbackImpl(ObjectContext ctx, TransactionGroup group, List<ReplicatedRecord> result) {
			super();
			this.ctx = ctx;
			this.group = group;
			this.result = result;
		}

		/**
		 * @see ish.oncourse.server.replication.updater.RelationShipCallback#updateRelationShip(java.lang.Long, java.lang.Class)
		 */
		@SuppressWarnings("unchecked")
		public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
			String entityIdentifier = getEntityName(clazz);

			ReplicationStub stub = findInTransactionGroup(entityId, entityIdentifier);

			M obj = null;

			if (stub != null) {
				obj = (M) updateRecord(ctx, stub, group, result);
			} else {
				obj = (M) findEntityByAngelId(ctx, entityId, entityIdentifier);
			}

			return obj;
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
	}

}
