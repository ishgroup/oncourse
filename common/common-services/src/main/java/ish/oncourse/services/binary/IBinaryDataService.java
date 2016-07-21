package ish.oncourse.services.binary;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Document;
import ish.oncourse.model.Queueable;
import org.apache.cayenne.exp.Property;

import java.util.List;

public interface IBinaryDataService {

	<E> Document getBinaryInfo(Property<E> searchProperty, E value);

	List<Document> getAttachedFiles(Long entityIdNum, String entityIdentifier, boolean hidePrivateAttachments);

	/**
	 * The method returns  Profile Picture BinaryInfo for the contact
	 */
	Document getProfilePicture(Contact contact);
	
	String getUrl(Document binaryInfo);

	/**
	 * @return attachment which is linked to this <code>entity</code> and has a tag with this  <code>tagPath</code>
	 */
	<E extends Queueable> Document getAttachmentByTag(E entity, String tagPath);

	/**
	 * @return list of attachments which are linked to this entity
	 */
	<E extends Queueable> List<Document> getAttachments(E entity);
}
