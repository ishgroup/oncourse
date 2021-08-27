/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_CONTRA_INVOICES, SET_CONTRA_INVOICES } from "../actions";
import { DataResponse } from "@api/model";
import EntityService from "../../../../common/services/EntityService";
import { ContraInvoice, ContraInvoiceFormData } from "../reducers/state";
import { initialize } from "redux-form";

let savedInvoiceToContra: ContraInvoiceFormData;

const request: EpicUtils.Request<any,  ContraInvoiceFormData> = {
  type: GET_CONTRA_INVOICES,
  hideLoadIndicator: true,
  getData: invoiceToContra => {
    if (invoiceToContra) {
      savedInvoiceToContra = invoiceToContra;
      return EntityService.getPlainRecords(
        "Invoice",
        "dateDue,invoiceNumber,amountOwing",
        `contact.id == ${invoiceToContra.contactId} and amountOwing > 0`
      );
    }
    return Promise.resolve(null);
  },
  processData: (response: DataResponse) => {
    let contraInvoices: ContraInvoice[] = null;
    let invoiceToContra: ContraInvoiceFormData = null;

    if (response) {
      invoiceToContra = savedInvoiceToContra;

      if (response.rows.length) {
        contraInvoices = response.rows.map(({ id, values }) => ({
          id: Number(id),
          includeInPayment: false,
          dueDate: values[0],
          invoiceNumber: Number(values[1]),
          owing: Number(values[2]),
          toBePaid: 0
        }));
      }
    }

    return [
      {
        type: SET_CONTRA_INVOICES,
        payload: { contraInvoices }
      },

      ...(contraInvoices && invoiceToContra
        ? [initialize("ContraInvoiceForm", { ...invoiceToContra, contraInvoices })]
        : [])
    ];
  }
};

export const EpicGetContraInvoices: Epic<any, any> = EpicUtils.Create(request);
