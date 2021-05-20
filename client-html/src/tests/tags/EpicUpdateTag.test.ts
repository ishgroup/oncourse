import { DefaultEpic } from "../common/Default.Epic";
import {
  UPDATE_TAG_REQUEST_FULFILLED,
  updateTag
} from "../../js/containers/tags/actions";
import { FETCH_SUCCESS } from "../../js/common/actions";
import { EpicUpdateTag } from "../../js/containers/tags/epics/EpicUpdateTag";

describe("Update tag epic tests", () => {
  it("EpicUpdateTag should returns correct values", () => DefaultEpic({
    action: mockedApi => updateTag(1, mockedApi.db.getTag(1)),
    epic: EpicUpdateTag,
    processData: mockedApi => {
      const allTags = mockedApi.db.getTags();

      return [
        {
          type: UPDATE_TAG_REQUEST_FULFILLED,
          payload: { allTags }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Tag was successfully updated" }
        }
      ];
    }
  }));
});
