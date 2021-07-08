/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Lead } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { processCustomFields } from "../../customFieldTypes/utils";
import { CREATE_LEAD_ITEM, CREATE_LEAD_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import LeadService from "../services/LeadService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { lead: Lead }> = {
  type: CREATE_LEAD_ITEM,
  getData: ({ lead }) => {
    processCustomFields(lead);
    return LeadService.createLead(lead);
  },
  processData: () => [
      {
        type: CREATE_LEAD_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Lead created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Lead" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: (response, { lead }) => [
    ...FetchErrorHandler(response, "Lead was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, lead)
  ]
};

export const EpicCreateLead: Epic<any, any> = EpicUtils.Create(request);
