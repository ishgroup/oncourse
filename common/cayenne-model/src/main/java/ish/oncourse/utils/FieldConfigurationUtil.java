package ish.oncourse.utils;

import ish.common.types.FieldConfigurationType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.*;

public class FieldConfigurationUtil {
    
    public static Class<? extends FieldConfiguration> getClassByType(Integer value) {
        FieldConfigurationType type = TypesUtil.getEnumForDatabaseValue(value, FieldConfigurationType.class);

        if (type != null) {
            switch (type) {
                case APPLICATION:
                    return ApplicationFieldConfiguration.class;
                case ENROLMENT:
                    return EnrolmentFieldConfiguration.class;
                case WAITING_LIST:
                    return WaitingListFieldConfiguration.class;
                case SURVEY:
                    return SurveyFieldConfiguration.class;
                case PAYER:
                    return PayerFieldConfiguration.class;
                case PARENT:
                    return ParentFieldConfiguration.class;
                case PRODUCT:
                    return ArticleFieldConfiguration.class;
                case MEMBERSHIP:
                    return MembershipFieldConfiguration.class;
                case VOUCHER:
                    return VoucherFieldConfiguration.class;
                default:
            }
        }
        
        throw new IllegalArgumentException(String.format("Unknown FieldConfigurationType value: %s", value));
    }
}
