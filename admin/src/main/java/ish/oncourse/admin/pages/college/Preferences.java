package ish.oncourse.admin.pages.college;

import java.util.HashMap;
import java.util.Map;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Preferences {
	
	@Property
	@Persist
	private College college;
	
	@Property
	private String currentKey;
	
	@Property
	@Persist
	private Map<String, String> preferences;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	@SetupRender
	void setupRender() {
		this.preferences = initPreferences();
	}
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
	}
	
	@OnEvent(component = "prefForm", value="success")
	void submitted() {
		ObjectContext context = cayenneService.newContext();
			
		for (Preference pref : college.getPreferences()) {
			Preference p = (Preference) context.localObject(pref.getObjectId(), null);
			
			if (p != null) {
				p.setValueString(preferences.get(p.getName()));
			}
		}
		
		context.commitChanges();
	}
	
	private Map<String, String> initPreferences() {
		Map<String, String> prefs = new HashMap<String, String>();
		
		for (Preference pref : college.getPreferences()) {
			prefs.put(pref.getName(), pref.getValueString());
		}
		
		return prefs;
	}
	
	public String getCurrentValue() {
		return preferences.get(currentKey);
	}
	
	public void setCurrentValue(String value) {
		preferences.put(currentKey, value);
	}

}
