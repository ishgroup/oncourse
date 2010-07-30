package ish.oncourse.services.textile;

import org.apache.cayenne.ObjectContext;

import ish.oncourse.model.College;
import ish.oncourse.util.ValidationErrors;

public class ImageTextileRenderer extends AbstractRenderer {

	public ImageTextileRenderer() {
		validator = new ImageTextileValidator();
	}
	public String render(String tag, ValidationErrors errors, ObjectContext context, College currentCollege) {
		tag = super.render(tag, errors, context, currentCollege);
		//TODO perform replacement of image tag here
		return tag;
	}

}
