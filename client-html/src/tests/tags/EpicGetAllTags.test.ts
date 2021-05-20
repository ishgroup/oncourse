import { DefaultEpic } from "../common/Default.Epic";
import { EpicGetAllTags } from "../../js/containers/tags/epics/EpicGetAllTags";
import { GET_ALL_TAGS_FULFILLED, getAllTags } from "../../js/containers/tags/actions";

describe("Get all tags epic tests", () => {
  it("EpicGetAllTags should returns correct values", () => DefaultEpic({
    action: getAllTags(),
    epic: EpicGetAllTags,
    processData: mockedApi => {
      const allTags = mockedApi.db.getTags();

      return [
        {
          type: GET_ALL_TAGS_FULFILLED,
          payload: { allTags }
        }
      ];
    }
  }));
});
