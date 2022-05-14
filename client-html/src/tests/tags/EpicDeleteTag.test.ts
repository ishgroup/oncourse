import { DefaultEpic } from "../common/Default.Epic";
import {
  getAllTags,
  deleteTag
} from "../../js/containers/tags/actions";
import { FETCH_SUCCESS } from "../../js/common/actions";
import { EpicDeleteTag } from "../../js/containers/tags/epics/EpicDeleteTag";

describe("Delete tag epic tests", () => {
  it("EpicDeleteTag should returns correct values", () => DefaultEpic({
    action: deleteTag(1),
    epic: EpicDeleteTag,
    processData: () => {
      return [
        {
          type: FETCH_SUCCESS,
          payload: { message: "Tag was successfully deleted" }
        },
        getAllTags()
      ];
    }
  }));
});
