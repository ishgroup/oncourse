import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PRIOR_LEARNING_ITEM, GET_PRIOR_LEARNING_ITEM_FULFILLED } from "../actions";
import { PriorLearning } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import PriorLearningService from "../services/PriorLearningService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_PRIOR_LEARNING_ITEM,
  getData: (id: number) => PriorLearningService.getPriorLearning(id),
  processData: (priorLearning: PriorLearning) => {
    return [
      {
        type: GET_PRIOR_LEARNING_ITEM_FULFILLED,
        payload: { priorLearning }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: priorLearning, name: priorLearning.title }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, priorLearning)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetPriorLearning: Epic<any, any> = EpicUtils.Create(request);
