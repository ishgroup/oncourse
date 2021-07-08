/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Lead } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { processCustomFields } from "../../customFieldTypes/utils";
import { GET_LEAD_ITEM, UPDATE_LEAD_ITEM, UPDATE_LEAD_ITEM_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import LeadService from "../services/LeadService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; lead: Lead }> = {
  type: UPDATE_LEAD_ITEM,
  getData: ({ id, lead }) => {
    processCustomFields(lead);
    return LeadService.updateLead(id, lead);
  },
  processData: (v, s, { id }) => [
    {
      type: UPDATE_LEAD_ITEM_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Lead was updated" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "Lead", listUpdate: true, savedID: id }
    },
    ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
      type: GET_LEAD_ITEM,
      payload: id
    }] : []
  ],
  processError: (response, { lead }) => [
      ...FetchErrorHandler(response, "Lead was not updated"),
      initialize(LIST_EDIT_VIEW_FORM_NAME, lead)
    ]
};

export const EpicUpdateLead: Epic<any, any> = EpicUtils.Create(request);
