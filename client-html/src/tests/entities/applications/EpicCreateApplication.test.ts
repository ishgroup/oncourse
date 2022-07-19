
import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create application epic tests", () => {
  it("EpicCreateApplication should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.mockedCreateApplication(), "Application"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Application")
  }));
});
