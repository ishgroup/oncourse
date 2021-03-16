import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { PriorLearning } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CREATE_PRIOR_LEARNING_ITEM, CREATE_PRIOR_LEARNING_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import PriorLearningService from "../services/PriorLearningService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, PriorLearning> = {
  type: CREATE_PRIOR_LEARNING_ITEM,
  getData: priorLearning => PriorLearningService.createPriorLearning(priorLearning),
  processData: () => [
      {
        type: CREATE_PRIOR_LEARNING_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Prior learning record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "PriorLearning" }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: (response, priorLearning) => [
    ...FetchErrorHandler(response, "Prior learning was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, priorLearning)
  ]
};

export const EpicCreatePriorLearning: Epic<any, any> = EpicUtils.Create(request);
