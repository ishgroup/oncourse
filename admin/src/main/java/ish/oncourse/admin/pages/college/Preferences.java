package ish.oncourse.admin.pages.college;

import java.util.Date;
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
	private String newPreferenceKey;
	
	@Property
	private String newPreferenceValue;
	
	@Property
	@Persist
	private Map<String, String> preferences;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	private boolean isNew;
	
	@SetupRender
	void setupRender() {
		this.preferences = initPreferences();
	}
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
	}
	
	@OnEvent(component="save", value="selected")
	void submitSave() {
		this.isNew = false;
	}
	
	@OnEvent(component="add", value="selected")
	void submitNew() {
		this.isNew = true;
	}
	
	@OnEvent(component = "prefForm", value="success")
	void submitted() {
		
		Date now = new Date();
		ObjectContext context = cayenneService.newContext();
			
		if (isNew) {
			College college = (College) context.localObject(this.college.getObjectId(), null);
			if (college != null) {
				Preference p = context.newObject(Preference.class);
				p.setCollege(college);
				p.setName(newPreferenceKey);
				p.setValueString(newPreferenceValue);
				p.setCreated(now);
				p.setModified(now);
			}
		}
		else {
			for (Preference pref : college.getPreferences()) {
				Preference p = (Preference) context.localObject(pref.getObjectId(), null);
				
				if (p != null) {
					p.setValueString(preferences.get(p.getName()));
				}
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
