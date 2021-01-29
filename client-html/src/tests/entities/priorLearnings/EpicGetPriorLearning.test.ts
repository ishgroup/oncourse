import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetPriorLearning } from "../../../js/containers/entities/priorLearnings/epics/EpicGetPriorLearning";
import {
  GET_PRIOR_LEARNING_ITEM_FULFILLED,
  getPriorLearning
} from "../../../js/containers/entities/priorLearnings/actions";

describe("Get priorLearning epic tests", () => {
  it("EpicGetPriorLearning should returns correct values", () => DefaultEpic({
    action: getPriorLearning("1"),
    epic: EpicGetPriorLearning,
    processData: mockedApi => {
      const priorLearning = mockedApi.db.getPriorLearning(1);
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
    }
  }));
});
