/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DELETE_LEAD_ITEM, DELETE_LEAD_ITEM_FULFILLED } from "../actions/index";
import LeadService from "../services/LeadService";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: DELETE_LEAD_ITEM,
  getData: (id: number) => LeadService.removeLead(id),
  processData: () => {
    return [
      {
        type: DELETE_LEAD_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Lead deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Lead", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, "Lead was not deleted")
};

export const EpicDeleteLead: Epic<any, any> = EpicUtils.Create(request);
