package ish.oncourse.ui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import ish.oncourse.model.WebBlock;

public class WebBlockDisplay {

	@Property
	@Parameter
	private WebBlock displayedBlock;
	
	public String getContent() {
		return displayedBlock.getContent();
	}
}
