import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_GRADING_TYPES_REQUEST,
  updateGradingTypes
} from "../../../js/containers/preferences/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdateGradingTypes } from "../../../js/containers/preferences/containers/grading/epics/EpicUpdateGradingTypes";

describe("Update grading type epic tests", () => {
  it("EpicUpdateGradingTypes should returns correct values", () => DefaultEpic({
    action: mockedApi => updateGradingTypes(mockedApi.db.getGradingTypes()),
    epic: EpicUpdateGradingTypes,
    processData: () => [
      {
        type: GET_GRADING_TYPES_REQUEST,
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Grading types were successfully updated" }
      }
    ]
  }));
});
