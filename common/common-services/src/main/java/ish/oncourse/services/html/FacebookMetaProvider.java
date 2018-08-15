package ish.oncourse.services.html;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.content.cache.IContentCacheService;
import ish.oncourse.services.content.cache.IContentKeyFactory;
import ish.oncourse.services.content.cache.WillowContentKey;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.PersistentObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class FacebookMetaProvider implements IFacebookMetaProvider {
	private static final Logger logger = LogManager.getLogger();
	private static final int MAX_META_LENGTH = 250;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private IPlainTextExtractor plainTextExtractor;

	@Inject
	private IContentCacheService<WillowContentKey, String> contentCacheService;

	@Inject
	private IContentKeyFactory<PersistentObject, WillowContentKey> contentKeyFactory;

	public String getDescriptionContent(CourseClass courseClass) {
		return getDescriptionContent(courseClass.getCourse());
	}

	public String getDescriptionContent(Course course) {
		String detail = StringUtils.trimToEmpty(course.getDetail());
		return getDescriptionContent(course, detail, MAX_META_LENGTH);
	}

	public String getDescriptionContent(WebNode webNode)
	{
		if (webNode != null && webNode.getWebContentVisibility().size() > 0)
			return getDescriptionContent(webNode, webNode.getWebContentVisibility().get(0).getWebContent().getContent(), 150);
		else
			return StringUtils.EMPTY;
	}

	@Override
	public String getDescriptionContent(Tag tag) {
		String detail = StringUtils.trimToEmpty(tag.getDetail());
		return getDescriptionContent(tag, detail, MAX_META_LENGTH);
	}

	private String getDescriptionContent(PersistentObject key,String details, int size)
	{

		WillowContentKey cacheKey = contentKeyFactory.createKey(this.getClass().getSimpleName(), key);
		String cachedContent = contentCacheService.get(cacheKey);
		
 		if (cachedContent != null) {
			return cachedContent;
		}

		try {
			String detail = textileConverter.convertCustomTextile(details,
					new ValidationErrors());
			if (detail != null) {
				String plainText = plainTextExtractor.extractFromHtml(detail);
                if (plainText.length() > size) {
					plainText = StringUtils.abbreviate(plainText, size);
				}
				contentCacheService.put(cacheKey, plainText);
				return plainText;
			}
		} catch (Exception e) {
			logger.catching(e);
		}
		return StringUtils.EMPTY;
	}

}