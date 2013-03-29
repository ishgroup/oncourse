package ish.oncourse.services.binary;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Contact;

import java.util.List;

public interface IBinaryDataService {

	/**
	 * The method deprecated to use after #11321.
	 * getBinaryInfo(BinaryInfo.NAME_PROPERTY, name) should be called instead
	 * @param refNum  - reference number.
	 * @return
	 */
	@Deprecated
	BinaryInfo getBinaryInfoByReferenceNumber(Object refNum);
	
	BinaryInfo getBinaryInfoById(Object id);
	
	BinaryInfo getBinaryInfo(String searchProperty, Object value);

	BinaryInfo getRandomImage();

	List<BinaryInfo> getAttachedFiles(Long entityIdNum, String entityIdentifier, boolean hidePrivateAttachments);

	/**
	 * The method returns  Profile Picture BinaryInfo for the contact
	 */
	BinaryInfo getProfilePicture(Contact contact);
}
