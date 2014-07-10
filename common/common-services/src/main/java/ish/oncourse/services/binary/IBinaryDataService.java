package ish.oncourse.services.binary;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Document;

import java.util.List;

public interface IBinaryDataService {

	Document getBinaryInfo(String searchProperty, Object value);

	List<Document> getAttachedFiles(Long entityIdNum, String entityIdentifier, boolean hidePrivateAttachments);

	/**
	 * The method returns  Profile Picture BinaryInfo for the contact
	 */
	Document getProfilePicture(Contact contact);
	
	String getUrl(Document binaryInfo);
}
