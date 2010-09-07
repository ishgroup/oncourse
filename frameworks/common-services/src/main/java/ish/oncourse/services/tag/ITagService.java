package ish.oncourse.services.tag;

import ish.oncourse.model.Tag;

public interface ITagService {

	Tag getRootTag();

	Tag getTag(String searchProperty, Object value);
	
}
