import { DefaultEpic } from "../common/Default.Epic";
import { getAvetmiss8ExportStatus } from "../../js/containers/avetmiss-export/actions";
import { EpicProcessAvetmiss8Export } from "../../js/containers/avetmiss-export/epics/EpicProcessAvetmiss8Export";
import { UPDATE_PROCESS } from "../../js/common/actions";

describe("Process avetmiss exports epic tests", () => {
  it("ProcessAvetmiss8Export should returns correct values", () => DefaultEpic({
    action: mockedApi => getAvetmiss8ExportStatus(mockedApi.db.outcomesID),
    epic: EpicProcessAvetmiss8Export,
    processData: mockedApi => {
      const process = mockedApi.db.getExportProcess();
      return [
        {
          type: UPDATE_PROCESS,
          payload: { process }
        }
      ];
    }
  }));
});
