/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.util;

import ish.common.types.FieldConfigurationType;
import ish.common.types.TypesUtil;
import ish.oncourse.server.cayenne.*;

public class FieldConfigurationUtil {

    public static Class<? extends FieldConfiguration> getClassByType(Integer value) {
        var type = TypesUtil.getEnumForDatabaseValue(value, FieldConfigurationType.class);

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
                case ARTICLE:
                    return ArticleFieldConfiguration.class;
                case VOUCHER:
                    return VoucherFieldConfiguration.class;
                case MEMBERSHIP:
                    return MembershipFieldConfiguration.class;
                default:
            }
        }

        throw new IllegalArgumentException(String.format("Unknown FieldConfigurationType value: %s", value));
    }
}
