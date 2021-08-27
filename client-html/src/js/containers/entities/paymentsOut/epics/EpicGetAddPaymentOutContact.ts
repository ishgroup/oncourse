import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import {
  GET_ADD_PAYMENT_OUT_CONTACT,
  GET_ADD_PAYMENT_OUT_CONTACT_FULFILLED,
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
      "AbstractInvoice",
      "contact.id,contact.lastName,contact.firstName",
      `id == ${invoiceId}`
    ).then(res => {
      if (res && res.rows && res.rows.length) {
        return res;
      }
      return null;
    });
  },
  processData: (response: DataResponse) => {
    if (!response) {
      return [{ type: FETCH_SUCCESS }];
    }

    const { values } = response.rows[0];
    const contactId = Number(values[0]);
    const selectedInvoiceContact = {
      payeeId: contactId,
      payeeName: values[1] ? values[1] + (values[2] && values[2] !== values[1] ? `, ${values[2]}` : "") : ""
    };

    return [
      {
        type: GET_ADD_PAYMENT_OUT_CONTACT_FULFILLED,
        payload: { ...selectedInvoiceContact }
      },
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
