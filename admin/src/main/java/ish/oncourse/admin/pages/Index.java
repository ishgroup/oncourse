package ish.oncourse.admin.pages;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;

import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

public class Index {
	
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
	
	@Property
	@Persist
	private College college;
	
	@SetupRender
	void setupRender() {
		if (collegeModel == null) {
			collegeModel = beanModelSource.createDisplayModel(College.class, componentResources.getMessages());
			collegeModel.include("id", "name", "ipAddress", "angelVersion");
			collegeModel.add("action", null);
		}
		this.colleges = collegeService.allColleges();
	}
}
