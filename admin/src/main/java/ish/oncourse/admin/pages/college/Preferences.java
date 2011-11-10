package ish.oncourse.admin.pages.college;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Preferences {
	
	@Property
	private College college;
	
	@Property
	private String currentKey;
	
	@Property
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
		
		for (String key : preferences.keySet()) {
			SelectQuery query = new SelectQuery(Preference.class);
			Expression exp = ExpressionFactory.matchExp(Preference.NAME_PROPERTY, preferences.get(key));
			query.setQualifier(exp);
			
			List<Preference> prefs = context.performQuery(query);
			
			if (!prefs.isEmpty()) {
				
				Preference currentPref = prefs.get(0);
				if (preferences.get(key).equals(currentPref.getName())) {
					currentPref.setName(preferences.get(key));
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

}
