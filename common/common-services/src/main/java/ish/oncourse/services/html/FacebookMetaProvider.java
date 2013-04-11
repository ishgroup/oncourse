package ish.oncourse.services.html;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

public class FacebookMetaProvider implements IFacebookMetaProvider {

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private IPlainTextExtractor plainTextExtractor;


	public String getDescriptionContent(CourseClass courseClass) {
		return getDescriptionContent(courseClass.getDetail());
	}

	public String getDescriptionContent(Course course) {
		return getDescriptionContent(course.getDetail());
	}

	private String getDescriptionContent(String details)
	{
		String detail = textileConverter.convertCustomTextile(details,
				new ValidationErrors());
		if (detail != null) {
			String plainText = plainTextExtractor.extractFromHtml(detail);
			return StringUtils.abbreviate(plainText, 150);
		} else
			return StringUtils.EMPTY;
	}

}