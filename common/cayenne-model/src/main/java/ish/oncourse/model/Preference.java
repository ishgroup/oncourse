package ish.oncourse.model;

import com.sun.tools.javac.util.List;
import ish.oncourse.model.auto._Preference;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.validation.ValidationResult;

public class Preference extends _Preference implements Queueable {

	private static final long serialVersionUID = 8309390847931508840L;

	public static final String STORAGE_BUCKET_NAME = "storage.bucket";
	public static final String STORAGE_ACCESS_ID = "storage.access.id";
	public static final String STORAGE_ACCESS_KEY = "storage.access.key";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return (getWebSite() == null) || (List.of(STORAGE_BUCKET_NAME, STORAGE_ACCESS_ID, STORAGE_ACCESS_KEY).contains(getName()));
	}

	@Override
	public void validateForInsert(ValidationResult validationResult) {
		Preference preference = null;
		if ((getCollege() != null) && (!getCollege().getObjectId().isTemporary())) {
			preference = ObjectSelect.query(Preference.class)
					.where(COLLEGE.eq(getCollege()))
					.and(NAME.eq(getName()))
					.selectFirst(objectContext);
		}
		boolean isPresentedInDatabase = (preference != null) && (preference.getWebSite() == null);
		if ((isPresentedInDatabase) || (isPresentedInUncommitedObjects())) {
			String propertyName = COLLEGE.getName() + ":" + NAME.getName();
			validationResult.addFailure(ValidationFailure.validationFailure(
					this, propertyName, "Entity with this data exists."
			));
		}
		super.validateForInsert(validationResult);
	}

	/**
	 * Matches all preferences in ObjectStore with this object
	 * @return result of matching with preferences, which presented in ObjectStore
	 */
	private boolean isPresentedInUncommitedObjects() {
		return ((DataContext) objectContext).uncommittedObjects().stream()
				.filter(item -> item instanceof Preference)
				.anyMatch(item -> {
					Preference uncommitedPreference = (Preference) item;
					boolean hasSameColleges = nullSafeEquals(getCollege(), uncommitedPreference.getCollege());
					boolean hasSameNames = nullSafeEquals(getName(), uncommitedPreference.getName());
					boolean isThisObject = this.equals(uncommitedPreference);
					return (hasSameColleges) && (hasSameNames) && (uncommitedPreference.getWebSite() == null) && (!isThisObject);
				});
	}

	private boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}
}
