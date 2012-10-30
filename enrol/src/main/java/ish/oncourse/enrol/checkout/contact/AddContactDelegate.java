package ish.oncourse.enrol.checkout.contact;

import java.util.Map;

public interface AddContactDelegate
{
	void cancelEditing();
	void saveEditing(Map<String,String> errors);
	ContactCredentials getContactCredentials();
}
