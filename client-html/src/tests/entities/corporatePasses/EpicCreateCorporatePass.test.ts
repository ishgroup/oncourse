import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create corporate pass epic tests", () => {
  it("EpicCreateCorporatePass should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("CorporatePass", mockedApi.db.createNewCorporatePasses()),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("CorporatePass")
  }));
});
