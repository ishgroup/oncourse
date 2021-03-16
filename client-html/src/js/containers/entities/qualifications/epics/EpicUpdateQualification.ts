/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { Qualification } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import {
  GET_QUALIFICATION_ITEM,
  UPDATE_QUALIFICATION_ITEM,
  UPDATE_QUALIFICATION_ITEM_FULFILLED
} from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; qualification: Qualification }> = {
  type: UPDATE_QUALIFICATION_ITEM,
  getData: ({ id, qualification }) => updateEntityItemById("Qualification", id, qualification),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_QUALIFICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Qualification was updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Qualification", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView ? [] : [{
        type: GET_QUALIFICATION_ITEM,
        payload: id
      }]
    ],
  processError: (response, { qualification }) => [
      ...FetchErrorHandler(response, "Qualification was not updated"),
      initialize(LIST_EDIT_VIEW_FORM_NAME, qualification)
    ]
};

export const EpicUpdateQualification: Epic<any, any> = EpicUtils.Create(request);
