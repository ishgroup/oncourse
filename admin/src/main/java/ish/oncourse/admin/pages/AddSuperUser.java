package ish.oncourse.admin.pages;

import ish.oncourse.model.WillowUser;
import ish.oncourse.services.persistence.ICayenneService;
import ish.util.SecurityUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class AddSuperUser {
	
	@SuppressWarnings("all")
	@Property
	private List<WillowUser> cmsSuperUsers;
	
	@SuppressWarnings("all")
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
	
	@SuppressWarnings("unchecked")
	@SetupRender
	void setupRender() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		Expression exp = ExpressionFactory.matchExp(WillowUser.COLLEGE_PROPERTY, null);
		this.cmsSuperUsers = context.performQuery(new SelectQuery(WillowUser.class, exp));
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
		
		Expression exp = ExpressionFactory.matchDbExp(WillowUser.EMAIL_PROPERTY, email);
		SelectQuery query = new SelectQuery(WillowUser.class, exp);
		WillowUser user = (WillowUser) Cayenne.objectForQuery(context, query);
		context.deleteObject(user);
		context.commitChanges();
		
		return null;
	}

}
