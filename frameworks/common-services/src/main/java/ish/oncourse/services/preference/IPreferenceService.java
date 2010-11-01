package ish.oncourse.services.preference;

import ish.oncourse.model.Preference;

public interface IPreferenceService {
	Preference getPreferenceByKey(String key);
}
