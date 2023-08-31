/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DataResponse } from "@api/model";
import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "ish-ui";
import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { setListEditRecord } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import history from "../../../../constants/History";
import { GET_ADD_PAYMENT_OUT_VALUES, getActivePaymentOutMethods } from "../actions";
import { PaymentOutModel } from "../reducers/state";
import { getAmountToAllocate } from "../utils";

const request: EpicUtils.Request<{ dataResponse: DataResponse, formData: PaymentOutModel  }> = {
  type: GET_ADD_PAYMENT_OUT_VALUES,
  hideLoadIndicator: true,
  getData: async formData => {
    const { payeeId } = formData;

    let dataResponse = null;

    if (payeeId) {
      dataResponse = await EntityService.getPlainRecords(
        "Invoice",
        "dateDue,invoiceNumber,amountOwing",
        `contact.id == ${payeeId} and amountOwing < 0`
      );
    }

    return Promise.resolve({ formData, dataResponse });
  },
  processData: ({ dataResponse, formData }, s, { invoiceId }) => {
    if (dataResponse && dataResponse.rows.length) {
      formData.invoices = dataResponse.rows.map(({ id, values }) => ({
        id: Number(id),
        payable: false,
        dateDue: values[0],
        invoiceNumber: Number(values[1]),
        amountOwing: Number(values[2]),
        outstanding: Number(values[2])
      }));
    }

    const invoiceIndex = formData.invoices?.findIndex(i => i.id === Number(invoiceId));
    if (invoiceIndex !== -1 && formData.invoices[invoiceIndex]) {
      const amount = Math.abs(formData.invoices[invoiceIndex].amountOwing);
      const amountToAllocate = getAmountToAllocate(formData.invoices, amount);
      formData.amount = amount;
      formData.invoices[invoiceIndex].payable = true;
      formData.invoices[invoiceIndex].outstanding = formData.invoices[invoiceIndex].outstanding + amountToAllocate;
    }

    formData.datePayed = format(Date.now(), III_DD_MMM_YYYY);
    formData.dateBanked = "";

    const urlParams = new URLSearchParams(window.location.search);
    urlParams.delete("invoiceId");

    history.replace({
      pathname: history.location.pathname,
      search: decodeURIComponent(urlParams.toString())
    });

    return [
      getActivePaymentOutMethods(),
      initialize(LIST_EDIT_VIEW_FORM_NAME, formData),
      setListEditRecord(formData)
    ];
  }
};

export const EpicGetAddPaymentOutValues: Epic<any, any> = EpicUtils.Create(request);