package ish.oncourse.services.html;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class FacebookMetaProvider implements IFacebookMetaProvider {
	private static final Logger logger = LogManager.getLogger();

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private IPlainTextExtractor plainTextExtractor;


	public String getDescriptionContent(CourseClass courseClass) {
		return getDescriptionContent(courseClass.getCourse());
	}

	public String getDescriptionContent(Course course) {
		String detail = StringUtils.trimToEmpty(course.getDetail());
		return getDescriptionContent(detail, detail.length());
	}

	public String getDescriptionContent(WebNode webNode)
	{
		if (webNode != null && webNode.getWebContentVisibility().size() > 0)
			return getDescriptionContent(webNode.getWebContentVisibility().get(0).getWebContent().getContent(), 150);
		else
			return StringUtils.EMPTY;
	}

	@Override
	public String getDescriptionContent(Tag tag) {
		String detail = StringUtils.trimToEmpty(tag.getDetail());
		return getDescriptionContent(detail, detail.length());
	}

	private String getDescriptionContent(String details, int size)
	{
		try {
			String detail = textileConverter.convertCustomTextile(details,
					new ValidationErrors());
			if (detail != null) {
				String plainText = plainTextExtractor.extractFromHtml(detail);
                if (plainText.length() > size)
				    return StringUtils.abbreviate(plainText, size);
                else
                    return plainText;
			}
		} catch (Exception e) {
			logger.catching(e);
		}
		return StringUtils.EMPTY;
	}

}