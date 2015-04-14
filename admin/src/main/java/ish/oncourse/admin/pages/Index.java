package ish.oncourse.admin.pages;

import ish.oncourse.model.College;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Index {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
	
	@Inject
	private ICollegeService collegeService;
	
	@Property
	private List<College> colleges;
	
	@Property
	private BeanModel<College> collegeModel;
	
	@Inject
	private BeanModelSource beanModelSource;
	
	@Inject
	private ComponentResources componentResources;

	@Inject
	private PreferenceControllerFactory preferenceControllerFactory;
	
	@Property
	@Persist
	private College college;
	
	@SetupRender
	void setupRender() {
		if (collegeModel == null) {
			collegeModel = beanModelSource.createDisplayModel(College.class, componentResources.getMessages());
			collegeModel.include("id", "name", "lastRemoteAuthentication", "angelVersion");
			collegeModel.get("lastRemoteAuthentication").dataType("DateTime");
			collegeModel.add("action", null);
		}
		this.colleges = collegeService.allColleges();
		Ordering.orderList(this.colleges, Arrays.asList(new Ordering(College.NAME_PROPERTY, SortOrder.ASCENDING_INSENSITIVE)));
	}
	
	public String getLastRemoteAuthentication() {
		Date lastRemoteAuth = college.getLastRemoteAuthentication();
		return lastRemoteAuth != null ? DATE_FORMAT.format(lastRemoteAuth) : "";
	}

	public String getState() {
		PreferenceController preferenceController = preferenceControllerFactory.getPreferenceController(college);
		return preferenceController.getAvetmissStateName();
	}
}
