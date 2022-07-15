import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create article product epic tests", () => {
  it("EpicCreateArticleProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("ArticleProduct", mockedApi.db.mockedCreateArticleProduct()),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("ArticleProduct")
  }));
});
