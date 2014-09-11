package ish.oncourse.model;

import ish.oncourse.model.auto._WebUrlAlias;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class WebUrlAlias extends _WebUrlAlias {
	private static final long serialVersionUID = 8310897606553438218L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	protected void onPostAdd() {
		Date today = new Date();
		setCreated(today);
		setModified(today);
        setDefault(false);
	}

    @Override
    protected void validateForSave(ValidationResult validationResult) {
        if (getWebNode() == null && StringUtils.trimToNull(getRedirectTo()) == null) {
            validationResult.addFailure(ValidationFailure.validationFailure(this, _WebUrlAlias.WEB_NODE_PROPERTY, "WebUrlAlias should have either webNode either targetUrl."));
            return;
        }
        super.validateForSave(validationResult);
    }
}
