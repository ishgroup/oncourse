package ish.oncourse.enrol.checkout.contact;

public interface AddContactDelegate
{
	void cancelEditing();
	void saveEditing();
	ContactCredentials getContactCredentials();
}
