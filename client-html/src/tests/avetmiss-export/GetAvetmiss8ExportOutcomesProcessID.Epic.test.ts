import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID_FULFILLED,
  getAvetmiss8ExportOutcomesProcessID
} from "../../js/containers/avetmiss-export/actions";
import { EpicGetAvetmiss8ExportOutcomesProcessID } from "../../js/containers/avetmiss-export/epics/EpicGetAvetmiss8ExportOutcomesProcessID";

describe("Get avetmiss export outcomes process ID epic tests", () => {
  it("GetAvetmiss8ExportOutcomesProcessID should returns correct values", () => DefaultEpic({
    action: mockedAPI => getAvetmiss8ExportOutcomesProcessID(mockedAPI.db.getExportSettings()),
    epic: EpicGetAvetmiss8ExportOutcomesProcessID,
    processData: mockedApi => [
      {
        type: GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID_FULFILLED,
        payload: { outcomesID: mockedApi.db.outcomesID }
      }
    ]
  }));
});
