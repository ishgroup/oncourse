/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DELETE_PRIOR_LEARNING_ITEM, DELETE_PRIOR_LEARNING_ITEM_FULFILLED } from "../actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { initialize } from "redux-form";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import PriorLearningService from "../services/PriorLearningService";

const request: EpicUtils.Request = {
  type: DELETE_PRIOR_LEARNING_ITEM,
  getData: (id: number) => PriorLearningService.removePriorLearning(id),
  processData: () => {
    return [
      {
        type: DELETE_PRIOR_LEARNING_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Prior learning deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "PriorLearning", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, "Prior learning was not deleted")
};

export const EpicDeletePriorLearning: Epic<any, any> = EpicUtils.Create(request);
