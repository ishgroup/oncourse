package ish.oncourse.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.regex.Pattern;

public class ISHUrlValidator extends UrlValidator {

    private static final String PAGE_PATH_REGEX = "/[\\^\\-a-zA-Z0-9_/]*";
    private static final Pattern PAGE_PATH_PATTERN = Pattern.compile(PAGE_PATH_REGEX);

    public ISHUrlValidator() {
        super();
    }

    public ISHUrlValidator(String[] schemes) {
        super(schemes);
    }

    public ISHUrlValidator(int options) {
        super(options);
    }

    public ISHUrlValidator(String[] schemes, int options) {
        super(schemes, options);
    }

    public boolean isValidOnlyPath(String path) {
        return StringUtils.trimToNull(path) != null && super.isValidPath(path);
    }

    public boolean isValidPagePath(String path) {
        return StringUtils.trimToNull(path) != null && PAGE_PATH_PATTERN.matcher(path).matches();
    }
}
