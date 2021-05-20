import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeletePriorLearning } from "../../../js/containers/entities/priorLearnings/epics/EpicDeletePriorLearning";
import {
  DELETE_PRIOR_LEARNING_ITEM_FULFILLED,
  deletePriorLearning
} from "../../../js/containers/entities/priorLearnings/actions";

describe("Delete priorLearning epic tests", () => {
  it("EpicDeletePriorLearning should returns correct values", () => DefaultEpic({
    action: deletePriorLearning("1"),
    epic: EpicDeletePriorLearning,
    processData: () => [
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
    ]
  }));
});
