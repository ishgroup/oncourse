package ish.oncourse.services.content;

import ish.oncourse.model.WebContent;

public interface IWebContentService {
	String getParsedContent(WebContent webContent);
	WebContent getWebContent(String searchProperty, Object value);
	WebContent loadById(String id);
}
