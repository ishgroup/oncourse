import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_USI_SORTWARE_ID_FULFILLED,
  getUSISoftwareId
} from "../../js/containers/preferences/actions";
import { EpicGetUSISortwareId } from "../../js/containers/preferences/epics/EpicGetUSISortwareId";

describe("Get USI software id epic tests", () => {
  it("EpicGetUSISortwareId should returns correct values", () => DefaultEpic({
    action: getUSISoftwareId(),
    epic: EpicGetUSISortwareId,
    processData: mockedApi => {
      const usiId = mockedApi.db.getPreferences(["usi.softwareid"]);
      return [
        {
          type: GET_USI_SORTWARE_ID_FULFILLED,
          payload: usiId[0].valueString
        }
      ];
    }
  }));
});
