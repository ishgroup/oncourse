/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { decimalMinus, decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";
import { GET_REFUNDABLE_PAYMENTS, GET_REFUNDABLE_PAYMENTS_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";

const request: EpicUtils.Request = {
  type: GET_REFUNDABLE_PAYMENTS,
  hideLoadIndicator: true,
  getData: contactId => {
    if (contactId) {
      return EntityService.getPlainRecords(
        "PaymentIn",
        "createdOn,gatewayReference,creditCardClientReference,amount",
        `payer.id == ${contactId} and paymentMethod.type == CREDIT_CARD and status == SUCCESS and banking.id != null and gatewayReference !== null`,
        PLAIN_LIST_MAX_PAGE_SIZE,
        0,
        "createdOn",
        false
      ).then(async response => {
        let refundablePayments = null;

        if (response && response.rows && response.rows.length) {
          refundablePayments = response.rows.map(({ id, values }) => ({
            refundableId: Number(id),
            createdOn: values[0],
            gatewayReference: values[1],
            creditCardClientReference: values[2],
            amount: Number(values[3]),
            privateNotes: values[4]
          }));
        }

        if (refundablePayments && refundablePayments.length) {
          await refundablePayments
            .map((p, i) => () => EntityService.getPlainRecords(
            "PaymentOut",
            "amount",
            `status is SUCCESS and amount > 0 and paymentInGatewayReference is "${p.gatewayReference}"`
            ).then(res => {
                if (res.rows.length) {
                  refundablePayments[i].amount = decimalMinus(
                    refundablePayments[i].amount,
                    res.rows.reduce((p, r) => decimalPlus(p, parseFloat(r.values[0])), 0)
                  );
                }
              }))
            .reduce(async (a, b) => {
            await a;
            await b();
          }, Promise.resolve());
        }

        return refundablePayments;
      });
    }

    return Promise.resolve(null);
  },
  processData: refundablePayments => [
    {
      type: GET_REFUNDABLE_PAYMENTS_FULFILLED,
      payload: refundablePayments
    }
  ]
};

export const EpicGetRefundablePayents: Epic<any, any> = EpicUtils.Create(request);