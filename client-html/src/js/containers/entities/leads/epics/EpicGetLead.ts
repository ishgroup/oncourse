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
import { GET_LEAD_ITEM, GET_LEAD_ITEM_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import LeadService from "../services/LeadService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { getNoteItems } from "../../../../common/components/form/notes/actions";

const request: EpicUtils.Request = {
  type: GET_LEAD_ITEM,
  getData: (id: number) => LeadService.getLead(id),
  processData: (lead: Lead) => {
    return [
      {
        type: GET_LEAD_ITEM_FULFILLED
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: lead, name: lead.contactName }
      },
      getNoteItems("Lead", lead.id, LIST_EDIT_VIEW_FORM_NAME),
      initialize(LIST_EDIT_VIEW_FORM_NAME, lead),
    ];
  }
};

export const EpicGetLead: Epic<any, any> = EpicUtils.Create(request);
