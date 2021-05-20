import { DefaultEpic } from "../common/Default.Epic";
import { EpicCreateTag } from "../../js/containers/tags/epics/EpicCreateTag";
import { CREATE_TAG_REQUEST_FULFILLED, createTag } from "../../js/containers/tags/actions";
import { FETCH_SUCCESS } from "../../js/common/actions";

describe("Create tag epic tests", () => {
  it("EpicCreateTag should returns correct values", () => DefaultEpic({
    action: mockedApi => createTag(mockedApi.db.getTag(1)),
    epic: EpicCreateTag,
    processData: mockedApi => {
      const allTags = mockedApi.db.getTags();

      return [
        {
          type: CREATE_TAG_REQUEST_FULFILLED,
          payload: { allTags }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Tag was successfully created" }
        }
      ];
    }
  }));
});
