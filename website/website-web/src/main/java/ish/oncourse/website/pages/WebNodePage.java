package ish.oncourse.website.pages;

import ish.oncourse.model.WebNode;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class WebNodePage {
	@Inject
	private Request request;
	
	@Property
	private WebNode node;
	
	void beginRender(){
		node = (WebNode) request.getAttribute("node");
	}
}
