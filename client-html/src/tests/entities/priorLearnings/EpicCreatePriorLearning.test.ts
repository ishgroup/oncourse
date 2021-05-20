import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreatePriorLearning } from "../../../js/containers/entities/priorLearnings/epics/EpicCreatePriorLearning";
import {
  CREATE_PRIOR_LEARNING_ITEM_FULFILLED,
  createPriorLearning
} from "../../../js/containers/entities/priorLearnings/actions";

describe("Create priorLearning epic tests", () => {
  it("EpicCreatePriorLearning should returns correct values", () => DefaultEpic({
    action: mockedApi => createPriorLearning(mockedApi.db.getPriorLearning(1)),
    epic: EpicCreatePriorLearning,
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
    ]
  }));
});
