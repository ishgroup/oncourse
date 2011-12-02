package ish.oncourse.admin.pages;

import ish.oncourse.model.WillowUser;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

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
		
		Expression exp = ExpressionFactory.matchExp(WillowUser.COLLEGE_PROPERTY, null);
		this.cmsSuperUsers = context.performQuery(new SelectQuery(WillowUser.class, exp));
	}
	
	@OnEvent(component="cmsUsersForm", value="success")
	void addSuperUser() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		WillowUser user = context.newObject(WillowUser.class);
		user.setCollege(null);
		user.setEmail(newUserEmailValue);
		user.setPassword(newUserPasswordValue);
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
