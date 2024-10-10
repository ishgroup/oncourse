/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Enrolment, EnrolmentExemptionType, EnrolmentStudyReason, PaymentSource } from "@api/model";
import { mapSelectItems } from "ish-ui";

export const paymentSourceItems = Object.keys(PaymentSource).map(mapSelectItems);

export const enrolmentStudyReasonItems = Object.keys(EnrolmentStudyReason).map(mapSelectItems);

export const enrolmentExemptionTypeItems = Object.keys(EnrolmentExemptionType).map(mapSelectItems);

export const outcomeCommonFields: Array<keyof Enrolment> = ["fundingSource", "vetFundingSourceStateID", "vetPurchasingContractID", "vetPurchasingContractScheduleID"];

export const getOutcomeCommonFieldName = (field: keyof Enrolment) => {
  switch (field) {
    case "fundingSource": {
      return "Default funding source national";
    }
    case "vetFundingSourceStateID": {
      return "Default funding source - State";
    }
    case "vetPurchasingContractID": {
      return "Default purchasing contract identifier";
    }
    case "vetPurchasingContractScheduleID": {
      return "Purchasing Contract Schedule Identifier";
    }
    default:
      return "";
  }
};