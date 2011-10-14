package ish.oncourse.services.contact;

import ish.oncourse.model.Contact;

public interface IContactService {
	Contact findByUniqueCode(String uniqueCode);
}
