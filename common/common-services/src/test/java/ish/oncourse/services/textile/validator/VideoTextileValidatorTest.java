package ish.oncourse.services.textile.validator;

import static org.junit.Assert.*;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.VideoTextileAttributes;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class VideoTextileValidatorTest extends CommonValidatorTest {

	private static final String INCORRECT_VIDEO_TYPE = "notyoutube";
	private static final String VIDEO_TEXTILE = "{video id:\"youtubeId\" type:\"youtube\"}";

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<>();
		for (VideoTextileAttributes attr : VideoTextileAttributes.values()) {
			switch (attr) {
			case VIDEO_PARAM_HEIGHT:
				data.put(VideoTextileAttributes.VIDEO_PARAM_HEIGHT.getValue(),
						"{video height:\"30\" height:\"50\"}");
				break;
			case VIDEO_PARAM_ID:
				data.put(VideoTextileAttributes.VIDEO_PARAM_ID.getValue(),
						"{video id:\"30\" id:\"50\"}");
				break;
			case VIDEO_PARAM_TYPE:
				data.put(VideoTextileAttributes.VIDEO_PARAM_TYPE.getValue(),
						"{video type:\"type1\" type:\"type2\"}");
				break;
			case VIDEO_PARAM_WIDTH:
				data.put(VideoTextileAttributes.VIDEO_PARAM_WIDTH.getValue(),
						"{video width:\"100\" width:\"200\"}");
				break;
			}
		}
		return data;
	}

	@Override
	protected String getIncorrectFormatTextile() {
		return "{video aaaaa}";
	}

	@Override
	protected String getTextileForSmokeTest() {
		return VIDEO_TEXTILE;
	}

	@Override
	public void init() {
		errors = new ValidationErrors();
		validator = new VideoTextileValidator();
	}

	/**
	 * The "id" and "type" attrs are required for the video textile
	 */
	@Test
	public void requiredAttrsTest() {
		validator.validate(VIDEO_TEXTILE, errors);
		assertFalse(errors.hasFailures());

		String tag = "{video height=\"200\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.contains(TextileUtil.getRequiredParamErrorMessage(
				tag, VideoTextileAttributes.VIDEO_PARAM_ID.getValue())));
		assertTrue(errors.contains(TextileUtil.getRequiredParamErrorMessage(
				tag, VideoTextileAttributes.VIDEO_PARAM_TYPE.getValue())));
	}

	@Test
	public void incorrectVideoTypeTest() {
		validator.validate("{video id:\"Id\" type:\"" + INCORRECT_VIDEO_TYPE
				+ "\"}", errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.contains(((VideoTextileValidator) validator)
				.getIncorrectTypeMessage(INCORRECT_VIDEO_TYPE)));
	}

	/**
	 * Emulates the situation when there is a new line in {video}, shouldn't be
	 * any errors.
	 */
	@Test
	public void videoWithNewLineTest() {
		String tag = "{video id:\"youtubeId\" \n type:\"youtube\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());
	}
}
