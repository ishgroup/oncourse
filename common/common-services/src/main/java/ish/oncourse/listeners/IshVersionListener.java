package ish.oncourse.listeners;

import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;

import java.util.Map;

import org.apache.cayenne.Persistent;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.annotation.PreUpdate;
import org.apache.cayenne.lifecycle.changeset.ChangeSet;
import org.apache.cayenne.lifecycle.changeset.ChangeSetFilter;
import org.apache.cayenne.lifecycle.changeset.PropertyChange;

public class IshVersionListener {

	@PrePersist(value = Qualification.class)
	public void prePersistQualification(Qualification q) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		q.setIshVersion(ishVersion);
	}

	@PreUpdate(value = Qualification.class)
	public void preUpdateQualification(Qualification q) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		if (shouldUpdateIshVersion(q)) {
			q.setIshVersion(ishVersion);
		}
	}

	@PrePersist(value = TrainingPackage.class)
	public void prePersistTrainingPackage(TrainingPackage tp) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		tp.setIshVersion(ishVersion);
	}

	@PreUpdate(value = TrainingPackage.class)
	public void preUpdateTrainingPackage(TrainingPackage tp) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		if (shouldUpdateIshVersion(tp)) {
			tp.setIshVersion(ishVersion);
		}
	}

	@PrePersist(value = Module.class)
	public void prePersistTrainingPackage(Module m) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		m.setIshVersion(ishVersion);
	}

	@PreUpdate(value = Module.class)
	public void preUpdateModule(Module m) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		if (shouldUpdateIshVersion(m)) {
			m.setIshVersion(ishVersion);
		}
	}

	/**
	 * Iterates and compares object properties from changeset with actual values;
	 * @param q persistent object
	 * @return true - if object was modified and we need to increase ishVersion, false - otherwise.
	 */
	private boolean shouldUpdateIshVersion(Persistent q) {

		ChangeSet changeSet = ChangeSetFilter.preCommitChangeSet();
		Map<String, PropertyChange> changes = changeSet.getChanges(q);

		boolean shouldSetIshVersion = false;

		for (Map.Entry<String, PropertyChange> change : changes.entrySet()) {
			PropertyChange propChange = change.getValue();
			if (!propChange.getNewValue().equals(propChange.getOldValue())) {
				shouldSetIshVersion = true;
				break;
			}
		}

		return shouldSetIshVersion;
	}
}
