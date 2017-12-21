package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Site;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.LocationTextileAttribute;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Map;

public class LocationTextileValidator extends AbstractTextileValidator {
    private ICayenneService cayenneService;

    public LocationTextileValidator(ICayenneService cayenneService) {
        this.cayenneService = cayenneService;
    }

    @Override
    protected void initValidator() {
        textileType = TextileType.LOCATION;
    }

    @Override
    protected void specificTextileValidate(String tag, ValidationErrors errors) {
        Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
        String display = tagParams.get(LocationTextileAttribute.DISPLAY.getValue());
        String suburb = tagParams.get(LocationTextileAttribute.SUBURB.getValue());

        String siteId = tagParams.get(LocationTextileAttribute.SITE.getValue());

        if (siteId == null && (display == null || suburb == null)) {
            errors.addFailure(getRequiredAttrsMessage(tag), ValidationFailureType.SYNTAX);
        }

        if (siteId != null) {
            ObjectContext objectContext = cayenneService.sharedContext();
            Site site = ObjectSelect.query(Site.class).and(ExpressionFactory.inDbExp(Site.ID_PK_COLUMN, Long.valueOf(siteId))).selectFirst(objectContext);
            if (site == null) {
                errors.addFailure(String.format("tag '%s' - cannot find Site with id: %s", tag, siteId), ValidationFailureType.CONTENT_NOT_FOUND);
            }
        }
    }

    /**
     * @param tag
     * @return
     */
    public String getRequiredAttrsMessage(String tag) {
        return "The location: " + tag + " must contain the required attribute: display and suburb or site";
    }

    public String getFormatErrorMessage(String tag) {
        return "The location tag '" + tag + "' doesn't match {location display:\"display\"}";
    }
}
