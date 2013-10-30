package ish.oncourse.portal.pages;


import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;


import java.util.Date;

/**
 * User: artem
 * Date: 10/29/13
 * Time: 10:33 AM
 */
public class Profile {

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IAuthenticationService authenticationService;

    @Persist
    @Property
    private Contact contact;

  @SetupRender
  void setupRender()
    {
        ObjectContext context = cayenneService.newContext();

        contact = context.localObject(authenticationService.getUser());



    }

}
