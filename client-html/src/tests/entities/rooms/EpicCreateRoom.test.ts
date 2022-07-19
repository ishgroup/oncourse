import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create room epic tests", () => {
  it("EpicCreateRoom should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.getRoom(1), "Room"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Room")
  }));
});