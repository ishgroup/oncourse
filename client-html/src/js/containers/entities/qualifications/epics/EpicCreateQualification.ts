/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import QualificationService from "../services/QualificationService";
import { CREATE_QUALIFICATION_ITEM, CREATE_QUALIFICATION_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { Qualification } from "@api/model";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: Qualification;

const request: EpicUtils.Request = {
  type: CREATE_QUALIFICATION_ITEM,
  getData: payload => {
    savedItem = payload.qualification;
    return QualificationService.createQualification(payload.qualification);
  },
  processData: () => {
    return [
      {
        type: CREATE_QUALIFICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Qualification created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Qualification" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => [
    ...FetchErrorHandler(response, "Qualification was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateQualification: Epic<any, any> = EpicUtils.Create(request);
