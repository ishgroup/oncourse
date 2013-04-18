package ish.oncourse.services.html;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class FacebookMetaProvider implements IFacebookMetaProvider {
	private static final Logger LOGGER = Logger.getLogger(FacebookMetaProvider.class);

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

	private String getDescriptionContent(String details, int size)
	{
		try {
			String detail = textileConverter.convertCustomTextile(details,
					new ValidationErrors());
			if (detail != null) {
				String plainText = plainTextExtractor.extractFromHtml(detail);
				return StringUtils.abbreviate(plainText, size);
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
		return StringUtils.EMPTY;
	}

}