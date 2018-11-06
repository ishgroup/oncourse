package ish.oncourse.model;

import ish.oncourse.model.auto._Preference;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.persistence.Preferences;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.validation.ValidationResult;

public class Preference extends _Preference implements Queueable {

	private static final long serialVersionUID = 8309390847931508840L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return getWebSite() == null && !Preferences.SERVICES_SECURITYKEY.equals(getName());
	}

	@Override
	public void validateForInsert(ValidationResult validationResult) {
		Preference preference = ObjectSelect.query(Preference.class)
				.where(COLLEGE.eq(getCollege()))
				.and(NAME.eq(getName()))
				.selectFirst(objectContext);
		boolean isPresentedInDatabase = (preference != null) && (preference.getWebSite() == null);
		if ((isPresentedInDatabase) || (isPresentedInUncommitedObjects())) {
			String propertyName = COLLEGE.getName() + ":" + NAME.getName();
			validationResult.addFailure(ValidationFailure.validationFailure(
					this, propertyName, "Entity with this data is exists."
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
					boolean hasSameColleges = getCollege().equals(uncommitedPreference.getCollege());
					boolean hasSameNames = getName().equals(uncommitedPreference.getName());
					boolean isThisObject = this.equals(uncommitedPreference);
					return (hasSameColleges) && (hasSameNames) && (uncommitedPreference.getWebSite() == null) && (!isThisObject);
				});
	}
}
