import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import {
  GET_PRIOR_LEARNING_ITEM,
  UPDATE_PRIOR_LEARNING_ITEM,
  UPDATE_PRIOR_LEARNING_ITEM_FULFILLED
} from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { PriorLearning } from "@api/model";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import PriorLearningService from "../services/PriorLearningService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, any, { id: number; priorLearning: PriorLearning }> = {
  type: UPDATE_PRIOR_LEARNING_ITEM,
  getData: ({ id, priorLearning }) => PriorLearningService.updatePriorLearning(id, priorLearning),
  processData: (v, s, { id }) => {
    return [
      {
        type: UPDATE_PRIOR_LEARNING_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Prior learning record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "PriorLearning", listUpdate: true, savedID: id }
      },
      {
        type: GET_PRIOR_LEARNING_ITEM,
        payload: id
      }
    ];
  },
  processError: (response, { priorLearning }) => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, priorLearning)];
  }
};

export const EpicUpdatePriorLearning: Epic<any, any> = EpicUtils.Create(request);
