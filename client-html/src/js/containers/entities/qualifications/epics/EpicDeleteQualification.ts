/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import QualificationService from "../services/QualificationService";
import { DELETE_QUALIFICATION_ITEM, DELETE_QUALIFICATION_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: DELETE_QUALIFICATION_ITEM,
  getData: (id: number) => QualificationService.removeQualification(id),
  processData: () => {
    return [
      {
        type: DELETE_QUALIFICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Qualification deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Qualification", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, "Qualification was not deleted")
};

export const EpicDeleteQualification: Epic<any, any> = EpicUtils.Create(request);
