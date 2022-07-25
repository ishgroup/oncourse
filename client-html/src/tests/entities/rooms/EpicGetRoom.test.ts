import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get room epic tests", () => {
  it("EpicGetRoom should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getRoom(1), "Room"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getRoom(1), "Room", state)
  }));
});