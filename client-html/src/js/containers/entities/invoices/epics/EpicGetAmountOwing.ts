/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_AMOUNT_OWING, GET_AMOUNT_OWING_FULFILLED, GET_CONTRA_INVOICES } from "../actions";
import { DataResponse } from "@api/model";
import EntityService from "../../../../common/services/EntityService";
import { ContraInvoiceFormData } from "../reducers/state";

const request: EpicUtils.Request<any,  number> = {
  type: GET_AMOUNT_OWING,
  hideLoadIndicator: true,
  getData: invoiceId => {
    return EntityService.getPlainRecords(
      "AbstractInvoice",
      "contact.id,contact.lastName,contact.firstName,amountOwing",
      `id == ${invoiceId}`
    ).then(res1 => {
      if (res1 && res1.rows && res1.rows.length) {
        return res1;
      }
      return null;
    });
  },
  processData: (response: DataResponse) => {
    let invoiceToContra: ContraInvoiceFormData = null;

    const { id, values } = response.rows[0];

    const selectedInvoiceAmountOwing = parseFloat(values[3]);

    if (selectedInvoiceAmountOwing < 0) {
      const parsedAmount = -selectedInvoiceAmountOwing;

      invoiceToContra = {
        id: Number(id),
        contactId: Number(values[0]),
        contactName: values[1] ? values[1] + (values[2] && values[2] !== values[1] ? `, ${values[2]}` : "") : "",
        amountTotal: parsedAmount,
        amountLeft: parsedAmount,
        contraInvoices: []
      };
    }

    return [
      {
        type: GET_AMOUNT_OWING_FULFILLED,
        payload: { selectedInvoiceAmountOwing }
      },
      ...(invoiceToContra
        ? [
            {
              type: GET_CONTRA_INVOICES,
              payload: invoiceToContra
            }
          ]
        : [])
    ];
  }
};

export const EpicGetAmountOwing: Epic<any, any> = EpicUtils.Create(request);
