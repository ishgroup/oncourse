import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_GRADING_TYPES_FULFILLED,
  getGradingTypes
} from "../../../js/containers/preferences/actions";
import { EpicGetGradingTypes } from "../../../js/containers/preferences/containers/grading/epics/EpicGetGradingTypes";

describe("Get grading types epic tests", () => {
  it("EpicGetGradingTypes should returns correct values", () => DefaultEpic({
    action: getGradingTypes(),
    epic: EpicGetGradingTypes,
    processData: mockedApi => {
      const types = mockedApi.db.getGradingTypes();

      return [
        {
          type: GET_GRADING_TYPES_FULFILLED,
          payload: types
        }
      ];
    }
  }));
});
