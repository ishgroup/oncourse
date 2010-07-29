package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;

public class ImageTextileRenderer extends AbstractRenderer {

	public ImageTextileRenderer() {
		validator = new ImageTextileValidator();
	}
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		//TODO perform replacement of image tag here
		return tag;
	}

}
