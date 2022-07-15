import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";


describe("Create contact epic tests", () => {
  it("EpicCreateContact should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("Contact", mockedApi.db.createNewContact()),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Contact")
  }));
});
