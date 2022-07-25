import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get voucherProduct epic tests", () => {
  it("EpicGetVoucherProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getVoucherProduct(1), "VoucherProduct"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getVoucherProduct(1), "VoucherProduct", state)
  }));
});