package ish.oncourse.enrol.pages;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Index {

	@Inject
	private Request request;

	String onActivate(){
		if (request.getPath().equals("/"))
			return Checkout.class.getSimpleName();
		else
			return "ui/PageNotFound";
	}
}
