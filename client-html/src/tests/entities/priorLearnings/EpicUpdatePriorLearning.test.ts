import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdatePriorLearning } from "../../../js/containers/entities/priorLearnings/epics/EpicUpdatePriorLearning";
import {
  UPDATE_PRIOR_LEARNING_ITEM_FULFILLED,
  updatePriorLearning
} from "../../../js/containers/entities/priorLearnings/actions";

describe("Update priorLearning epic tests", () => {
  it("EpicUpdatePriorLearning should returns correct values", () => DefaultEpic({
    action: mockedApi => updatePriorLearning("1", mockedApi.db.getPriorLearning(1)),
    epic: EpicUpdatePriorLearning,
    processData: () => [
      {
        type: UPDATE_PRIOR_LEARNING_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Prior learning record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "PriorLearning", listUpdate: true, savedID: "1" }
      }
    ]
  }));
});
