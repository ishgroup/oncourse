package ish.oncourse.portal.components;

import java.util.List;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.binary.IBinaryDataService;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Materials {

	@Parameter
	@Property
	private CourseClass courseClass;

	@Inject
	private IBinaryDataService binaryDataService;

	@Property
	private List<BinaryInfo> materials;

	@Property
	private BinaryInfo material;

	@SetupRender
	boolean setupRender() {
		if (courseClass == null) {
			return false;
		}

		materials = binaryDataService.getAttachedFiles(courseClass.getId(), CourseClass.class.getSimpleName(), false);
		return true;
	}

}
