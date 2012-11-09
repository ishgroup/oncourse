package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.IDelegate;

public interface AddContactDelegate extends IDelegate
{
	void cancelEditing();
	void saveEditing();
	ContactCredentials getContactCredentials();
}
