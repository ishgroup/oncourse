package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._FieldConfigurationScheme;
import ish.oncourse.utils.FieldConfigurationSchemeUtil;
import ish.oncourse.utils.QueueableObjectUtils;

public class FieldConfigurationScheme extends _FieldConfigurationScheme implements Queueable {

    private static final long serialVersionUID = 1L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	public FieldConfiguration getApplicationFieldConfiguration() {
		return FieldConfigurationSchemeUtil.getConfiguration(this, FieldConfigurationType.APPLICATION, super.getApplicationFieldConfiguration());
	}
	
	@Override
	public FieldConfiguration getEnrolFieldConfiguration() {
		return FieldConfigurationSchemeUtil.getConfiguration(this, FieldConfigurationType.ENROLMENT, super.getEnrolFieldConfiguration());
	}

	@Override
	public FieldConfiguration getWaitingListFieldConfiguration() {
		return FieldConfigurationSchemeUtil.getConfiguration(this, FieldConfigurationType.WAITING_LIST, super.getWaitingListFieldConfiguration());
	}
}
