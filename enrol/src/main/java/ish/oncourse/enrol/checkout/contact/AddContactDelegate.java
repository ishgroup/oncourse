package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.IDelegate;

public interface AddContactDelegate extends IDelegate
{
	void resetContact();
	void addContact();
	ContactCredentials getContactCredentials();

    String getHeaderTitle();
    String getHeaderMessage();
}
