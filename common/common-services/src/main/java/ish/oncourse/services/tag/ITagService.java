package ish.oncourse.services.tag;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;

import java.util.List;

/**
 * Service for manipulations with {@link Tag} entities.
 * 
 * @author ksenia
 * 
 */
public interface ITagService {

	/**
	 * Retrieves the "special" tag with name {@value Tag#SUBJECTS_TAG_NAME}.
	 * 
	 * @return
	 */
	Tag getSubjectsTag();

	/**
	 * Retrieves web visible taggroup with the given name.
	 * 
	 * @param name
	 * @return
	 */
	Tag getTagGroupByName(String name);

	/**
	 * Retrieves the list of tags with ids within the given ids.
	 * 
	 * @param ids
	 *            the given ids
	 * @return
	 */
	List<Tag> loadByIds(Object... ids);

	/**
	 * Retrieves the list of tags which the entity with given id and given name
	 * is tagged with.
	 * 
	 * @param entityName
	 * @param entityId
	 * @return
	 */
	List<Tag> getTagsForEntity(String entityName, Long entityId);

	/**
	 * Retrieves the tag with the given path. E.g. if path="Subjects/Arts", it
	 * will find the tag with name "Arts" and with parent "Subjects".<br/>
	 * If the path doesn't start with "Subjects" special tag, then it checks if
	 * the root tag belongs to "Subjects" and if no, looks for the tag group
	 * with this root name.<br/>
	 * Path can start or end with "/", or it can contain "+" and "|" symbols
	 * from url rewriting, all these are processed properly.
	 * 
	 * 
	 * @param path
	 *            the given path
	 * @return tag id the tag with the given path exists, null otherwise.
	 */
	Tag getTagByFullPath(String path);

	/**
	 * Retrieves the list of ids of entities with the given name tagged with tag
	 * of the given path.
	 * 
	 * @param tagPath
	 * @param entityName
	 * @return
	 */
	List<Long> getEntityIdsByTagPath(String tagPath, String entityName);
	
	/**
	 * Gets the browsing tag, ie the tag selected for searching of courses.
	 * @return
	 */
	Tag getBrowseTag();
	
	/**
	 * All mailing lists
	 * @return
	 */
	List<Tag> getMailingLists();

	/**
	 * Mailing lists for user
	 * @param currentUser
	 * @return
	 */
	List<Tag> getMailingListsContactSubscribed(Contact currentUser);
	
	/**
	 * Subscribe specified contact to specified mailing list.
	 * 
	 * @param contact
	 * @param mailingList
	 */
	void subscribeContactToMailingList(Contact contact, Tag mailingList);
	
	/**
	 * Unsubscribe specified contact from specified mailing list.
	 * 
	 * @param contact
	 * @param mailingList
	 */
	void unsubscribeContactFromMailingList(Contact contact, Tag mailingList);

	boolean isContactSubscribedToMailingList(Contact contact, Tag mailingList);
}
