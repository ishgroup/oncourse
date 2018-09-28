package ish.oncourse.model;

import ish.common.types.SurveyVisibility;
import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.common.field.Property;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.model.auto._Survey;
import ish.oncourse.utils.QueueableObjectUtils;

public class Survey extends _Survey implements Queueable {
	private static final long serialVersionUID = -4483770644382704684L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
	
	@Override
	protected void onPostAdd() {
        setCourseScore(0);
        setTutorScore(0);
        setVenueScore(0);
        setNetPromoterScore(0);
	    setVisibility(SurveyVisibility.REVIEW);
	}


	@Property(value = FieldProperty.CUSTOM_FIELD_SURVEY, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value){
		setCustomFieldValue(key, value, SurveyCustomField.class);
	}
}
