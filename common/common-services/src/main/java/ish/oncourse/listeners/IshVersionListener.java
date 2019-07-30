package ish.oncourse.listeners;

import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.lifecycle.PropertyChangeFilter;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.annotation.PostAdd;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.annotation.PreUpdate;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

public class IshVersionListener {

	@PostAdd(value = Qualification.class)
	public void postAddQualification(Qualification q) {
		q.setIshVersion(-1L);
	}

	@PrePersist(value = Qualification.class)
	public void prePersistQualification(Qualification q) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		if (ishVersion != null) {
			q.setIshVersion(ishVersion);
		}
	}

	@PreUpdate(value = Qualification.class)
	public void preUpdateQualification(Qualification q) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		if (ishVersion != null) {
			if (shouldUpdateIshVersion(q)) {
				q.setIshVersion(ishVersion);
			}
		}
	}

	@PostAdd(value = TrainingPackage.class)
	public void postAddTrainingPackage(TrainingPackage tp) {
		tp.setIshVersion(-1L);
	}

	@PrePersist(value = TrainingPackage.class)
	public void prePersistTrainingPackage(TrainingPackage tp) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		if (ishVersion != null) {
			tp.setIshVersion(ishVersion);
		}
	}

	@PreUpdate(value = TrainingPackage.class)
	public void preUpdateTrainingPackage(TrainingPackage tp) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		if (ishVersion != null) {
			if (shouldUpdateIshVersion(tp)) {
				tp.setIshVersion(ishVersion);
			}
		}
	}

	@PostAdd(value = Module.class)
	public void postAddModule(Module m) {
		m.setIshVersion(-1L);
	}

	@PrePersist(value = Module.class)
	public void prePersistModule(Module m) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		if (ishVersion != null) {
			m.setIshVersion(ishVersion);
		}
	}

	@PreUpdate(value = Module.class)
	public void preUpdateModule(Module m) {
		Long ishVersion = IshVersionHolder.getIshVersion();
		if (ishVersion != null) {
			if (shouldUpdateIshVersion(m)) {
				m.setIshVersion(ishVersion);
			}
		}
	}

	/**
	 * Iterates and compares object properties from changeset with actual
	 * values;
	 * 
	 * @param q
	 *            persistent object
	 * @return true - if object was modified and we need to increase ishVersion,
	 *         false - otherwise.
	 */
	private boolean shouldUpdateIshVersion(Persistent q) {

		Map<String, PropertyChangeFilter.PropertyChange> changes = PropertyChangeFilter.getChangesForObject(q);

		boolean shouldSetIshVersion = false;

		for (Map.Entry<String, PropertyChangeFilter.PropertyChange> change : changes.entrySet()) {
			PropertyChangeFilter.PropertyChange propChange = change.getValue();
			if (ObjectUtils.notEqual(propChange.getOldValue(), propChange.getNewValue())) {
				shouldSetIshVersion = true;
				break;
			}
		}

		return shouldSetIshVersion;
	}
}
