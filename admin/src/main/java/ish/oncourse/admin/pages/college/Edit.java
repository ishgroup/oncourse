package ish.oncourse.admin.pages.college;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.College;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

public class Edit {

	@Property
	private College college;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ICollegeService collegeService;

	void onActivate(Long collegeId) {
		if (college == null) {
			College c = collegeService.findById(collegeId);

			if (c != null) {
				ObjectContext objectContext = cayenneService.newContext();
				this.college = (College) objectContext.localObject(c.getObjectId(), null);
			}
		}
	}
}
