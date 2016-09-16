package ish.oncourse.portal.pages;


import ish.oncourse.portal.services.IPortalService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class History {
	
	private static final String ACTIVE_CLASS = " active";
	private static final String FINANCE_TAB = "finance";
	private static final String ENROLMENTS_TAB = "enrolments";
	private static final String APPLICATION_TAB = "application";
	
    @Inject
    private IPortalService portalService;

    @InjectPage
    private Index indexPage;

	@Property
	private String financeClass = ACTIVE_CLASS;
	
	@Property
	private String enrolmentsClass = StringUtils.EMPTY;
	
	@Property
	private String applicationClass = StringUtils.EMPTY;

	Object onActivate(String tab) {
		financeClass = StringUtils.EMPTY;
		if (tab.startsWith(APPLICATION_TAB)) {
			applicationClass = ACTIVE_CLASS;
		} else if (tab.startsWith(ENROLMENTS_TAB)) {
			enrolmentsClass = ACTIVE_CLASS;
		} else if (tab.startsWith(FINANCE_TAB)) {
			financeClass = ACTIVE_CLASS;
		} else {
			return indexPage;
		}
		return true;
	}
	
    public boolean isStudent(){
        return portalService.getContact().getStudent() != null;
    }

}
