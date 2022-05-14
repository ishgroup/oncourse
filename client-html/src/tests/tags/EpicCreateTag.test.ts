import { DefaultEpic } from "../common/Default.Epic";
import { EpicCreateTag } from "../../js/containers/tags/epics/EpicCreateTag";
import { getAllTags, createTag } from "../../js/containers/tags/actions";
import { FETCH_SUCCESS } from "../../js/common/actions";

describe("Create tag epic tests", () => {
  it("EpicCreateTag should returns correct values", () => DefaultEpic({
    action: mockedApi => createTag(mockedApi.db.getTag(1)),
    epic: EpicCreateTag,
    processData: () => [
        {
          type: FETCH_SUCCESS,
          payload: { message: "Tag was successfully created" }
        },
        getAllTags()
      ]
  }));
});
