package ish.oncourse.portal.pages;


import ish.oncourse.portal.services.IPortalService;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;



public class History {

    @Inject
    private IPortalService portalService;

    @InjectPage
    private PageNotFound pageNotFound;


    public boolean isStudent(){
        return portalService.getContact().getStudent() != null;
    }

}
