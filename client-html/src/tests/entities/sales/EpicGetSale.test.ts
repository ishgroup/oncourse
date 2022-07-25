import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get sale epic tests", () => {
  it("EpicGetSale should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getProductItem(1), "ProductItem"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getProductItem(1), "ProductItem", state)
  }));
});