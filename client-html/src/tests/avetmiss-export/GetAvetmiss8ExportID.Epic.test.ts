import { DefaultEpic } from "../common/Default.Epic";
import { GET_AVETMISS8_EXPORT_ID, GET_AVETMISS8_EXPORT_ID_FULFILLED } from "../../js/containers/avetmiss-export/actions";
import { EpicGetAvetmiss8ExportID } from "../../js/containers/avetmiss-export/epics/EpicGetAvetmiss8ExportID";

describe("Get avetmiss export ID epic tests", () => {
  it("GetAvetmiss8ExportID should returns correct values", () => DefaultEpic({
    action: {
      type: GET_AVETMISS8_EXPORT_ID
    },
    epic: EpicGetAvetmiss8ExportID,
    processData: () => [
      {
        type: GET_AVETMISS8_EXPORT_ID_FULFILLED,
        payload: { exportID: "0e8eb6ef-5ea9-452c-bc07-396a301bef8a" }
      }
    ]
  }));
});
