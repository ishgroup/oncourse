import { DefaultEpic } from "../common/Default.Epic";
import {
  CLEAR_AVETMISS_EXPORT_OUTCOMES,
  GET_AVETMISS8_EXPORT_RESULTS,
  GET_AVETMISS8_EXPORT_RESULTS_FULFILLED,
  GET_FUNDING_UPLOADS_REQUEST
} from "../../js/containers/avetmiss-export/actions";
import { EpicGetAvetmiss8ExportResults } from "../../js/containers/avetmiss-export/epics/EpicGetAvetmiss8ExportResults";
import { CLEAR_PROCESS } from "../../js/common/actions";

describe("Get avetmiss export results epic tests", () => {
  it("GetAvetmiss8ExportResults should returns correct values", () => DefaultEpic({
    action: mockedApi => ({
      type: GET_AVETMISS8_EXPORT_RESULTS,
      payload: mockedApi.db.outcomesID
    }),
    epic: EpicGetAvetmiss8ExportResults,
    processData: () => [
      {
        type: GET_AVETMISS8_EXPORT_RESULTS_FULFILLED,
        payload: { exported: "" }
      },
      {
        type: CLEAR_AVETMISS_EXPORT_OUTCOMES
      },
      {
        type: CLEAR_PROCESS
      },
      {
        type: GET_FUNDING_UPLOADS_REQUEST
      }
    ]
  }));
});
