package ish.oncourse.ui.pages;


import ish.oncourse.components.ISHCommon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class SiteNotFound extends ISHCommon {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	private Request request;
	
	
}
