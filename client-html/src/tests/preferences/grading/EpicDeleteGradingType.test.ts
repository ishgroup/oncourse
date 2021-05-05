import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  deleteGradingType, GET_GRADING_TYPES_REQUEST
} from "../../../js/containers/preferences/actions";
import { EpicDeleteGradingType } from "../../../js/containers/preferences/containers/grading/epics/EpicDeleteGradingType";

const id = 1;

describe("Delete grading type epic tests", () => {
  it("EpicDeleteGradingType should returns correct values", () => DefaultEpic({
    action: deleteGradingType(id),
    epic: EpicDeleteGradingType,
    processData: () => [
      {
        type: GET_GRADING_TYPES_REQUEST,
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Grading type was successfully deleted" }
      }
    ]
  }));
});
