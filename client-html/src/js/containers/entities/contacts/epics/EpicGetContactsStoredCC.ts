/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { format } from "date-fns-tz";
import { Epic } from "redux-observable";
import { DataResponse, PaymentMethod } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { D_MMM_YYYY } from "../../../../common/utils/dates/format";
import PreferencesService from "../../../preferences/services/PreferencesService";
import { GET_CONTACTS_STORED_CC, GET_CONTACTS_STORED_CC_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";

const getCardTypeId = (types: PaymentMethod[]): number => types.find(t => t.type === "Credit card").id;

const request: EpicUtils.Request<DataResponse, number> = {
  type: GET_CONTACTS_STORED_CC,
  hideLoadIndicator: true,
  getData: (contactId, s) => {
    const getCard = credtCardTypeId => EntityService.getPlainRecords(
      "PaymentIn",
      "creditCardNumber,creditCardType,createdOn",
      `payer.id is ${contactId} and paymentMethod.id is ${credtCardTypeId} and status is SUCCESS and billingId not is null`,
      1,
      0,
      "createdOn",
      false
    );

    const cardTypeId = s.preferences.paymentTypes ? getCardTypeId(s.preferences.paymentTypes) : null;

    if (cardTypeId) {
      return getCard(cardTypeId);
    }
    return PreferencesService.getPaymentTypes().then(types => getCard(getCardTypeId(types)));
  },
  processData: res => {
    const storedCard = res.rows.length
      ? {
        creditCardNumber: res.rows[0].values[0].replace(/\./g, "X"),
        creditCardType: res.rows[0].values[1],
        created: format(new Date(res.rows[0].values[2]), D_MMM_YYYY)
      } : null;

    return [
      {
        type: GET_CONTACTS_STORED_CC_FULFILLED,
        payload: { storedCard }
      }
    ];
  }
};

export const EpicGetContactsStoredCC: Epic<any, any> = EpicUtils.Create(request);
