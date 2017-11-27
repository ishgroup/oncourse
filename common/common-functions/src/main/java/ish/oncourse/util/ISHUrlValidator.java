package ish.oncourse.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

public class ISHUrlValidator extends UrlValidator {

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
}
