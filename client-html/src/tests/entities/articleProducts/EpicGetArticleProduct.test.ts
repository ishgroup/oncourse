import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get article product epic tests", () => {
  it("EpicGetArticleProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getArticleProduct(1), "ArticleProduct"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getArticleProduct(1), "ArticleProduct", state)
  }));
});