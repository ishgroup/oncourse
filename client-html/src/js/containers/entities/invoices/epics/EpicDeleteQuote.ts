/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { FETCH_SUCCESS } from "../../../../common/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DELETE_QUOTE_ITEM } from "../actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import InvoiceService from "../services/InvoiceService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: DELETE_QUOTE_ITEM,
  getData: (id: number) => InvoiceService.deleteQuote(id),
  processData: () => ([
      {
        type: FETCH_SUCCESS,
        payload: { message: "Quote record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "AbstractInvoice", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]),
  processError: response => [...FetchErrorHandler(response)]
};

export const EpicDeleteQuote: Epic<any, any> = EpicUtils.Create(request);
