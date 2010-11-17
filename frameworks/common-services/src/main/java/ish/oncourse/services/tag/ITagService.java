package ish.oncourse.services.tag;

import java.util.List;

import ish.oncourse.model.Tag;

public interface ITagService {

	Tag getRootTag();

	Tag getTag(String searchProperty, Object value);

	List<Tag> loadByIds(Object... ids);

	List<Tag> getTags(String searchProperty, Object value);

	Tag getSubTagByName(String name);

	List<Long> getEntityIdsByTagName(String tagName, String entityName);

}
