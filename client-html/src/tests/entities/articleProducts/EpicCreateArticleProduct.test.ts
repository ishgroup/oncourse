import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create article product epic tests", () => {
  it("EpicCreateArticleProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.mockedCreateArticleProduct(), "ArticleProduct"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("ArticleProduct")
  }));
});
