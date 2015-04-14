package ish.oncourse.services.format;

import org.apache.commons.collections.Factory;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ListResourceBundle;

/**
 * A format ResourceBundle based on default locale. If format localization is
 * ever needed, language-specific bundles can override certain formats. For now
 * the bundle serves as a singleton map of format factories. Formatters
 * themselves are cached per thread in the FormatService.
 */
public class FormatBundle extends ListResourceBundle {

	static final Object[][] contents = {
			{ FormatName.TO_STRING.name(), toStringFormatFactory() },
			{ FormatName.YEAR_4DIGIT.name(), dateFormatFactory("yyyy") } };

	@Override
	protected Object[][] getContents() {
		return contents;
	}

	private static Factory dateFormatFactory(final String format) {
		return new Factory() {
			public Object create() {
				return new SimpleDateFormat(format);
			}
		};
	}

	private static Factory toStringFormatFactory() {
		return new Factory() {
			public Object create() {
				return new Format() {
					private static final long serialVersionUID = -1216760086337892665L;

					@Override
					public StringBuffer format(Object object,
							StringBuffer toAppendTo, FieldPosition pos) {
						return toAppendTo.append(String.valueOf(object));
					}

					@Override
					public Object parseObject(String source, ParsePosition pos) {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

}
