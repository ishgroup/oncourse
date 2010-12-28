package ish.oncourse.services.tag;

import java.util.List;

import ish.oncourse.model.Tag;

public interface ITagService {

	Tag getRootTag();

	List<Tag> loadByIds(Object... ids);

	Tag getSubTagByName(String name);

	List<Long> getEntityIdsByTagName(String tagName, String entityName);

	List<Tag> getTagsForEntity(String entityName, Long entityId);
}
