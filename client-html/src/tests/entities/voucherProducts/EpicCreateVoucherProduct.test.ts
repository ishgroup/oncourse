import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create voucherProduct epic tests", () => {
  it("EpicCreateVoucherProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("VoucherProduct", mockedApi.db.getVoucherProduct(1)),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("VoucherProduct")
  }));
});