import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_ENTITY_TAGS_REQUEST_FULFILLED,
  GET_LIST_TAGS_FULFILLED,
  getListTags
} from "../../js/containers/tags/actions";
import { EpicGetListTags } from "../../js/containers/tags/epics/EpicGetListTags";
import { SET_LIST_MENU_TAGS } from "../../js/common/components/list-view/actions";
import { getMenuTags } from "../../js/common/components/list-view/utils/listFiltersUtils";

const entityName: string = "Course";

describe("Get list tags epic tests", () => {
  it("EpicGetListTags should returns correct values", () => DefaultEpic({
    action: getListTags(entityName),
    epic: EpicGetListTags,
    processData: mockedApi => {
      const tags = mockedApi.db.getTags();
      const menuTags = getMenuTags(tags, []);

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
          payload: { tags, entityName }
        }
      ];
    }
  }));
});
