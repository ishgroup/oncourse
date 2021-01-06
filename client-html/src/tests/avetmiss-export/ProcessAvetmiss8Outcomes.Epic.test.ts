import { DefaultEpic } from "../common/Default.Epic";
import { getAvetmiss8OutcomesStatus } from "../../js/containers/avetmiss-export/actions";
import { UPDATE_PROCESS } from "../../js/common/actions";
import { EpicProcessAvetmiss8Outcomes } from "../../js/containers/avetmiss-export/epics/EpicProcessAvetmiss8Outcomes";

describe("Process avetmiss outcomes epic tests", () => {
  it("ProcessAvetmiss8Outcomes should returns correct values", () => DefaultEpic({
    action: mockedApi => getAvetmiss8OutcomesStatus(mockedApi.db.outcomesID),
    epic: EpicProcessAvetmiss8Outcomes,
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
