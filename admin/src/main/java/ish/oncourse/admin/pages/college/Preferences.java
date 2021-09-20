package ish.oncourse.admin.pages.college;

import ish.oncourse.util.PreferenceUtil;
import ish.oncourse.model.College;
import ish.oncourse.model.Settings;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Preferences {
	
	@Property
	private College college;
	
	@Property
	private String currentKey;
	
	@Property
	private String newSettingKey;
	
	@Property
	private String newSettingValue;
	
	@Property
	@Persist(PersistenceConstants.SESSION)
	private Map<String, String> settings;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	private boolean isNew;
	
	@SetupRender
	void setupRender() {
		this.settings = initSettings();
	}
	
	void onActivate(Long id) {
		this.college = collegeService.findById(id);
	}

	Object onPassivate() {
		return college.getId();
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
		ObjectContext context = cayenneService.newContext();
			
		if (isNew) {
			College college = context.localObject(this.college);
			if (college != null && StringUtils.trimToNull(newSettingKey) != null 
					&& StringUtils.trimToNull(newSettingValue) != null) {
				PreferenceUtil.createSetting(context, college, newSettingKey, newSettingValue);
			}
		}
		else {
			for (Settings pref : getSettings()) {
				Settings p = context.localObject(pref);
				if (p != null) {
					p.setValue(settings.get(p.getName()));
				}
			}
		}
		
		context.commitChanges();
	}
	
	private Map<String, String> initSettings() {
		
		Map<String, String> prefs = new TreeMap<>();
		
		for (Settings pref : getSettings()) {
			if (pref.getName() != null) {
				prefs.put(pref.getName(), pref.getValue());
			}
		}
		
		return prefs;
	}
	
	private List<Settings> getSettings() {
		ObjectContext context = cayenneService.newContext();
		College college = context.localObject(this.college);

		return  ObjectSelect.query(Settings.class).
				where(Settings.COLLEGE.eq(college)).
				select(context);
	}
	
	public String getCurrentValue() {
		return settings.get(currentKey);
	}
	
	public void setCurrentValue(String value) {
		if (currentKey != null) {
			settings.put(currentKey, value);
		}
	}

}
