/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import {
  GET_ADD_PAYMENT_OUT_CONTACT,
  GET_ADD_PAYMENT_OUT_VALUES,
  GET_REFUNDABLE_PAYMENTS
} from "../actions";
import EntityService from "../../../../common/services/EntityService";
import { FETCH_SUCCESS } from "../../../../common/actions";

const request: EpicUtils.Request = {
  type: GET_ADD_PAYMENT_OUT_CONTACT,
  hideLoadIndicator: true,
  getData: invoiceId => {
    return EntityService.getPlainRecords(
      "Invoice",
      "contact.id,contact.lastName,contact.firstName",
      `id == ${invoiceId}`
    ).then(res => {
      if (res && res.rows && res.rows.length) {
        return res;
      }
      return null;
    });
  },
  processData: (response: DataResponse, s, invoiceId) => {
    if (!response) {
      return [{ type: FETCH_SUCCESS }];
    }

    const { values } = response.rows[0];
    const contactId = Number(values[0]);
    const selectedInvoiceContact = {
      payeeId: contactId,
      payeeName: values[1] ? values[1] + (values[2] && values[2] !== values[1] ? `, ${values[2]}` : "") : "",
      invoiceId
    };

    return [
      {
        type: GET_ADD_PAYMENT_OUT_VALUES,
        payload: { ...selectedInvoiceContact }
      },
      {
        type: GET_REFUNDABLE_PAYMENTS,
        payload: contactId
      }
    ];
  }
};

export const EpicGetAddPaymentOutContact: Epic<any, any> = EpicUtils.Create(request);