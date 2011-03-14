package ish.oncourse.services.tag;

import java.util.List;

import ish.oncourse.model.Tag;

public interface ITagService {

	/**
	 * Retrieves the "special" tag with name "Subjects".
	 * 
	 * @return
	 */
	Tag getSubjectsTag();

	/**
	 * Retrieves taggroup with the given name.
	 * 
	 * @param name
	 * @return
	 */
	Tag getTagGroupByName(String name);

	List<Tag> loadByIds(Object... ids);

	List<Tag> getTagsForEntity(String entityName, Long entityId);

	/**
	 * Retrieves the tag with the given path. E.g. if path="Subjects/Arts", it
	 * will find the tag with name "Arts" and with parent "Subjects".<br/>
	 * If the path doesn't start with "Subjects" special tag, then it checks if
	 * the root tag belongs to "Subjects" and if no, looks for the tag group
	 * with this root name.
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
}
