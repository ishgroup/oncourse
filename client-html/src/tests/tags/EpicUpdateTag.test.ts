import { DefaultEpic } from "../common/Default.Epic";
import {
  getAllTags,
  updateTag
} from "../../js/containers/tags/actions";
import { FETCH_SUCCESS } from "../../js/common/actions";
import { EpicUpdateTag } from "../../js/containers/tags/epics/EpicUpdateTag";

describe("Update tag epic tests", () => {
  it("EpicUpdateTag should returns correct values", () => DefaultEpic({
    action: mockedApi => updateTag(1, mockedApi.db.getTag(1)),
    epic: EpicUpdateTag,
    processData: () => {
      return [
        {
          type: FETCH_SUCCESS,
          payload: { message: "Tag was successfully updated" }
        },
        getAllTags()
      ];
    }
  }));
});
