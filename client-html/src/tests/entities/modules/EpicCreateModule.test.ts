import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create module epic tests", () => {
  it("EpicCreateModule should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.createAndUpdateModule(), "Module"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Module")
  }));
});
