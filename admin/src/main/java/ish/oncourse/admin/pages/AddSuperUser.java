package ish.oncourse.admin.pages;

import ish.oncourse.model.WillowUser;
import ish.oncourse.services.persistence.ICayenneService;
import ish.util.SecurityUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class AddSuperUser {
	
	@Property
	private List<WillowUser> cmsSuperUsers;
	
	@Property
	private WillowUser currentUser;
	
	@Property
	private String newUserEmailValue;
	
	@Property
	private String newUserPasswordValue;
	
	@Property
	private String newUserFirstNameValue;
	
	@Property
	private String newUserLastNameValue;
	
	@Inject
	private ICayenneService cayenneService;
	
	@SetupRender
	void setupRender() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		this.cmsSuperUsers = ObjectSelect.query(WillowUser.class).
				where(WillowUser.COLLEGE.isNull()).
				select(context);
	}
	
	@OnEvent(component="cmsUsersForm", value="success")
	void addSuperUser() throws UnsupportedEncodingException {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		WillowUser user = context.newObject(WillowUser.class);
		user.setCollege(null);
		user.setEmail(newUserEmailValue);
		final String hashedPassword = SecurityUtil.hashPassword(newUserPasswordValue);
		user.setPassword(newUserPasswordValue);//TODO: migrate when found logic which will update old passwords
		user.setFirstName(newUserFirstNameValue);
		user.setLastName(newUserLastNameValue);
		
		context.commitChanges();
	}
	
	Object onActionFromDeleteUser(String email) {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		WillowUser user = ObjectSelect.query(WillowUser.class).
				where(WillowUser.EMAIL.eq(email)).
				selectOne(context);
		context.deleteObject(user);
		context.commitChanges();
		
		return null;
	}

}
