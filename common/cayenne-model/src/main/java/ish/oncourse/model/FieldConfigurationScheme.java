package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._FieldConfigurationScheme;
import ish.oncourse.utils.FieldConfigurationSchemeUtil;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

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

	public List<FieldConfiguration> getSurveyFieldConfigurations() {
		return this.getFieldConfigurationLinks().stream()
				.filter(fcl -> FieldConfigurationType.SURVEY.equals(fcl.getFieldConfiguration().getType()))
				.map(FieldConfigurationLink::getFieldConfiguration)
				.collect(Collectors.toList());
	}

	public FieldConfiguration getPayerFieldConfiguration() {
		return FieldConfigurationSchemeUtil.getConfiguration(this, FieldConfigurationType.PAYER, null);
	}

	public FieldConfiguration getParentFieldConfiguration() {
		return FieldConfigurationSchemeUtil.getConfiguration(this, FieldConfigurationType.PARENT, null);
	}

	public FieldConfiguration getArticleFieldConfiguration() {
		return FieldConfigurationSchemeUtil.getConfiguration(this, FieldConfigurationType.ARTICLE, null);
	}

	public FieldConfiguration getMembershipFieldConfiguration() {
		return FieldConfigurationSchemeUtil.getConfiguration(this, FieldConfigurationType.MEMBERSHIP, null);
	}

	public FieldConfiguration getVoucherFieldConfiguration() {
		return FieldConfigurationSchemeUtil.getConfiguration(this, FieldConfigurationType.VOUCHER, null);
	}
}
