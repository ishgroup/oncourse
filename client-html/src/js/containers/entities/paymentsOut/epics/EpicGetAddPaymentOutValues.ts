import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ADD_PAYMENT_OUT_VALUES, GET_ADD_PAYMENT_OUT_VALUES_FULFILLED } from "../actions";
import { DataResponse, PaymentOut } from "@api/model";
import EntityService from "../../../../common/services/EntityService";

let formData: PaymentOut;

const request: EpicUtils.Request = {
  type: GET_ADD_PAYMENT_OUT_VALUES,
  hideLoadIndicator: true,
  getData: contact => {
    const { payeeId } = contact;
    formData = contact;

    if (payeeId) {
      return EntityService.getPlainRecords(
        "Invoice",
        "dateDue,invoiceNumber,amountOwing",
        `contact.id == ${payeeId} and amountOwing < 0`
      );
    }

    return Promise.resolve(null);
  },
  processData: (response: DataResponse) => {
    if (response && response.rows.length) {
      formData.invoices = response.rows.map(({ id, values }) => ({
        id: Number(id),
        payable: false,
        dateDue: values[0],
        invoiceNumber: Number(values[1]),
        amountOwing: Number(values[2]),
        outstanding: Number(values[2])
      }));
    }

    return [
      {
        type: GET_ADD_PAYMENT_OUT_VALUES_FULFILLED,
        payload: { ...formData }
      }
    ];
  }
};

export const EpicGetAddPaymentOutValues: Epic<any, any> = EpicUtils.Create(request);
