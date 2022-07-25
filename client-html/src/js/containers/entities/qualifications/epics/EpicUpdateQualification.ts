/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { Qualification } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { UPDATE_QUALIFICATION_ITEM } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { getEntityRecord } from "../../common/actions";

const request: EpicUtils.Request<any, { id: number; qualification: Qualification }> = {
  type: UPDATE_QUALIFICATION_ITEM,
  getData: ({ id, qualification }) => updateEntityItemById("Qualification", id, qualification),
  processData: (v, s, { id }) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Qualification was updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Qualification", listUpdate: true, savedID: id }
      },
      getEntityRecord(id, "Qualification")
    ],
  processError: (response, { qualification }) => [
    ...FetchErrorHandler(response, "Qualification was not updated"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, qualification)
  ]
};

export const EpicUpdateQualification: Epic<any, any> = EpicUtils.Create(request);
