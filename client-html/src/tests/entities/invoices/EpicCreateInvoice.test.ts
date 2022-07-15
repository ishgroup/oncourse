import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create invoice epic tests", () => {
  it("EpicCreateInvoice should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("AbstractInvoice", { ...mockedApi.db.getInvoice(1), id: "" }),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("AbstractInvoice")
  }));
});
