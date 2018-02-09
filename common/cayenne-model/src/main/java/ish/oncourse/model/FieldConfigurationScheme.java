package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._FieldConfigurationScheme;
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
		return getFieldConfigurationLinks().stream()
				.filter(fcl -> FieldConfigurationType.APPLICATION.equals(fcl.getFieldConfiguration().getType()))
				.map(FieldConfigurationLink::getFieldConfiguration)
				.findFirst()
				.orElseGet(super::getWaitingListFieldConfiguration);
	}

	@Override
	public FieldConfiguration getEnrolFieldConfiguration() {
		return getFieldConfigurationLinks().stream()
				.filter(fcl -> FieldConfigurationType.ENROLMENT.equals(fcl.getFieldConfiguration().getType()))
				.map(FieldConfigurationLink::getFieldConfiguration)
				.findFirst()
				.orElseGet(super::getWaitingListFieldConfiguration);
	}

	@Override
	public FieldConfiguration getWaitingListFieldConfiguration() {
		return getFieldConfigurationLinks().stream()
				.filter(fcl -> FieldConfigurationType.WAITING_LIST.equals(fcl.getFieldConfiguration().getType()))
				.map(FieldConfigurationLink::getFieldConfiguration)
				.findFirst()
				.orElseGet(super::getWaitingListFieldConfiguration);
	}
}
