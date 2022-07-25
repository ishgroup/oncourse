import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get discount epic tests", () => {
  it("EpicGetDiscount should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getDiscount(1), "Discount"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getDiscount(1), "Discount", state)
  }));
});