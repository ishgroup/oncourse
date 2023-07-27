/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Invoice } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import InvoiceService from "../services/InvoiceService";
import { DUPLICATE_QUOTE } from "../actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { formatToDateOnly } from  "ish-ui";

const request: EpicUtils.Request = {
  type: DUPLICATE_QUOTE,
  getData: id => {
    return InvoiceService.getInvoice(id);
  },
  processData: (data: Invoice) => {
    data.invoiceLines.forEach(l => {
      l.id = null;
    });

    data.amountOwing = data.total;
    data.invoiceDate = formatToDateOnly(new Date());
    data.dateDue = formatToDateOnly(new Date());

    data.id = null;
    data.invoiceNumber = null;
    data.quoteNumber = null;
    return [initialize(LIST_EDIT_VIEW_FORM_NAME, data)];
  },
  processError: response => [...FetchErrorHandler(response, "Failed to duplicate Quote")]
};

export const EpicDuplicateQuote: Epic<any, any> = EpicUtils.Create(request);
