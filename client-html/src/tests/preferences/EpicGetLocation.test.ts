import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_LOCATION_FULFILLED,
  getLocation
} from "../../js/containers/preferences/actions";
import { EpicGetLocation } from "../../js/containers/preferences/epics/EpicGetLocation";

describe("Get currency epic tests", () => {
  it("EpicGetLocation should returns correct values", () => DefaultEpic({
    action: getLocation(),
    epic: EpicGetLocation,
    processData: mockedApi => {
      const location = mockedApi.db.location;
      return [
        {
          type: GET_LOCATION_FULFILLED,
          payload: location
        }
      ];
    }
  }));
});
