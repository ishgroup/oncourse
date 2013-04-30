package ish.oncourse.admin.pages.college;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Preferences {
	
	@Property
	private College college;
	
	@Property
	private String currentKey;
	
	@Property
	private String newPreferenceKey;
	
	@Property
	private String newPreferenceValue;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
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
		
		Date now = new Date();
		ObjectContext context = cayenneService.newContext();
			
		if (isNew) {
			College college = (College) context.localObject(this.college.getObjectId(), null);
			if (college != null && StringUtils.trimToNull(newPreferenceKey) != null 
					&& StringUtils.trimToNull(newPreferenceValue) != null) {
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
		ObjectContext context = cayenneService.sharedContext();
		
		Map<String, String> prefs = new TreeMap<>();
		
		College college = (College) context.localObject(this.college.getObjectId(), null);
		
		SelectQuery q = new SelectQuery(Preference.class, ExpressionFactory.matchExp(
				Preference.COLLEGE_PROPERTY, college));
		q.setCacheStrategy(QueryCacheStrategy.NO_CACHE);
		List<Preference> prefList = context.performQuery(q); 
		
		for (Preference pref : prefList) {
			if (pref.getName() != null) {
				prefs.put(pref.getName(), pref.getValueString());
			}
		}
		
		return prefs;
	}
	
	public String getCurrentValue() {
		return preferences.get(currentKey);
	}
	
	public void setCurrentValue(String value) {
		if (currentKey != null) {
			preferences.put(currentKey, value);
		}
	}

}
