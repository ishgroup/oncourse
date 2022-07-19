import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create discount epic tests", () => {
  it("EpicCreateDiscount should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.createAndUpdateDiscount(), "Discount"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Discount")
  }));
});
