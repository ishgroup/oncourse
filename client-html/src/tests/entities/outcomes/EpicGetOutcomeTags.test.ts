import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_MENU_TAGS } from "../../../js/common/components/list-view/actions";
import { getOutcomeTags } from "../../../js/containers/entities/outcomes/actions";
import { EpicGetOutcomeTags } from "../../../js/containers/entities/outcomes/epics/EpicGetOutcomeTags";
import { getMenuTags } from "../../../js/common/components/list-view/utils/listFiltersUtils";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED, GET_LIST_TAGS_FULFILLED } from "../../../js/containers/tags/actions";

describe("Get outcome tags epic tests", () => {
  it("EpicGetOutcomeTags should returns correct values", () => DefaultEpic({
    action: getOutcomeTags(),
    epic: EpicGetOutcomeTags,
    processData: mockedApi => {
      const tags = mockedApi.db.getTags();
      const menuTags = getMenuTags(tags, [], null, null, "Enrolment", "enrolment");

      return [
        {
          type: GET_LIST_TAGS_FULFILLED
        },
        {
          type: SET_LIST_MENU_TAGS,
          payload: { menuTags }
        },
        {
          type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
          payload: { tags, entityName: "Enrolment" }
        }
      ];
    }
  }));
});
