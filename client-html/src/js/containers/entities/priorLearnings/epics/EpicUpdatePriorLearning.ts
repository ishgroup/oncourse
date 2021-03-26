import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { PriorLearning } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PRIOR_LEARNING_ITEM, UPDATE_PRIOR_LEARNING_ITEM, UPDATE_PRIOR_LEARNING_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import PriorLearningService from "../services/PriorLearningService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; priorLearning: PriorLearning }> = {
  type: UPDATE_PRIOR_LEARNING_ITEM,
  getData: ({ id, priorLearning }) => PriorLearningService.updatePriorLearning(id, priorLearning),
  processData: (v, s, { id }) => [
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
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_PRIOR_LEARNING_ITEM,
        payload: id
      }] : []
    ],
  processError: (response, { priorLearning }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, priorLearning)]
};

export const EpicUpdatePriorLearning: Epic<any, any> = EpicUtils.Create(request);
