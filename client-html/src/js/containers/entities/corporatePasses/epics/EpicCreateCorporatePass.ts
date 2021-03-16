/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { CorporatePass } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CREATE_CORPORATE_PASS_ITEM, CREATE_CORPORATE_PASS_ITEM_FULFILLED } from "../actions";
import CorporatePassService from "../services/CorporatePassService";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: CorporatePass;

const request: EpicUtils.Request<any, { corporatePass: CorporatePass }> = {
  type: CREATE_CORPORATE_PASS_ITEM,
  getData: payload => {
    savedItem = payload.corporatePass;
    return CorporatePassService.createCorporatePass(savedItem);
  },
  processData: () => [
      {
        type: CREATE_CORPORATE_PASS_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New CorporatePass created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CorporatePass" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: response => [
    ...FetchErrorHandler(response, "CorporatePass was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateCorporatePass: Epic<any, any> = EpicUtils.Create(request);
