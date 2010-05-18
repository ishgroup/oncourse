package ish.oncourse.services.college;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Scope;

import ish.oncourse.model.College;
import ish.oncourse.services.host.IWebHostNameService;

@Scope("perthread")
public class CollegeService implements ICollegeService {

	@Inject
	private IWebHostNameService webHostNameService;

	public College getCurrentCollege() {
		return webHostNameService.getCurrentWebHostName().getCollege();
	}

}
